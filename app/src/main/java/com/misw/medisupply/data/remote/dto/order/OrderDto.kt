package com.misw.medisupply.data.remote.dto.order

import com.google.gson.annotations.SerializedName
import com.misw.medisupply.data.remote.dto.customer.CustomerDto
import com.misw.medisupply.data.remote.dto.customer.toDomain
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.domain.model.order.PaymentMethod
import com.misw.medisupply.domain.model.order.PaymentTerms
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Data Transfer Object for Order from API
 * Matches the API response structure exactly
 */
data class OrderDto(
    @SerializedName("id")
    val id: Int?,
    
    @SerializedName("order_number")
    val orderNumber: String?,
    
    @SerializedName("customer_id")
    val customerId: Int,
    
    @SerializedName("seller_id")
    val sellerId: String,
    
    @SerializedName("seller_name")
    val sellerName: String?,
    
    @SerializedName("order_date")
    val orderDate: String?,
    
    @SerializedName("delivery_date")
    val deliveryDate: String?,
    
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
    val paymentMethod: String?,
    
    @SerializedName("delivery_address")
    val deliveryAddress: String?,
    
    @SerializedName("delivery_city")
    val deliveryCity: String?,
    
    @SerializedName("delivery_department")
    val deliveryDepartment: String?,
    
    @SerializedName("preferred_distribution_center")
    val preferredDistributionCenter: String?,
    
    @SerializedName("notes")
    val notes: String?,
    
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("updated_at")
    val updatedAt: String?,
    
    @SerializedName("customer")
    val customer: CustomerDto?,
    
    @SerializedName("items")
    val items: List<OrderItemDto>
)

/**
 * Parse ISO 8601 date string
 */
private fun String.parseIso8601() = try {
    // Try parsing with microseconds (backend format)
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()).parse(this)
} catch (e: Exception) {
    try {
        // Try parsing with milliseconds and Z
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(this)
    } catch (e2: Exception) {
        try {
            // Try parsing with Z but no milliseconds
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(this)
        } catch (e3: Exception) {
            try {
                // Try simple date format
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(this)
            } catch (e4: Exception) {
                null
            }
        }
    }
}

/**
 * Extension function to convert OrderDto to Domain Order model
 */
fun OrderDto.toDomain() = Order(
    id = id,
    orderNumber = orderNumber,
    customerId = customerId,
    sellerId = sellerId,
    sellerName = sellerName,
    orderDate = orderDate?.parseIso8601(),
    deliveryDate = deliveryDate?.parseIso8601(),
    status = OrderStatus.fromValue(status),
    subtotal = subtotal,
    discountAmount = discountAmount,
    taxAmount = taxAmount,
    totalAmount = totalAmount,
    paymentTerms = PaymentTerms.fromValue(paymentTerms),
    paymentMethod = PaymentMethod.fromValue(paymentMethod),
    deliveryAddress = deliveryAddress,
    deliveryCity = deliveryCity,
    deliveryDepartment = deliveryDepartment,
    preferredDistributionCenter = preferredDistributionCenter,
    notes = notes,
    createdAt = createdAt?.parseIso8601(),
    updatedAt = updatedAt?.parseIso8601(),
    customer = customer?.toDomain(),
    items = items.map { it.toDomain() }
)
