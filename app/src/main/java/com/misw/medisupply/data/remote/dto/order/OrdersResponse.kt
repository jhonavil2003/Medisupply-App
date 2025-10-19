package com.misw.medisupply.data.remote.dto.order

import com.google.gson.annotations.SerializedName

/**
 * Response wrapper for list of orders from API
 * Matches the API response structure from GET /orders endpoint
 */
data class OrdersResponse(
    @SerializedName("orders")
    val orders: List<OrderDto>,
    
    @SerializedName("total")
    val total: Int
)
