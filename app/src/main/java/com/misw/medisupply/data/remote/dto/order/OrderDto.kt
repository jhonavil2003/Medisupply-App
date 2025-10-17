package com.misw.medisupply.data.remote.dto.order

import com.google.gson.annotations.SerializedName
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data Transfer Object for Order from API
 * Matches the API response structure exactly
 */
data class OrderDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("order_number")
    val orderNumber: String,
    
    @SerializedName("customer_id")
    val customerId: Int,
    
    @SerializedName("seller_id")
    val sellerId: String,
    
    @SerializedName("seller_name")
    val sellerName: String,
    
    @SerializedName("order_date")
    val orderDate: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("subtotal")
    val subtotal: Double,
    
    @SerializedName("discount_amount")
    val discountAmount: Double,
    
    @SerializedName("tax_amount")
    val taxAmount: Double,
    
    @SerializedName("total_amount")
    val totalAmount: Double,
    
    @SerializedName("payment_terms")
    val paymentTerms: String,
    
    @SerializedName("payment_method")
    val paymentMethod: String,
    
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    
    @SerializedName("delivery_city")
    val deliveryCity: String,
    
    @SerializedName("delivery_department")
    val deliveryDepartment: String,
    
    @SerializedName("preferred_distribution_center")
    val preferredDistributionCenter: String,
    
    @SerializedName("notes")
    val notes: String?,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("updated_at")
    val updatedAt: String,
    
    @SerializedName("items")
    val items: List<OrderItemDto>
)

/**
 * Parse ISO 8601 date string
 */
private fun String.parseIso8601(): Date {
    return try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(this) ?: Date()
    } catch (e: Exception) {
        Date()
    }
}

/**
 * Extension function to convert OrderDto to Domain Order model
 */
fun OrderDto.toDomain(): Order {
    return Order(
        id = id,
        orderNumber = orderNumber,
        customerId = customerId,
        sellerId = sellerId,
        sellerName = sellerName,
        orderDate = orderDate.parseIso8601(),
        status = OrderStatus.fromString(status),
        subtotal = subtotal,
        discountAmount = discountAmount,
        taxAmount = taxAmount,
        totalAmount = totalAmount,
        paymentTerms = paymentTerms,
        paymentMethod = paymentMethod,
        deliveryAddress = deliveryAddress,
        deliveryCity = deliveryCity,
        deliveryDepartment = deliveryDepartment,
        preferredDistributionCenter = preferredDistributionCenter,
        notes = notes,
        createdAt = createdAt.parseIso8601(),
        updatedAt = updatedAt.parseIso8601(),
        items = items.map { it.toDomain() }
    )
}

/**
 * Extension function to convert list of OrderDto to domain models
 */
fun List<OrderDto>.toDomain(): List<Order> {
    return this.map { it.toDomain() }
}
