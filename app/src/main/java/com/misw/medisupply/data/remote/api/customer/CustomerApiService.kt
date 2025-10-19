package com.misw.medisupply.data.remote.api.customer

import com.misw.medisupply.data.remote.dto.customer.CustomerDto
import com.misw.medisupply.data.remote.dto.customer.CustomersResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API service for Customer endpoints
 * Defines all customer-related API calls
 */
interface CustomerApiService {
    
    /**
     * Get list of customers with optional filters
     * 
     * @param customerType Filter by customer type (hospital, clinica, farmacia, etc.)
     * @param city Filter by city
     * @param isActive Filter by active status
     * @return Response containing CustomersResponse with list of customers and total count
     */
    @GET("customers")
    suspend fun getCustomers(
        @Query("customer_type") customerType: String? = null,
        @Query("city") city: String? = null,
        @Query("is_active") isActive: Boolean? = null
    ): Response<CustomersResponse>
    
    /**
     * Get a single customer by ID
     * 
     * @param customerId The unique ID of the customer
     * @return Response containing CustomerDto
     */
    @GET("customers/{id}")
    suspend fun getCustomerById(
        @Path("id") customerId: Int
    ): Response<CustomerDto>
}
