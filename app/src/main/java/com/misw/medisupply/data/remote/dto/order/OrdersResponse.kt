package com.misw.medisupply.data.remote.dto.order

import com.google.gson.annotations.SerializedName

/**
 * Response wrapper for list of orders from API
 * Matches the API response structure from GET /orders endpoint
 */
data class OrdersResponse(
    @SerializedName("orders")
    val orders: List<OrderDto>? = null,
    
    @SerializedName("total")
    val total: Int = 0,
    
    @SerializedName("page")
    val page: Int = 1,
    
    @SerializedName("per_page")
    val perPage: Int = 20,
    
    @SerializedName("total_pages")
    val totalPages: Int = 1
)
