package com.misw.medisupply.data.remote.dto.order

import com.google.gson.annotations.SerializedName
import com.misw.medisupply.domain.model.order.OrderItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data Transfer Object for Order Item from API
 */
data class OrderItemDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("order_id")
    val orderId: Int,
    
    @SerializedName("product_sku")
    val productSku: String,
    
    @SerializedName("product_name")
    val productName: String,
    
    @SerializedName("quantity")
    val quantity: Int,
    
    @SerializedName("unit_price")
    val unitPrice: Double,
    
    @SerializedName("discount_percentage")
    val discountPercentage: Double,
    
    @SerializedName("discount_amount")
    val discountAmount: Double,
    
    @SerializedName("tax_percentage")
    val taxPercentage: Double,
    
    @SerializedName("tax_amount")
    val taxAmount: Double,
    
    @SerializedName("subtotal")
    val subtotal: Double,
    
    @SerializedName("total")
    val total: Double,
    
    @SerializedName("distribution_center_code")
    val distributionCenterCode: String,
    
    @SerializedName("stock_confirmed")
    val stockConfirmed: Boolean,
    
    @SerializedName("stock_confirmation_date")
    val stockConfirmationDate: String?,
    
    @SerializedName("created_at")
    val createdAt: String
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
 * Extension function to convert OrderItemDto to Domain OrderItem model
 */
fun OrderItemDto.toDomain(): OrderItem {
    return OrderItem(
        id = id,
        orderId = orderId,
        productSku = productSku,
        productName = productName,
        quantity = quantity,
        unitPrice = unitPrice,
        discountPercentage = discountPercentage,
        discountAmount = discountAmount,
        taxPercentage = taxPercentage,
        taxAmount = taxAmount,
        subtotal = subtotal,
        total = total,
        distributionCenterCode = distributionCenterCode,
        stockConfirmed = stockConfirmed,
        stockConfirmationDate = stockConfirmationDate?.parseIso8601(),
        createdAt = createdAt.parseIso8601()
    )
}
