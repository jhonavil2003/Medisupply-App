package com.misw.medisupply.data.remote.api.cart

import com.misw.medisupply.data.remote.dto.cart.ClearCartRequest
import com.misw.medisupply.data.remote.dto.cart.ClearCartResponse
import com.misw.medisupply.data.remote.dto.cart.RealTimeStockResponse
import com.misw.medisupply.data.remote.dto.cart.ReleaseStockRequest
import com.misw.medisupply.data.remote.dto.cart.ReleaseStockResponse
import com.misw.medisupply.data.remote.dto.cart.ReserveStockRequest
import com.misw.medisupply.data.remote.dto.cart.ReserveStockResponse
import com.misw.medisupply.data.remote.dto.cart.UserReservationsResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Cart API Service
 * Defines endpoints for cart reservation operations with the logistics service
 */
interface CartApiService {
    
    /**
     * Reserve stock temporarily when user adds product to cart
     * 
     * @param request Contains product_sku, quantity, user_id, session_id, etc.
     * @return Response with reservation details including expiration time
     */
    @POST("cart/reserve")
    suspend fun reserveStock(
        @Body request: ReserveStockRequest
    ): Response<ReserveStockResponse>
    
    /**
     * Release stock when user removes product from cart or reduces quantity
     * 
     * @param request Contains product_sku, quantity, user_id, session_id
     * @return Response with release confirmation and updated stock
     */
    @POST("cart/release")
    suspend fun releaseStock(
        @Body request: ReleaseStockRequest
    ): Response<ReleaseStockResponse>
    
    /**
     * Clear all cart reservations for a user (empties cart completely)
     * Supports both DELETE and POST methods
     * 
     * @param request Contains user_id and session_id
     * @return Response with count of cleared reservations
     */
    @HTTP(method = "DELETE", path = "cart/clear", hasBody = true)
    suspend fun clearCart(
        @Body request: ClearCartRequest
    ): Response<ClearCartResponse>
    
    /**
     * Alternative POST method for clearing cart (for compatibility)
     */
    @POST("cart/clear")
    suspend fun clearCartPost(
        @Body request: ClearCartRequest
    ): Response<ClearCartResponse>
    
    /**
     * Get all active reservations for a user
     * 
     * @param userId User identifier
     * @param sessionId Session identifier
     * @return Response with list of active cart reservations
     */
    @GET("cart/reservations")
    suspend fun getUserReservations(
        @Query("user_id") userId: String,
        @Query("session_id") sessionId: String
    ): Response<UserReservationsResponse>
    
    /**
     * Get real-time stock considering active cart reservations
     * Can query single or multiple products
     * 
     * @param productSku Single product SKU
     * @param productSkus Comma-separated list of SKUs
     * @param distributionCenterId Filter by distribution center
     * @return Response with real-time available stock
     */
    @GET("cart/stock/realtime")
    suspend fun getRealTimeStock(
        @Query("product_sku") productSku: String? = null,
        @Query("product_skus") productSkus: String? = null,
        @Query("distribution_center_id") distributionCenterId: Int? = null
    ): Response<RealTimeStockResponse>
}
