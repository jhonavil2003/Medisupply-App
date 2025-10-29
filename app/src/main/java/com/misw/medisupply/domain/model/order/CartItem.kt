package com.misw.medisupply.domain.model.order

import com.misw.medisupply.core.utils.FormatUtils

/**
 * Cart Item
 * Represents a product added to the shopping cart during order creation
 */
data class CartItem(
    val productSku: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Float,
    val stockAvailable: Int?,
    val requiresColdChain: Boolean = false,
    val category: String = ""
) {
    /**
     * Calculate subtotal for this cart item
     */
    fun calculateSubtotal(): Float = unitPrice * quantity
    
    /**
     * Get formatted subtotal with currency
     */
    fun getFormattedSubtotal(): String = FormatUtils.formatCurrency(calculateSubtotal(), decimals = 0)
    
    /**
     * Check if quantity exceeds available stock
     */
    fun exceedsStock(): Boolean {
        return stockAvailable?.let { quantity > it } ?: false
    }
    
    /**
     * Check if product has sufficient stock for current quantity
     */
    fun hasSufficientStock(): Boolean {
        return stockAvailable?.let { it >= quantity } ?: false
    }
    
    /**
     * Get max quantity that can be ordered based on stock
     */
    fun getMaxQuantity(): Int? = stockAvailable
}
