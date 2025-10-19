package com.misw.medisupply.data.remote.dto.order

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for creating an order item
 * Used in the request body when creating an order
 */
data class CreateOrderItemRequest(
    @SerializedName("product_sku")
    val productSku: String,
    
    @SerializedName("quantity")
    val quantity: Int,
    
    @SerializedName("discount_percentage")
    val discountPercentage: Double = 0.0,
    
    @SerializedName("tax_percentage")
    val taxPercentage: Double = 19.0
)
