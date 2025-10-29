package com.misw.medisupply.data.remote.dto.order

import com.google.gson.annotations.SerializedName
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderItem
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.domain.model.order.PaymentTerms
import com.misw.medisupply.domain.model.order.PaymentMethod
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * DTO for Order tracking from API
 */
data class OrderTrackingDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("order_number")
    val orderNumber: String,
    
    @SerializedName("customer_id")
    val customerId: Int,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("total")
    val total: Double,
    
    @SerializedName("order_date")
    val orderDate: String,
    
    @SerializedName("delivery_date")
    val deliveryDate: String?,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("updated_at")
    val updatedAt: String
)

/**
 * Extension function to convert DTO to domain model
 */
fun OrderTrackingDto.toDomain(): Order {
    val microsecondsFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
    val millisecondsFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val standardFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    fun parseDate(dateStr: String): Date? {
        return try {
            microsecondsFormat.parse(dateStr)
        } catch (e: Exception) {
            try {
                millisecondsFormat.parse(dateStr)
            } catch (e: Exception) {
                try {
                    standardFormat.parse(dateStr)
                } catch (e: Exception) {
                    try {
                        simpleDateFormat.parse(dateStr)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        }
    }
    
    return Order(
        id = id,
        orderNumber = orderNumber,
        customerId = customerId,
        sellerId = "", // Not provided in tracking API
        sellerName = null,
        orderDate = parseDate(orderDate) ?: Date(),
        deliveryDate = deliveryDate?.let { parseDate(it) },
        status = OrderStatus.fromValue(status),
        subtotal = total,
        discountAmount = 0.0,
        taxAmount = 0.0,
        totalAmount = total,
        paymentTerms = PaymentTerms.CREDIT_30, // Default
        paymentMethod = null,
        deliveryAddress = null,
        deliveryCity = null,
        deliveryDepartment = null,
        preferredDistributionCenter = null,
        notes = null,
        createdAt = parseDate(createdAt) ?: Date(),
        updatedAt = parseDate(updatedAt) ?: Date(),
        customer = null,
        items = emptyList()
    )
}

/**
 * Extension function to convert list of DTOs to domain models
 */
fun List<OrderTrackingDto>.toDomain(): List<Order> = map { it.toDomain() }