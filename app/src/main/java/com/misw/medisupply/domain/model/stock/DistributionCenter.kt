package com.misw.medisupply.domain.model.stock

/**
 * Distribution Center domain model
 * Represents a warehouse or distribution center
 */
data class DistributionCenter(
    val id: Int,
    val code: String,
    val name: String,
    val city: String,
    val quantityAvailable: Int,
    val quantityReserved: Int? = null,
    val quantityInTransit: Int? = null,
    val isLowStock: Boolean,
    val isOutOfStock: Boolean
) {
    /**
     * Get total quantity including reserved and in transit
     */
    fun getTotalQuantity(): Int {
        return quantityAvailable + (quantityReserved ?: 0) + (quantityInTransit ?: 0)
    }
    
    /**
     * Check if has sufficient stock
     */
    fun hasSufficientStock(requestedQuantity: Int): Boolean {
        return quantityAvailable >= requestedQuantity
    }
    
    /**
     * Get stock status text
     */
    fun getStockStatus(): String = when {
        isOutOfStock -> "Sin stock"
        isLowStock -> "Stock bajo"
        else -> "Disponible"
    }
}
