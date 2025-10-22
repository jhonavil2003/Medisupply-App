package com.misw.medisupply.domain.model.order

import com.misw.medisupply.domain.model.customer.Customer
import java.util.Date

/**
 * Domain model representing an Order
 * This model is used across the application and is independent of data sources
 */
data class Order(
    val id: Int?,
    val orderNumber: String?,
    val customerId: Int,
    val sellerId: String,
    val sellerName: String?,
    val orderDate: Date?,
    val deliveryDate: Date?,
    val status: OrderStatus,
    val subtotal: Double,
    val discountAmount: Double,
    val taxAmount: Double,
    val totalAmount: Double,
    val paymentTerms: PaymentTerms,
    val paymentMethod: PaymentMethod?,
    val deliveryAddress: String?,
    val deliveryCity: String?,
    val deliveryDepartment: String?,
    val preferredDistributionCenter: String?,
    val notes: String?,
    val createdAt: Date?,
    val updatedAt: Date?,
    val customer: Customer?,
    val items: List<OrderItem>
) {
    /**
     * Get formatted total amount
     */
    fun getFormattedTotal(): String {
        return "$ ${String.format("%,.2f", totalAmount)}"
    }
    
    /**
     * Get formatted subtotal
     */
    fun getFormattedSubtotal(): String {
        return "$ ${String.format("%,.2f", subtotal)}"
    }
    
    /**
     * Get formatted discount amount
     */
    fun getFormattedDiscount(): String {
        return "$ ${String.format("%,.2f", discountAmount)}"
    }
    
    /**
     * Get formatted tax amount
     */
    fun getFormattedTax(): String {
        return "$ ${String.format("%,.2f", taxAmount)}"
    }
    
    /**
     * Get total number of items
     */
    fun getTotalItems(): Int {
        return items.size
    }
    
    /**
     * Get total quantity of products
     */
    fun getTotalQuantity(): Int {
        return items.sumOf { it.quantity }
    }
}
