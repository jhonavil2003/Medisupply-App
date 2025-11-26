package com.misw.medisupply.data.remote.api.customer

import com.misw.medisupply.data.remote.dto.customer.CreateCustomerRequest
import com.misw.medisupply.data.remote.dto.customer.CreateCustomerResponse
import com.misw.medisupply.data.remote.dto.customer.CustomerDto
import com.misw.medisupply.data.remote.dto.customer.CustomersResponse
import com.misw.medisupply.data.remote.dto.customer.ValidateDocumentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
    
    /**
     * Validate if a document number is already registered
     * 
     * @param documentNumber The document number to validate
     * @param documentType The type of document (NIT, CC, etc.)
     * @return Response containing ValidateDocumentResponse
     */
    @GET("customers/validate-document")
    suspend fun validateDocumentNumber(
        @Query("document_number") documentNumber: String,
        @Query("document_type") documentType: String = "NIT"
    ): Response<ValidateDocumentResponse>
    
    /**
     * Register a new customer
     * 
     * @param request The customer registration data
     * @return Response containing CreateCustomerResponse with created customer info
     */
    @POST("customers")
    suspend fun registerCustomer(
        @Body request: CreateCustomerRequest
    ): Response<CreateCustomerResponse>
    
    /**
     * Get customers assigned to a specific salesperson
     * 
     * @param salespersonId The ID of the salesperson
     * @param isActive Filter by active status (optional)
     * @param page Page number for pagination (default: 1)
     * @param perPage Number of results per page (default: 50)
     * @return Response containing CustomersResponse with list of customers and total count
     */
    @GET("customers/by-salesperson/{salesperson_id}")
    suspend fun getCustomersBySalesperson(
        @Path("salesperson_id") salespersonId: Int,
        @Query("is_active") isActive: Boolean? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 50
    ): Response<CustomersResponse>

    /**
     * Get customers assigned to a specific salesperson by employee ID
     *
     */
    @GET("customers/by-salesperson/employee/{employee_id}")
    suspend fun getCustomersBySalespersonEmployeeId(
        @Path("employee_id") employeeId: String
    ): Response<CustomersResponse>
}
