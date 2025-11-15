package com.misw.medisupply.presentation.salesforce.viewmodel.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.remote.websocket.InventoryWebSocketClient
import com.misw.medisupply.data.remote.websocket.WebSocketEvent
import com.misw.medisupply.data.remote.websocket.WebSocketState
import com.misw.medisupply.domain.model.order.CartItem
import com.misw.medisupply.domain.model.product.Pagination
import com.misw.medisupply.domain.model.product.Product
import com.misw.medisupply.domain.model.stock.DistributionCenter
import com.misw.medisupply.domain.model.stock.StockLevel
import com.misw.medisupply.domain.usecase.cart.ClearCartUseCase
import com.misw.medisupply.domain.usecase.cart.ReleaseStockUseCase
import com.misw.medisupply.domain.usecase.cart.ReserveStockUseCase
import com.misw.medisupply.domain.usecase.product.GetProductsUseCase
import com.misw.medisupply.domain.usecase.stock.GetMultipleProductsStockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Cache configuration for products catalog
 */
private const val PRODUCTS_CACHE_DURATION_MS = 3_600_000L // 1 hour

/**
 * ViewModel for Products Screen
 * Manages product catalog state, filtering and search with intelligent caching
 * Stock is always loaded fresh (no cache)
 * Includes real-time stock updates via WebSocket
 * Includes cart stock reservation system
 */
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getMultipleProductsStockUseCase: GetMultipleProductsStockUseCase,
    private val reserveStockUseCase: ReserveStockUseCase,
    private val releaseStockUseCase: ReleaseStockUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val webSocketClient: InventoryWebSocketClient
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsState())
    val state: StateFlow<ProductsState> = _state.asStateFlow()
    
    // WebSocket connection state
    val connectionState: StateFlow<WebSocketState> = webSocketClient.connectionState
    
    private var lastProductsLoadTime: Long = 0
    
    private var stockLoadingJob: Job? = null
    private var currentProductSkus: List<String> = emptyList()

    init {
        loadProducts()
        initializeWebSocket()
    }
    
    // ============================================================================
    // WebSocket Real-Time Updates
    // ============================================================================
    
    /**
     * Initializes WebSocket connection and event listeners
     */
    private fun initializeWebSocket() {
        // Connect to WebSocket server
        webSocketClient.connect()
        
        // Listen to connection state changes
        viewModelScope.launch {
            webSocketClient.connectionState.collect { state ->
                when (state) {
                    WebSocketState.CONNECTED -> {
                        println("🟢 WebSocket conectado")
                        // Subscribe to visible products when connected
                        subscribeToVisibleProducts()
                    }
                    WebSocketState.DISCONNECTED -> {
                        println("🔴 WebSocket desconectado")
                    }
                    WebSocketState.ERROR -> {
                        println("❌ WebSocket error")
                    }
                    WebSocketState.CONNECTING -> {
                        println("🟡 WebSocket conectando...")
                    }
                    WebSocketState.RECONNECTING -> {
                        println("🔄 WebSocket reconectando...")
                    }
                }
            }
        }
        
        // Listen to stock update events
        viewModelScope.launch {
            webSocketClient.stockEvents.collect { event ->
                when (event) {
                    is WebSocketEvent.StockUpdated -> {
                        handleStockUpdate(event)
                    }
                    is WebSocketEvent.Connected -> {
                        println("✅ WebSocket: ${event.message}")
                    }
                    is WebSocketEvent.Subscribed -> {
                        println("📡 Suscrito a ${event.productSkus.size} productos")
                    }
                    is WebSocketEvent.Error -> {
                        println("⚠️ WebSocket Error: ${event.message}")
                    }
                    is WebSocketEvent.Disconnected -> {
                        println("🔌 Desconectado: ${event.reason}")
                    }
                    null -> {
                        // No event yet
                    }
                }
            }
        }
    }
    
    /**
     * Handles real-time stock update from WebSocket
     */
    private fun handleStockUpdate(event: WebSocketEvent.StockUpdated) {
        val normalizedSku = normalizedSku(event.productSku)
        
        println("📦 Actualización de stock recibida:")
        println("   SKU: ${event.productSku} (normalizado: $normalizedSku)")
        println("   Tipo: ${event.changeType}")
        println("   Disponible: ${event.stockData.totalAvailable}")
        println("   Cambio: ${event.stockData.quantityChange}")
        
        // Update stock in the map
        _state.update { currentState ->
            val updatedStockMap = currentState.productStockMap.toMutableMap()
            
            // Convert WebSocket stock data to StockLevel domain model
            val updatedStock = StockLevel(
                productSku = event.stockData.productSku,
                totalAvailable = event.stockData.totalAvailable,
                totalReserved = event.stockData.totalReserved,
                totalInTransit = event.stockData.totalInTransit,
                distributionCenters = event.stockData.distributionCenters.map { dc ->
                    DistributionCenter(
                        id = dc.distributionCenterId,
                        code = dc.distributionCenterCode,
                        name = dc.distributionCenterName ?: "N/A",
                        city = dc.city ?: "N/A",
                        quantityAvailable = dc.quantityAvailable,
                        quantityReserved = null,
                        quantityInTransit = null,
                        isLowStock = dc.isLowStock,
                        isOutOfStock = dc.isOutOfStock
                    )
                }
            )
            
            updatedStockMap[normalizedSku] = updatedStock
            
            println("✅ Stock actualizado en el mapa")
            
            currentState.copy(productStockMap = updatedStockMap)
        }
        
        // Check if any cart items are affected
        checkCartItemsStock(event)
    }
    
    /**
     * Verifies if cart items are affected by stock changes
     */
    private fun checkCartItemsStock(event: WebSocketEvent.StockUpdated) {
        val normalizedSku = normalizedSku(event.productSku)
        val cartItem = _state.value.cartItems[normalizedSku]
        
        if (cartItem != null) {
            val newStockAvailable = event.stockData.totalAvailable
            
            println("⚠️ Producto en carrito afectado:")
            println("   Nombre: ${cartItem.productName}")
            println("   Cantidad en carrito: ${cartItem.quantity}")
            println("   Stock disponible: $newStockAvailable")
            
            // Update cart item with new stock info
            _state.update { currentState ->
                val updatedCart = currentState.cartItems.toMutableMap()
                
                if (newStockAvailable < cartItem.quantity) {
                    // Adjust quantity to available stock
                    if (newStockAvailable > 0) {
                        updatedCart[normalizedSku] = cartItem.copy(
                            quantity = newStockAvailable,
                            stockAvailable = newStockAvailable
                        )
                        println("   ⚠️ Cantidad ajustada a $newStockAvailable")
                    } else {
                        // Remove from cart if no stock
                        updatedCart.remove(normalizedSku)
                        println("   ❌ Removido del carrito (sin stock)")
                    }
                } else {
                    // Just update stock info
                    updatedCart[normalizedSku] = cartItem.copy(stockAvailable = newStockAvailable)
                    println("   ✅ Stock actualizado en carrito")
                }
                
                currentState.copy(cartItems = updatedCart)
            }
        }
    }
    
    /**
     * Subscribe to visible products for real-time updates
     */
    private fun subscribeToVisibleProducts() {
        val productSkus = _state.value.products.map { it.sku }
        
        if (productSkus.isNotEmpty() && webSocketClient.isConnected()) {
            webSocketClient.subscribeToProducts(productSkus)
            println("📡 Suscrito a ${productSkus.size} productos visibles")
        }
    }
    
    /**
     * Called when products are loaded to subscribe to them
     */
    private fun onProductsLoaded(products: List<Product>) {
        if (webSocketClient.isConnected()) {
            val productSkus = products.map { it.sku }
            webSocketClient.subscribeToProducts(productSkus)
        }
    }
    
    // ============================================================================
    // Product Loading (Original Methods)
    // ============================================================================
    
    private fun normalizedSku(sku: String): String = sku.uppercase().trim()
    
    private fun isProductsCacheValid(): Boolean {
        return System.currentTimeMillis() - lastProductsLoadTime < PRODUCTS_CACHE_DURATION_MS
    }
    
    fun getCacheAge(): String {
        if (lastProductsLoadTime == 0L) return "Nunca actualizado"
        
        val ageMs = System.currentTimeMillis() - lastProductsLoadTime
        val ageSeconds = ageMs / 1000
        val ageMinutes = ageSeconds / 60
        val ageHours = ageMinutes / 60
        
        return when {
            ageSeconds < 60 -> "Hace ${ageSeconds}s"
            ageMinutes < 60 -> "Hace ${ageMinutes}m"
            else -> "Hace ${ageHours}h"
        }
    }

    /**
     * Load products with current filters
     * @param forceRefresh If true, bypass cache and reload from API
     */
    fun loadProducts(
        search: String? = _state.value.searchQuery,
        category: String? = _state.value.selectedCategory,
        page: Int = _state.value.currentPage,
        perPage: Int = 20,
        forceRefresh: Boolean = false
    ) {
        // Check cache validity if not forcing refresh
        if (!forceRefresh && 
            isProductsCacheValid() && 
            _state.value.products.isNotEmpty() &&
            search == _state.value.searchQuery &&
            category == _state.value.selectedCategory &&
            page == _state.value.currentPage
        ) {
            // Cache is valid and filters haven't changed, reload stock (always fresh)
            loadStockForProducts(_state.value.products)
            return
        }
        
        getProductsUseCase(
            search = search,
            category = category,
            requiresColdChain = null,
            isActive = true,
            page = page,
            perPage = perPage
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true, error = null) }
                }
                is Resource.Success -> {
                    val (products, pagination) = resource.data!!
                    lastProductsLoadTime = System.currentTimeMillis()
                    _state.update {
                        it.copy(
                            products = products,
                            pagination = pagination,
                            isLoading = false,
                            error = null,
                            currentPage = pagination.page
                        )
                    }
                    // Load stock for current products (always fresh)
                    loadStockForProducts(products)
                    // Subscribe to products for real-time updates
                    onProductsLoaded(products)
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = resource.message ?: "Error al cargar productos"
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query.takeIf { it.isNotBlank() }, currentPage = 1) }
        loadProducts(forceRefresh = true)  // Force refresh to bypass cache
    }

    fun onCategoryFilterChange(category: String?) {
        _state.update { it.copy(selectedCategory = category, currentPage = 1) }
        loadProducts(forceRefresh = true)  // Force refresh to bypass cache
    }

    fun clearFilters() {
        _state.update {
            it.copy(
                searchQuery = null,
                selectedCategory = null,
                currentPage = 1
            )
        }
        loadProducts(forceRefresh = true)  // Force refresh to bypass cache
    }

    fun loadNextPage() {
        val pagination = _state.value.pagination
        if (pagination?.hasNext == true) {
            loadProducts(page = pagination.page + 1)
        }
    }

    fun loadPreviousPage() {
        val pagination = _state.value.pagination
        if (pagination?.hasPrev == true && pagination.page > 1) {
            loadProducts(page = pagination.page - 1)
        }
    }

    fun refresh() {
        loadProducts(forceRefresh = true)
    }
    
    private fun loadStockForProducts(products: List<Product>) {
        if (products.isEmpty()) return
        
        stockLoadingJob?.cancel()
        
        val productSkus = products.map { normalizedSku(it.sku) }
        currentProductSkus = productSkus
        
        stockLoadingJob = viewModelScope.launch {
            loadStockWithRetry(productSkus, maxRetries = 2)
        }
    }
    
    private suspend fun loadStockWithRetry(
        productSkus: List<String>,
        maxRetries: Int = 2
    ) {
        var attempt = 0
        var lastError: String? = null
        var shouldContinue = true
        
        while (attempt < maxRetries && shouldContinue) {
            try {
                getMultipleProductsStockUseCase(
                    productSkus = productSkus,
                    includeReserved = false,
                    includeInTransit = false
                ).catch { e ->
                    // Handle flow errors
                    lastError = e.message ?: "Unknown error"
                    println("Stock loading error (attempt ${attempt + 1}/$maxRetries): $lastError")
                }.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _state.update { 
                                it.copy(
                                    isLoadingStock = true, 
                                    stockError = null,
                                    stockRetryAttempt = attempt
                                ) 
                            }
                        }
                        is Resource.Success -> {
                            if (currentProductSkus == productSkus) {
                                val stockData = resource.data!!

                                val stockMap = stockData.products.associateBy { 
                                    normalizedSku(it.productSku)
                                }
                                
                                _state.update {
                                    it.copy(
                                        productStockMap = stockMap,
                                        isLoadingStock = false,
                                        stockError = null,
                                        stockRetryAttempt = 0
                                    )
                                }
                                
                                println("✅ Stock loaded successfully: ${stockMap.size} products")
                                shouldContinue = false // Success, exit retry loop
                            } else {
                                println("⚠️ Stock data discarded (products changed)")
                                shouldContinue = false // Products changed, exit
                            }
                        }
                        is Resource.Error -> {
                            lastError = resource.message ?: "Unknown error"
                            println("Stock loading failed (attempt ${attempt + 1}/$maxRetries): $lastError")
                        }
                    }
                }
                
                if (lastError != null && shouldContinue) {
                    attempt++
                    if (attempt < maxRetries) {
                        val delayMs = 500L * attempt // 500ms, 1000ms
                        println("Retrying stock load in ${delayMs}ms...")
                        _state.update { it.copy(stockRetryAttempt = attempt) }
                        delay(delayMs)
                    }
                }
                
            } catch (e: Exception) {
                lastError = e.message ?: "Exception: ${e.javaClass.simpleName}"
                println("Stock loading exception (attempt ${attempt + 1}/$maxRetries): $lastError")
                attempt++
                if (attempt < maxRetries) {
                    val delayMs = 500L * attempt
                    delay(delayMs)
                }
            }
        }
        
        if (shouldContinue && lastError != null) {
            _state.update {
                it.copy(
                    isLoadingStock = false,
                    stockError = "No se pudo cargar el stock. ${lastError ?: "Intente nuevamente"}",
                    stockRetryAttempt = 0
                )
            }
            println("❌ Stock loading failed after $maxRetries attempts: $lastError")
        }
    }
    
    fun getStockForProduct(sku: String): StockLevel? {
        return _state.value.productStockMap[normalizedSku(sku)]
    }
    
    fun retryStockLoading() {
        val currentProducts = _state.value.products
        if (currentProducts.isNotEmpty()) {
            println("🔄 Manual retry of stock loading...")
            loadStockForProducts(currentProducts)
        }
    }
    
    // ============================================================================
    // Shopping Cart Management with Stock Reservation
    // ============================================================================
    
    /**
     * Add product to cart with backend stock reservation
     * Uses optimistic update with rollback on failure
     */
    fun addToCart(product: Product) {
        val normalizedSku = normalizedSku(product.sku)
        val stockLevel = _state.value.productStockMap[normalizedSku]
        val stockAvailable = stockLevel?.totalAvailable
        
        println("🛒 Adding to cart: ${product.name} (SKU: ${product.sku})")
        println("   Normalized SKU: $normalizedSku")
        println("   Stock Available: $stockAvailable")
        
        // 1. Local validation
        val currentCart = _state.value.cartItems
        val existingItem = currentCart[normalizedSku]
        val newQuantity = if (existingItem != null) existingItem.quantity + 1 else 1
        
        if (stockAvailable != null && newQuantity > stockAvailable) {
            _state.update { it.copy(error = "Stock insuficiente (disponible: $stockAvailable)") }
            println("   ❌ Cannot exceed stock: $newQuantity > $stockAvailable")
            return
        }
        
        // 2. Optimistic UI update
        val updatedCartItem = if (existingItem != null) {
            existingItem.copy(quantity = newQuantity)
        } else {
            CartItem(
                productSku = product.sku,
                productName = product.name,
                quantity = 1,
                unitPrice = product.unitPrice,
                stockAvailable = stockAvailable,
                requiresColdChain = product.requiresColdChain,
                category = product.category
            )
        }
        
        _state.update { currentState ->
            val updatedCart = currentState.cartItems.toMutableMap()
            updatedCart[normalizedSku] = updatedCartItem
            currentState.copy(
                cartItems = updatedCart,
                isReservingStock = true,
                error = null
            )
        }
        
        println("   ✅ Optimistic update: quantity = $newQuantity")
        
        // 3. Reserve stock in backend
        viewModelScope.launch {
            reserveStockUseCase(
                productSku = product.sku,
                quantity = 1 // Always reserve 1 unit at a time
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        println("   ✅ Stock reserved in backend: ${resource.data?.quantityReserved} units")
                        _state.update { it.copy(isReservingStock = false) }
                        // WebSocket will update the stock automatically
                    }
                    
                    is Resource.Error -> {
                        println("   ❌ Failed to reserve stock: ${resource.message}")
                        
                        // Rollback optimistic update
                        _state.update { currentState ->
                            val revertedCart = currentState.cartItems.toMutableMap()
                            
                            if (existingItem != null) {
                                // Restore previous quantity
                                revertedCart[normalizedSku] = existingItem
                            } else {
                                // Remove item that couldn't be added
                                revertedCart.remove(normalizedSku)
                            }
                            
                            currentState.copy(
                                cartItems = revertedCart,
                                isReservingStock = false,
                                error = resource.message ?: "No se pudo reservar el stock"
                            )
                        }
                    }
                    
                    is Resource.Loading -> {
                        // Already showing loading state
                    }
                }
            }
        }
    }
    
    /**
     * Remove product from cart or decrease quantity with backend stock release
     */
    fun removeFromCart(productSku: String) {
        val normalizedSku = normalizedSku(productSku)
        val cartItem = _state.value.cartItems[normalizedSku] ?: return
        
        println("🗑️ Removing from cart: ${cartItem.productName} (qty: ${cartItem.quantity})")
        
        // Determine new quantity
        val newQuantity = cartItem.quantity - 1
        val quantityToRelease = 1
        
        // Optimistic update
        _state.update { currentState ->
            val updatedCart = currentState.cartItems.toMutableMap()
            
            if (newQuantity > 0) {
                updatedCart[normalizedSku] = cartItem.copy(quantity = newQuantity)
            } else {
                updatedCart.remove(normalizedSku)
            }
            
            currentState.copy(cartItems = updatedCart)
        }
        
        // Release stock in backend
        viewModelScope.launch {
            releaseStockUseCase(
                productSku = productSku,
                quantity = quantityToRelease
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        println("   ✅ Stock released in backend: ${resource.data?.quantityReleased} units")
                        // WebSocket will update the stock automatically
                    }
                    
                    is Resource.Error -> {
                        println("   ⚠️ Failed to release stock: ${resource.message}")
                        // Don't rollback - backend will auto-release on timeout
                        // Just log the error
                    }
                    
                    is Resource.Loading -> {}
                }
            }
        }
    }
    
    fun updateCartQuantity(productSku: String, newQuantity: Int) {
        if (newQuantity < 0) return
        
        val normalizedSku = normalizedSku(productSku)
        
        _state.update { currentState ->
            val currentCart = currentState.cartItems.toMutableMap()
            val existingItem = currentCart[normalizedSku]
            
            if (existingItem != null) {
                if (newQuantity == 0) {
                    // Remove from cart
                    currentCart.remove(normalizedSku)
                } else {
                    // Check stock limit
                    val stockAvailable = existingItem.stockAvailable
                    val finalQuantity = if (stockAvailable != null) {
                        minOf(newQuantity, stockAvailable)
                    } else {
                        newQuantity
                    }
                    
                    currentCart[normalizedSku] = existingItem.copy(quantity = finalQuantity)
                }
            }
            
            currentState.copy(cartItems = currentCart)
        }
    }
    
    fun removeProductFromCart(productSku: String) {
        val normalizedSku = normalizedSku(productSku)
        
        
        _state.update { currentState ->
            val currentCart = currentState.cartItems.toMutableMap()
            currentCart.remove(normalizedSku)
            currentState.copy(cartItems = currentCart)
        }
    }
    
    /**
     * Clear all cart items and release all stock reservations
     * Call this when order is confirmed or user explicitly clears cart
     */
    fun clearCart() {
        println("🧹 Clearing cart...")
        
        // Clear UI immediately
        _state.update { it.copy(cartItems = emptyMap()) }
        
        // Release all reservations in backend
        viewModelScope.launch {
            clearCartUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        println("   ✅ Cart cleared in backend: ${resource.data?.clearedCount} reservations")
                    }
                    is Resource.Error -> {
                        println("   ⚠️ Failed to clear cart in backend: ${resource.message}")
                        // Cart is already cleared in UI, backend will auto-expire
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }
    
    /**
     * Load initial cart items (for edit mode)
     * Populates the cart with items from an existing order
     * NOTE: These items are not reserved in the cart reservation system
     * They are from an existing confirmed order
     */
    fun loadInitialCartItems(items: Map<String, CartItem>) {
        _state.update { it.copy(cartItems = items) }
    }

    fun getCartQuantity(productSku: String): Int {
        return _state.value.cartItems[normalizedSku(productSku)]?.quantity ?: 0
    }
    

    fun getCartItemsCount(): Int {
        return _state.value.cartItems.values.sumOf { it.quantity }
    }
    
    fun getCartTotal(): Float {
        return _state.value.cartItems.values.sumOf { it.calculateSubtotal().toDouble() }.toFloat()
    }
    
    fun hasItemsInCart(): Boolean {
        return _state.value.cartItems.isNotEmpty()
    }
    
    /**
     * Clean up when ViewModel is destroyed
     * Release all cart reservations
     */
    override fun onCleared() {
        super.onCleared()
        
        // Clear cart reservations before destroying
        if (_state.value.cartItems.isNotEmpty()) {
            println("🔄 ViewModel cleared - releasing cart reservations")
            // Launch in a separate scope since viewModelScope is already cancelled
            kotlinx.coroutines.GlobalScope.launch {
                clearCartUseCase().collect { /* Best effort release */ }
            }
        }
        
        webSocketClient.disconnect()
        println("🔌 WebSocket desconectado (ViewModel cleared)")
    }
}

data class ProductsState(
    val products: List<Product> = emptyList(),
    val pagination: Pagination? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String? = null,
    val selectedCategory: String? = null,
    val currentPage: Int = 1,
    val productStockMap: Map<String, StockLevel> = emptyMap(),
    val isLoadingStock: Boolean = false,
    val stockError: String? = null,
    val stockRetryAttempt: Int = 0,
    val cartItems: Map<String, CartItem> = emptyMap(),
    val isReservingStock: Boolean = false // New field for reservation loading state
)

