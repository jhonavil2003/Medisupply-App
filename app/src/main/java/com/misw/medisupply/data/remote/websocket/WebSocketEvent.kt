package com.misw.medisupply.data.remote.websocket

/**
 * WebSocket events received from the backend
 * Sealed class hierarchy for type-safe event handling
 */
sealed class WebSocketEvent {
    /**
     * Connection established successfully
     */
    data class Connected(val message: String) : WebSocketEvent()
    
    /**
     * Disconnection occurred
     */
    data class Disconnected(val reason: String) : WebSocketEvent()
    
    /**
     * Stock update notification received
     * This is the main event for real-time inventory updates
     */
    data class StockUpdated(
        val productSku: String,
        val changeType: String,
        val timestamp: String,
        val stockData: StockUpdateData
    ) : WebSocketEvent()
    
    /**
     * Successfully subscribed to product updates
     */
    data class Subscribed(
        val productSkus: List<String>,
        val message: String
    ) : WebSocketEvent()
    
    /**
     * Error occurred
     */
    data class Error(val message: String) : WebSocketEvent()
}

/**
 * Stock update data from WebSocket notification
 * Now includes cart reservation information
 */
data class StockUpdateData(
    val productSku: String,
    val totalAvailable: Int,
    val totalReserved: Int,
    val totalInTransit: Int,
    val totalCartReserved: Int = 0, // New field for cart reservations
    val distributionCenters: List<DistributionCenterStock>,
    val quantityChange: Int?,
    val previousQuantity: Int?,
    val newQuantity: Int?
)

/**
 * Distribution center stock information
 */
data class DistributionCenterStock(
    val distributionCenterId: Int,
    val distributionCenterCode: String,
    val distributionCenterName: String?,
    val city: String?,
    val quantityAvailable: Int,
    val isLowStock: Boolean,
    val isOutOfStock: Boolean
)
