package com.misw.medisupply.data.repository.order

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.core.utils.Constants
import com.misw.medisupply.data.remote.api.order.OrderApiService
import com.misw.medisupply.data.remote.dto.order.toDomain
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.repository.order.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of OrderRepository
 * Handles data operations for orders from remote API
 */
class OrderRepositoryImpl @Inject constructor(
    private val apiService: OrderApiService
) : OrderRepository {
    
    override fun getOrders(
        sellerId: String?,
        customerId: Int?,
        status: String?
    ): Flow<Resource<List<Order>>> = flow {
        try {
            // Emit loading state
            emit(Resource.Loading())
            
            // Make API call
            val response = apiService.getOrders(
                sellerId = sellerId,
                customerId = customerId,
                status = status
            )
            
            // Handle response
            if (response.isSuccessful) {
                val ordersResponse = response.body()
                if (ordersResponse != null) {
                    // Convert DTOs to domain models
                    val orders = ordersResponse.orders.toDomain()
                    emit(Resource.Success(orders))
                } else {
                    emit(Resource.Error(Constants.ErrorMessages.NO_DATA))
                }
            } else {
                // Handle HTTP errors
                val errorMessage = when (response.code()) {
                    401 -> Constants.ErrorMessages.UNAUTHORIZED
                    404 -> Constants.ErrorMessages.NOT_FOUND
                    500 -> Constants.ErrorMessages.SERVER_ERROR
                    else -> "${Constants.ErrorMessages.SERVER_ERROR} (${response.code()})"
                }
                emit(Resource.Error(errorMessage))
            }
            
        } catch (e: HttpException) {
            // HTTP exceptions (4xx, 5xx responses)
            val errorMessage = when (e.code()) {
                401 -> Constants.ErrorMessages.UNAUTHORIZED
                404 -> Constants.ErrorMessages.NOT_FOUND
                408 -> Constants.ErrorMessages.TIMEOUT
                500, 502, 503 -> Constants.ErrorMessages.SERVER_ERROR
                else -> Constants.ErrorMessages.UNKNOWN_ERROR
            }
            emit(Resource.Error(errorMessage))
            
        } catch (e: IOException) {
            // Network errors (no connection, timeout, etc.)
            emit(Resource.Error(Constants.ErrorMessages.NETWORK_ERROR))
            
        } catch (e: Exception) {
            // Unknown errors
            emit(Resource.Error(e.message ?: Constants.ErrorMessages.UNKNOWN_ERROR))
        }
    }
}
