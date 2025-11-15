package com.misw.medisupply.data.repository.cart

import android.util.Log
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.local.session.SessionManager
import com.misw.medisupply.data.remote.api.cart.CartApiService
import com.misw.medisupply.data.remote.dto.cart.*
import com.misw.medisupply.domain.repository.cart.CartRepository
import com.misw.medisupply.domain.usecase.cart.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of CartRepository
 * Handles cart stock reservation operations with the backend
 */
class CartRepositoryImpl @Inject constructor(
    private val apiService: CartApiService,
    private val sessionManager: SessionManager
) : CartRepository {
    
    companion object {
        private const val TAG = "CartRepository"
    }
    
    override fun reserveStock(
        productSku: String,
        quantity: Int,
        distributionCenterId: Int,
        ttlMinutes: Int
    ): Flow<Resource<ReserveStockResult>> = flow {
        try {
            emit(Resource.Loading())
            
            val userId = sessionManager.getUserId()
            val sessionId = sessionManager.getSessionId()
            
            Log.d(TAG, "Reserving stock: $productSku x$quantity for user: $userId, session: $sessionId")
            
            val request = ReserveStockRequest(
                productSku = productSku,
                quantity = quantity,
                userId = userId,
                sessionId = sessionId,
                distributionCenterId = distributionCenterId,
                ttlMinutes = ttlMinutes
            )
            
            val response = apiService.reserveStock(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val result = ReserveStockResult(
                        success = true,
                        reservationId = body.reservationId,
                        productSku = body.productSku,
                        quantityReserved = body.quantityReserved ?: 0,
                        stockAvailable = body.stockAvailable ?: 0,
                        expiresAt = body.expiresAt,
                        remainingTimeSeconds = body.remainingTimeSeconds
                    )
                    Log.d(TAG, "✅ Stock reserved successfully: ${result.quantityReserved} units")
                    emit(Resource.Success(result))
                } else {
                    val errorMsg = body?.message ?: "Error al reservar stock"
                    Log.e(TAG, "❌ Reserve failed: $errorMsg")
                    emit(Resource.Error(errorMsg))
                }
            } else {
                when (response.code()) {
                    400 -> emit(Resource.Error("Datos inválidos. Verifique la información."))
                    404 -> emit(Resource.Error("Producto no encontrado en inventario."))
                    409 -> {
                        // Insufficient stock
                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "❌ Insufficient stock: $errorBody")
                        emit(Resource.Error("Stock insuficiente. Verifica la disponibilidad."))
                    }
                    else -> emit(Resource.Error("Error del servidor: ${response.code()}"))
                }
            }
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP Exception: ${e.message()}", e)
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        } catch (e: IOException) {
            Log.e(TAG, "IO Exception: ${e.message}", e)
            emit(Resource.Error("Error de red. Verifica tu conexión."))
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}", e)
            emit(Resource.Error("Error inesperado: ${e.localizedMessage}"))
        }
    }
    
    override fun releaseStock(
        productSku: String,
        quantity: Int
    ): Flow<Resource<ReleaseStockResult>> = flow {
        try {
            emit(Resource.Loading())
            
            val userId = sessionManager.getUserId()
            val sessionId = sessionManager.getSessionId()
            
            Log.d(TAG, "Releasing stock: $productSku x$quantity")
            
            val request = ReleaseStockRequest(
                productSku = productSku,
                quantity = quantity,
                userId = userId,
                sessionId = sessionId
            )
            
            val response = apiService.releaseStock(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val result = ReleaseStockResult(
                        success = true,
                        productSku = body.productSku,
                        quantityReleased = body.quantityReleased,
                        stockAvailable = body.stockAvailable
                    )
                    Log.d(TAG, "✅ Stock released successfully: ${result.quantityReleased} units")
                    emit(Resource.Success(result))
                } else {
                    emit(Resource.Error(body?.message ?: "Error al liberar stock"))
                }
            } else {
                emit(Resource.Error("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing stock: ${e.message}", e)
            emit(Resource.Error("Error al liberar stock: ${e.localizedMessage}"))
        }
    }
    
    override fun clearCart(): Flow<Resource<ClearCartResult>> = flow {
        try {
            emit(Resource.Loading())
            
            val userId = sessionManager.getUserId()
            val sessionId = sessionManager.getSessionId()
            
            Log.d(TAG, "Clearing cart for user: $userId, session: $sessionId")
            
            val request = ClearCartRequest(
                userId = userId,
                sessionId = sessionId
            )
            
            val response = apiService.clearCart(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val result = ClearCartResult(
                        success = true,
                        clearedCount = body.clearedCount,
                        productsAffected = body.productsAffected
                    )
                    Log.d(TAG, "✅ Cart cleared: ${result.clearedCount} reservations")
                    emit(Resource.Success(result))
                } else {
                    emit(Resource.Error(body?.message ?: "Error al limpiar carrito"))
                }
            } else {
                emit(Resource.Error("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing cart: ${e.message}", e)
            emit(Resource.Error("Error al limpiar carrito: ${e.localizedMessage}"))
        }
    }
    
    override fun getCartReservations(): Flow<Resource<List<CartReservation>>> = flow {
        try {
            emit(Resource.Loading())
            
            val userId = sessionManager.getUserId()
            val sessionId = sessionManager.getSessionId()
            
            Log.d(TAG, "Getting cart reservations for user: $userId")
            
            val response = apiService.getUserReservations(userId, sessionId)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val reservations = body.reservations.map { dto ->
                        CartReservation(
                            id = dto.id,
                            productSku = dto.productSku,
                            quantityReserved = dto.quantityReserved,
                            expiresAt = dto.expiresAt,
                            remainingTimeSeconds = dto.remainingTimeSeconds,
                            createdAt = dto.createdAt,
                            distributionCenterId = dto.distributionCenterId
                        )
                    }
                    Log.d(TAG, "✅ Retrieved ${reservations.size} cart reservations")
                    emit(Resource.Success(reservations))
                } else {
                    emit(Resource.Error("Error al obtener reservas"))
                }
            } else {
                emit(Resource.Error("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting cart reservations: ${e.message}", e)
            emit(Resource.Error("Error al obtener reservas: ${e.localizedMessage}"))
        }
    }
}
