package com.misw.medisupply.domain.model.order

import java.util.Date

/**
 * Domain model representing an Order Item
 */
data class OrderItem(
    val id: Int,
    val orderId: Int,
    val productSku: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val discountPercentage: Double,
    val discountAmount: Double,
    val taxPercentage: Double,
    val taxAmount: Double,
    val subtotal: Double,
    val total: Double,
    val distributionCenterCode: String,
    val stockConfirmed: Boolean,
    val stockConfirmationDate: Date?,
    val createdAt: Date
) {
    /**
     * Get formatted unit price
     */
    fun getFormattedUnitPrice(): String {
        return "$ ${String.format("%,.2f", unitPrice)}"
    }
    
    /**
     * Get formatted total
     */
    fun getFormattedTotal(): String {
        return "$ ${String.format("%,.2f", total)}"
    }
    
    /**
     * Get formatted subtotal
     */
    fun getFormattedSubtotal(): String {
        return "$ ${String.format("%,.2f", subtotal)}"
    }
    
    /**
     * Check if has discount
     */
    fun hasDiscount(): Boolean {
        return discountPercentage > 0.0 || discountAmount > 0.0
    }
    
    /**
     * Get discount text
     */
    fun getDiscountText(): String {
        return if (hasDiscount()) {
            "-${String.format("%.0f", discountPercentage)}% (${String.format("$%,.2f", discountAmount)})"
        } else {
            "Sin descuento"
        }
    }
    
    /**
     * Get stock status text
     */
    fun getStockStatusText(): String {
        return if (stockConfirmed) {
            "Stock confirmado"
        } else {
            "Pendiente de confirmar"
        }
    }
}
