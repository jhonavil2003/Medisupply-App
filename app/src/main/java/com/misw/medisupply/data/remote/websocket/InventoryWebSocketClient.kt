package com.misw.medisupply.data.remote.websocket

import android.util.Log
import com.misw.medisupply.BuildConfig
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.net.URISyntaxException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WebSocket client for real-time inventory updates
 * Manages connection to logistics service WebSocket server
 * and handles stock update notifications
 */
@Singleton
class InventoryWebSocketClient @Inject constructor() {
    
    companion object {
        private const val TAG = "InventoryWebSocket"
        // URL del WebSocket - usa BuildConfig para diferentes entornos
        // Debug: http://10.0.2.2:3002 (localhost del emulador)
        // Release: URL de AWS desde build.gradle.kts
        private val WEBSOCKET_URL = BuildConfig.WEBSOCKET_URL
        private const val SOCKET_PATH = "/socket.io/"
    }
    
    private var socket: Socket? = null
    
    // Estado de conexión observable
    private val _connectionState = MutableStateFlow(WebSocketState.DISCONNECTED)
    val connectionState: StateFlow<WebSocketState> = _connectionState.asStateFlow()
    
    // Eventos de stock observables
    private val _stockEvents = MutableStateFlow<WebSocketEvent?>(null)
    val stockEvents: StateFlow<WebSocketEvent?> = _stockEvents.asStateFlow()
    
    // Productos actualmente suscritos
    private val subscribedProducts = mutableSetOf<String>()
    
    /**
     * Inicializa y conecta el WebSocket
     */
    fun connect() {
        if (socket?.connected() == true) {
            Log.d(TAG, "Ya conectado al WebSocket")
            return
        }
        
        try {
            _connectionState.value = WebSocketState.CONNECTING
            
            val options = IO.Options().apply {
                path = SOCKET_PATH
                transports = arrayOf("websocket", "polling")
                reconnection = true
                reconnectionAttempts = 5
                reconnectionDelay = 1000
                timeout = 10000
            }
            
            socket = IO.socket(WEBSOCKET_URL, options)
            registerEventListeners()
            socket?.connect()
            
            Log.i(TAG, "Conectando a WebSocket: $WEBSOCKET_URL")
            
        } catch (e: URISyntaxException) {
            Log.e(TAG, "Error en URI del WebSocket", e)
            _connectionState.value = WebSocketState.ERROR
            _stockEvents.value = WebSocketEvent.Error("URL inválida: ${e.message}")
        }
    }
    
    /**
     * Desconecta el WebSocket
     */
    fun disconnect() {
        socket?.disconnect()
        socket?.off() // Remover todos los listeners
        socket = null
        subscribedProducts.clear()
        _connectionState.value = WebSocketState.DISCONNECTED
        Log.i(TAG, "WebSocket desconectado")
    }
    
    /**
     * Suscribirse a actualizaciones de productos específicos
     */
    fun subscribeToProducts(productSkus: List<String>) {
        if (socket?.connected() != true) {
            Log.w(TAG, "No conectado, no se puede suscribir")
            return
        }
        
        val newProducts = productSkus.filter { it !in subscribedProducts }
        
        if (newProducts.isEmpty()) {
            Log.d(TAG, "Ya suscrito a todos los productos")
            return
        }
        
        val data = JSONObject().apply {
            put("product_skus", JSONArray(newProducts))
        }
        
        socket?.emit("subscribe_products", data)
        subscribedProducts.addAll(newProducts)
        
        Log.i(TAG, "Suscrito a ${newProducts.size} productos: $newProducts")
    }
    
    /**
     * Desuscribirse de productos
     */
    fun unsubscribeFromProducts(productSkus: List<String>) {
        if (socket?.connected() != true) {
            return
        }
        
        val data = JSONObject().apply {
            put("product_skus", JSONArray(productSkus))
        }
        
        socket?.emit("unsubscribe_products", data)
        subscribedProducts.removeAll(productSkus.toSet())
        
        Log.i(TAG, "Desuscrito de ${productSkus.size} productos")
    }
    
    /**
     * Suscribirse a TODOS los productos
     */
    fun subscribeToAllProducts() {
        if (socket?.connected() != true) {
            Log.w(TAG, "No conectado")
            return
        }
        
        socket?.emit("subscribe_all_products")
        Log.i(TAG, "Suscrito a TODOS los productos")
    }
    
    /**
     * Enviar ping para mantener conexión
     */
    fun ping() {
        socket?.emit("ping")
    }
    
    // ============================================
    // Event Listeners
    // ============================================
    
    private fun registerEventListeners() {
        socket?.apply {
            on(Socket.EVENT_CONNECT, onConnect)
            on(Socket.EVENT_DISCONNECT, onDisconnect)
            on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            on("connection_established", onConnectionEstablished)
            on("stock_updated", onStockUpdated)
            on("subscribed", onSubscribed)
            on("subscribed_all", onSubscribedAll)
            on("unsubscribed", onUnsubscribed)
            on("error", onError)
            on("pong", onPong)
        }
    }
    
    private val onConnect = Emitter.Listener {
        Log.i(TAG, "✅ Conectado al servidor WebSocket")
        _connectionState.value = WebSocketState.CONNECTED
    }
    
    private val onDisconnect = Emitter.Listener { args ->
        val reason = args.getOrNull(0)?.toString() ?: "Unknown"
        Log.w(TAG, "🔌 Desconectado del servidor: $reason")
        _connectionState.value = WebSocketState.DISCONNECTED
        _stockEvents.value = WebSocketEvent.Disconnected(reason)
        subscribedProducts.clear()
    }
    
    private val onConnectError = Emitter.Listener { args ->
        val error = args.getOrNull(0)?.toString() ?: "Unknown error"
        Log.e(TAG, "❌ Error de conexión: $error")
        _connectionState.value = WebSocketState.ERROR
        _stockEvents.value = WebSocketEvent.Error("Error de conexión: $error")
    }
    
    private val onConnectionEstablished = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val message = data.optString("message", "Conectado")
            Log.i(TAG, "🔗 $message")
            _stockEvents.value = WebSocketEvent.Connected(message)
        } catch (e: Exception) {
            Log.e(TAG, "Error procesando connection_established", e)
        }
    }
    
    private val onStockUpdated = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val productSku = data.getString("product_sku")
            val changeType = data.getString("change_type")
            val timestamp = data.getString("timestamp")
            val stockDataJson = data.getJSONObject("stock_data")
            
            val stockData = parseStockData(stockDataJson)
            
            val event = WebSocketEvent.StockUpdated(
                productSku = productSku,
                changeType = changeType,
                timestamp = timestamp,
                stockData = stockData
            )
            
            _stockEvents.value = event
            
            Log.i(TAG, "📦 Stock actualizado: $productSku - $changeType (${stockData.totalAvailable} disponibles)")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error procesando stock_updated", e)
        }
    }
    
    private val onSubscribed = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val productSkusArray = data.getJSONArray("product_skus")
            val message = data.getString("message")
            
            val skusList = mutableListOf<String>()
            for (i in 0 until productSkusArray.length()) {
                skusList.add(productSkusArray.getString(i))
            }
            
            _stockEvents.value = WebSocketEvent.Subscribed(skusList, message)
            
            Log.i(TAG, "✅ Suscrito correctamente: $message")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error procesando subscribed", e)
        }
    }
    
    private val onSubscribedAll = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val message = data.getString("message")
            Log.i(TAG, "✅ Suscrito a todos los productos: $message")
        } catch (e: Exception) {
            Log.e(TAG, "Error procesando subscribed_all", e)
        }
    }
    
    private val onUnsubscribed = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val productSkusArray = data.getJSONArray("product_skus")
            Log.i(TAG, "✅ Desuscrito correctamente de ${productSkusArray.length()} productos")
        } catch (e: Exception) {
            Log.e(TAG, "Error procesando unsubscribed", e)
        }
    }
    
    private val onError = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val message = data.getString("message")
            Log.e(TAG, "❌ Error del servidor: $message")
            _stockEvents.value = WebSocketEvent.Error(message)
        } catch (e: Exception) {
            Log.e(TAG, "Error procesando error event", e)
        }
    }
    
    private val onPong = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val timestamp = data.getString("timestamp")
            Log.d(TAG, "🏓 Pong recibido: $timestamp")
        } catch (e: Exception) {
            Log.e(TAG, "Error procesando pong", e)
        }
    }
    
    // ============================================
    // Helpers
    // ============================================
    
    private fun parseStockData(json: JSONObject): StockUpdateData {
        val distributionCenters = mutableListOf<DistributionCenterStock>()
        val centersArray = json.optJSONArray("distribution_centers")
        
        centersArray?.let {
            for (i in 0 until it.length()) {
                val centerJson = it.getJSONObject(i)
                distributionCenters.add(
                    DistributionCenterStock(
                        distributionCenterId = centerJson.getInt("distribution_center_id"),
                        distributionCenterCode = centerJson.getString("distribution_center_code"),
                        distributionCenterName = centerJson.optString("distribution_center_name"),
                        city = centerJson.optString("city"),
                        quantityAvailable = centerJson.getInt("quantity_available"),
                        isLowStock = centerJson.getBoolean("is_low_stock"),
                        isOutOfStock = centerJson.getBoolean("is_out_of_stock")
                    )
                )
            }
        }
        
        return StockUpdateData(
            productSku = json.getString("product_sku"),
            totalAvailable = json.getInt("total_available"),
            totalReserved = json.optInt("total_reserved", 0),
            totalInTransit = json.optInt("total_in_transit", 0),
            distributionCenters = distributionCenters,
            quantityChange = json.optInt("quantity_change"),
            previousQuantity = json.optInt("previous_quantity"),
            newQuantity = json.optInt("new_quantity")
        )
    }
    
    fun isConnected(): Boolean = socket?.connected() == true
}
