package com.misw.medisupply.domain.model.order

import com.misw.medisupply.core.utils.FormatUtils
import java.util.Date

/**
 * Domain model representing an Order Item
 * Each item represents a product line in an order
 */
data class OrderItem(
    val id: Int?,
    val orderId: Int?,
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
    val distributionCenterCode: String?,
    val stockConfirmed: Boolean,
    val stockConfirmationDate: Date?,
    val createdAt: Date?
) {
    /**
     * Get formatted unit price
     */
    fun getFormattedUnitPrice(): String {
        return FormatUtils.formatCurrency(unitPrice)
    }
    
    /**
     * Get formatted total
     */
    fun getFormattedTotal(): String {
        return FormatUtils.formatCurrency(total)
    }
    
    /**
     * Get formatted subtotal
     */
    fun getFormattedSubtotal(): String {
        return FormatUtils.formatCurrency(subtotal)
    }
}
