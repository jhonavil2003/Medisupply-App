package com.misw.medisupply.domain.model.stock

/**
 * Multiple Stock Levels Response
 * Represents stock information for multiple products
 */
data class MultipleStockLevels(
    val products: List<StockLevel>,
    val totalProducts: Int
) {
    /**
     * Get stock for a specific SKU
     */
    fun getStockForSku(sku: String): StockLevel? {
        return products.find { it.productSku.equals(sku, ignoreCase = true) }
    }
    
    /**
     * Get all products that are out of stock
     */
    fun getOutOfStockProducts(): List<StockLevel> {
        return products.filter { it.isOutOfStock() }
    }
    
    /**
     * Get all products with low stock
     */
    fun getLowStockProducts(): List<StockLevel> {
        return products.filter { it.hasLowStock() }
    }
    
    /**
     * Check if all products have sufficient stock
     */
    fun allProductsHaveStock(quantities: Map<String, Int>): Boolean {
        return quantities.all { (sku, quantity) ->
            val stock = getStockForSku(sku)
            stock?.hasSufficientStock(quantity) ?: false
        }
    }
}
