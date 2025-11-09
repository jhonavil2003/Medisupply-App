package com.misw.medisupply.data.remote.api.order

import com.misw.medisupply.data.remote.dto.order.CreateOrderRequest
import com.misw.medisupply.data.remote.dto.order.OrderDto
import com.misw.medisupply.data.remote.dto.order.OrdersResponse
import com.misw.medisupply.data.remote.dto.order.UpdateOrderRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API service for Order endpoints
 * Defines all order-related API calls
 */
interface OrderApiService {
    
    /**
     * Create a new order
     * 
     * @param orderRequest Request body containing order details
     * @return Response containing OrderDto with order confirmation
     */
    @POST("orders")
    suspend fun createOrder(
        @Body orderRequest: CreateOrderRequest
    ): Response<OrderDto>
    
    /**
     * Get list of orders with optional filters and pagination
     * 
     * @param sellerId Filter by seller ID
     * @param customerId Filter by customer ID
     * @param status Filter by order status
     * @param page Page number (default: 1)
     * @param perPage Number of results per page (default: 20, max: 100)
     * @param includeDetails Include order items in response (default: false)
     * @return Response containing OrdersResponse with list of orders and pagination metadata
     */
    @GET("orders")
    suspend fun getOrders(
        @Query("seller_id") sellerId: String? = null,
        @Query("customer_id") customerId: Int? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("include_details") includeDetails: Boolean = false
    ): Response<OrdersResponse>
    
    /**
     * Get a single order by ID
     * 
     * @param orderId ID of the order to retrieve
     * @return Response containing OrderDto with order details
     */
    @GET("orders/{id}")
    suspend fun getOrderById(
        @Path("id") orderId: Int
    ): Response<OrderDto>
    
    /**
     * Delete an order by ID
     * 
     * @param orderId ID of the order to delete
     * @return Response indicating success or failure
     */
    @DELETE("orders/{id}")
    suspend fun deleteOrder(
        @Path("id") orderId: Int
    ): Response<Unit>
    
    /**
     * Update an existing order
     * 
     * @param orderId ID of the order to update
     * @param updateRequest Request body containing fields to update
     * @return Response containing updated OrderDto
     */
    @PATCH("orders/{id}")
    suspend fun updateOrder(
        @Path("id") orderId: Int,
        @Body updateRequest: UpdateOrderRequest
    ): Response<OrderDto>
}
