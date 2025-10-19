package com.misw.medisupply.data.remote.api.order

import com.misw.medisupply.data.remote.dto.order.CreateOrderRequest
import com.misw.medisupply.data.remote.dto.order.OrderDto
import com.misw.medisupply.data.remote.dto.order.OrdersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
     * Get list of orders with optional filters
     * 
     * @param sellerId Filter by seller ID
     * @param customerId Filter by customer ID
     * @param status Filter by order status
     * @return Response containing OrdersResponse with list of orders and total count
     */
    @GET("orders")
    suspend fun getOrders(
        @Query("seller_id") sellerId: String? = null,
        @Query("customer_id") customerId: Int? = null,
        @Query("status") status: String? = null
    ): Response<OrdersResponse>
}
