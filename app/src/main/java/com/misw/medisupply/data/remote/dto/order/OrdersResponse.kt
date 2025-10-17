package com.misw.medisupply.data.remote.dto.order

import com.google.gson.annotations.SerializedName

/**
 * Response wrapper for Orders API endpoint
 */
data class OrdersResponse(
    @SerializedName("orders")
    val orders: List<OrderDto>,
    
    @SerializedName("total")
    val total: Int
)
