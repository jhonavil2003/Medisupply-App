package com.misw.medisupply.domain.repository.cart

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.usecase.cart.CartReservation
import com.misw.medisupply.domain.usecase.cart.ClearCartResult
import com.misw.medisupply.domain.usecase.cart.ReleaseStockResult
import com.misw.medisupply.domain.usecase.cart.ReserveStockResult
import kotlinx.coroutines.flow.Flow

/**
 * Cart Repository Interface
 * Defines operations for cart stock reservations
 */
interface CartRepository {
    
    /**
     * Reserve stock for a product
     */
    fun reserveStock(
        productSku: String,
        quantity: Int,
        distributionCenterId: Int = 1,
        ttlMinutes: Int = 15
    ): Flow<Resource<ReserveStockResult>>
    
    /**
     * Release reserved stock
     */
    fun releaseStock(
        productSku: String,
        quantity: Int
    ): Flow<Resource<ReleaseStockResult>>
    
    /**
     * Clear all cart reservations for current user
     */
    fun clearCart(): Flow<Resource<ClearCartResult>>
    
    /**
     * Get all cart reservations for current user
     */
    fun getCartReservations(): Flow<Resource<List<CartReservation>>>
}
