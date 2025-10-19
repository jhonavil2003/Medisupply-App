package com.misw.medisupply.data.remote.dto.customer

import com.google.gson.annotations.SerializedName

/**
 * Response wrapper for list of customers from API
 * Matches the API response structure
 */
data class CustomersResponse(
    @SerializedName("customers")
    val customers: List<CustomerDto>,
    
    @SerializedName("total")
    val total: Int
)
