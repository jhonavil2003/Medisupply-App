package com.misw.medisupply.domain.usecase.cart

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.repository.cart.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case: Reserve stock when adding product to cart
 */
class ReserveStockUseCase @Inject constructor(
    private val repository: CartRepository
) {
    operator fun invoke(
        productSku: String,
        quantity: Int,
        distributionCenterId: Int = 1,
        ttlMinutes: Int = 15
    ): Flow<Resource<ReserveStockResult>> {
        return repository.reserveStock(
            productSku = productSku,
            quantity = quantity,
            distributionCenterId = distributionCenterId,
            ttlMinutes = ttlMinutes
        )
    }
}

/**
 * Result from reserve stock operation
 */
data class ReserveStockResult(
    val success: Boolean,
    val reservationId: Int?,
    val productSku: String,
    val quantityReserved: Int,
    val stockAvailable: Int,
    val expiresAt: String?,
    val remainingTimeSeconds: Int?
)

/**
 * Use Case: Release stock when removing product from cart
 */
class ReleaseStockUseCase @Inject constructor(
    private val repository: CartRepository
) {
    operator fun invoke(
        productSku: String,
        quantity: Int
    ): Flow<Resource<ReleaseStockResult>> {
        return repository.releaseStock(
            productSku = productSku,
            quantity = quantity
        )
    }
}

/**
 * Result from release stock operation
 */
data class ReleaseStockResult(
    val success: Boolean,
    val productSku: String,
    val quantityReleased: Int,
    val stockAvailable: Int
)

/**
 * Use Case: Clear all cart reservations
 */
class ClearCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    operator fun invoke(): Flow<Resource<ClearCartResult>> {
        return repository.clearCart()
    }
}

/**
 * Result from clear cart operation
 */
data class ClearCartResult(
    val success: Boolean,
    val clearedCount: Int,
    val productsAffected: List<String>
)

/**
 * Use Case: Get user's cart reservations
 */
class GetCartReservationsUseCase @Inject constructor(
    private val repository: CartRepository
) {
    operator fun invoke(): Flow<Resource<List<CartReservation>>> {
        return repository.getCartReservations()
    }
}

/**
 * Domain model for cart reservation
 */
data class CartReservation(
    val id: Int,
    val productSku: String,
    val quantityReserved: Int,
    val expiresAt: String,
    val remainingTimeSeconds: Int,
    val createdAt: String,
    val distributionCenterId: Int
)
