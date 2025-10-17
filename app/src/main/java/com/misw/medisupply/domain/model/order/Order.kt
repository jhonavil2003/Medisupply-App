package com.misw.medisupply.domain.model.order

import java.util.Date

/**
 * Domain model representing an Order
 * This model is used across the application and is independent of data sources
 */
data class Order(
    val id: Int,
    val orderNumber: String,
    val customerId: Int,
    val sellerId: String,
    val sellerName: String,
    val orderDate: Date,
    val status: OrderStatus,
    val subtotal: Double,
    val discountAmount: Double,
    val taxAmount: Double,
    val totalAmount: Double,
    val paymentTerms: String,
    val paymentMethod: String,
    val deliveryAddress: String,
    val deliveryCity: String,
    val deliveryDepartment: String,
    val preferredDistributionCenter: String,
    val notes: String?,
    val createdAt: Date,
    val updatedAt: Date,
    val items: List<OrderItem>
) {
    /**
     * Get formatted total amount as currency
     */
    fun getFormattedTotal(): String {
        return "$ ${String.format("%,.2f", totalAmount)}"
    }
    
    /**
     * Get formatted subtotal as currency
     */
    fun getFormattedSubtotal(): String {
        return "$ ${String.format("%,.2f", subtotal)}"
    }
    
    /**
     * Get total number of items in order
     */
    fun getTotalItems(): Int {
        return items.sumOf { it.quantity }
    }
    
    /**
     * Get count of different products
     */
    fun getProductCount(): Int {
        return items.size
    }
    
    /**
     * Get full delivery address
     */
    fun getFullAddress(): String {
        return "$deliveryAddress, $deliveryCity, $deliveryDepartment"
    }
    
    /**
     * Check if order has notes
     */
    fun hasNotes(): Boolean {
        return !notes.isNullOrBlank()
    }
    
    /**
     * Get status display name
     */
    fun getStatusDisplayName(): String {
        return status.displayName
    }
    
    /**
     * Get payment terms display name
     */
    fun getPaymentTermsDisplay(): String {
        return when (paymentTerms) {
            "contado" -> "Contado"
            "credito_30" -> "Crédito 30 días"
            "credito_60" -> "Crédito 60 días"
            "credito_90" -> "Crédito 90 días"
            else -> paymentTerms
        }
    }
    
    /**
     * Get payment method display name
     */
    fun getPaymentMethodDisplay(): String {
        return when (paymentMethod) {
            "transferencia" -> "Transferencia"
            "cheque" -> "Cheque"
            "efectivo" -> "Efectivo"
            else -> paymentMethod
        }
    }
}

/**
 * Enum representing order status
 */
enum class OrderStatus(val displayName: String, val value: String) {
    PENDING("Pendiente", "pending"),
    CONFIRMED("Confirmado", "confirmed"),
    PROCESSING("En Proceso", "processing"),
    SHIPPED("Enviado", "shipped"),
    DELIVERED("Entregado", "delivered"),
    CANCELLED("Cancelado", "cancelled");
    
    companion object {
        /**
         * Get OrderStatus from string value
         */
        fun fromString(value: String): OrderStatus {
            return entries.find { 
                it.value.equals(value, ignoreCase = true) 
            } ?: PENDING
        }
        
        /**
         * Get all active statuses (excluding cancelled)
         */
        fun getActiveStatuses(): List<OrderStatus> {
            return entries.filter { it != CANCELLED }
        }
    }
}
