package com.misw.medisupply.data.remote.dto.order

import com.google.gson.annotations.SerializedName

/**
 * DTO for updating an existing order
 * Matches the backend PATCH /orders/{id} endpoint structure
 */
data class UpdateOrderRequest(
    @SerializedName("customer_id")
    val customerId: Int? = null,
    
    @SerializedName("items")
    val items: List<UpdateOrderItemRequest>? = null,
    
    @SerializedName("payment_terms")
    val paymentTerms: String? = null,
    
    @SerializedName("payment_method")
    val paymentMethod: String? = null,
    
    @SerializedName("delivery_address")
    val deliveryAddress: String? = null,
    
    @SerializedName("delivery_city")
    val deliveryCity: String? = null,
    
    @SerializedName("delivery_department")
    val deliveryDepartment: String? = null,
    
    @SerializedName("delivery_date")
    val deliveryDate: String? = null,
    
    @SerializedName("preferred_distribution_center")
    val preferredDistributionCenter: String? = null,
    
    @SerializedName("notes")
    val notes: String? = null
)

/**
 * DTO for order items in update request
 * Note: unit_price is REQUIRED by backend when updating items
 */
data class UpdateOrderItemRequest(
    @SerializedName("product_sku")
    val productSku: String,
    
    @SerializedName("product_name")
    val productName: String? = null,
    
    @SerializedName("quantity")
    val quantity: Int,
    
    @SerializedName("unit_price")
    val unitPrice: Double, // REQUIRED: Backend validates this field is present and non-negative
    
    @SerializedName("discount_percentage")
    val discountPercentage: Double? = null,
    
    @SerializedName("tax_percentage")
    val taxPercentage: Double? = null
)
