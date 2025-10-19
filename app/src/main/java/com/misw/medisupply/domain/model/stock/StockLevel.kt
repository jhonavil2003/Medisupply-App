package com.misw.medisupply.domain.model.stock

/**
 * Stock Level domain model
 * Represents stock information for a single product across distribution centers
 */
data class StockLevel(
    val productSku: String,
    val totalAvailable: Int,
    val totalReserved: Int? = null,
    val totalInTransit: Int? = null,
    val distributionCenters: List<DistributionCenter>
) {
    /**
     * Check if product has any stock available
     */
    fun hasStock(): Boolean = totalAvailable > 0
    
    /**
     * Check if has sufficient stock across all centers
     */
    fun hasSufficientStock(requestedQuantity: Int): Boolean {
        return totalAvailable >= requestedQuantity
    }
    
    /**
     * Get total quantity including reserved and in transit
     */
    fun getTotalQuantity(): Int {
        return totalAvailable + (totalReserved ?: 0) + (totalInTransit ?: 0)
    }
    
    /**
     * Get distribution center with most available stock
     */
    fun getCenterWithMostStock(): DistributionCenter? {
        return distributionCenters.maxByOrNull { it.quantityAvailable }
    }
    
    /**
     * Get distribution centers that can fulfill the requested quantity
     */
    fun getCentersWithSufficientStock(requestedQuantity: Int): List<DistributionCenter> {
        return distributionCenters.filter { it.hasSufficientStock(requestedQuantity) }
    }
    
    /**
     * Check if product is out of stock in all centers
     */
    fun isOutOfStock(): Boolean = totalAvailable == 0
    
    /**
     * Check if product has low stock in at least one center
     */
    fun hasLowStock(): Boolean = distributionCenters.any { it.isLowStock && !it.isOutOfStock }
    
    /**
     * Get stock status text
     */
    fun getStockStatus(): String = when {
        isOutOfStock() -> "Sin stock"
        hasLowStock() -> "Stock limitado"
        else -> "$totalAvailable unidades disponibles"
    }
}
