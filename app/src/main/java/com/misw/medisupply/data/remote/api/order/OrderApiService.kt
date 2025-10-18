package com.misw.medisupply.data.remote.api.order

import com.misw.medisupply.data.remote.dto.order.OrdersResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API service for Order endpoints
 * Defines all order-related API calls
 */
interface OrderApiService {
    
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
    
    /**
     * Delete an order by ID
     * 
     * @param orderId ID of the order to delete
     * @return Response with success status
     */
    @DELETE("orders/{order_id}")
    suspend fun deleteOrder(
        @Path("order_id") orderId: Int
    ): Response<Unit>
}
