package com.misw.medisupply.data.remote.dto.order

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for creating an order
 * Used in the POST /orders request body
 */
data class CreateOrderRequest(
    @SerializedName("customer_id")
    val customerId: Int,
    
    @SerializedName("seller_id")
    val sellerId: String,
    
    @SerializedName("seller_name")
    val sellerName: String? = null,
    
    @SerializedName("items")
    val items: List<CreateOrderItemRequest>,
    
    @SerializedName("payment_terms")
    val paymentTerms: String = "contado",
    
    @SerializedName("payment_method")
    val paymentMethod: String? = null,
    
    @SerializedName("delivery_address")
    val deliveryAddress: String? = null,
    
    @SerializedName("delivery_city")
    val deliveryCity: String? = null,
    
    @SerializedName("delivery_department")
    val deliveryDepartment: String? = null,
    
    @SerializedName("preferred_distribution_center")
    val preferredDistributionCenter: String? = null,
    
    @SerializedName("notes")
    val notes: String? = null
)
