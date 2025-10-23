package com.misw.medisupply.data.repository.order

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.core.utils.Constants
import com.misw.medisupply.data.remote.api.order.OrderApiService
import com.misw.medisupply.data.remote.dto.order.CreateOrderItemRequest
import com.misw.medisupply.data.remote.dto.order.CreateOrderRequest
import com.misw.medisupply.data.remote.dto.order.UpdateOrderItemRequest
import com.misw.medisupply.data.remote.dto.order.UpdateOrderRequest
import com.misw.medisupply.data.remote.dto.order.toDomain
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.PaymentMethod
import com.misw.medisupply.domain.model.order.PaymentTerms
import com.misw.medisupply.domain.repository.order.OrderItemRequest
import com.misw.medisupply.domain.repository.order.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Order Repository Implementation
 * Handles order data operations with sales service
 */
class OrderRepositoryImpl @Inject constructor(
    private val apiService: OrderApiService
) : OrderRepository {
    
    override fun createOrder(
        customerId: Int,
        sellerId: String,
        sellerName: String?,
        items: List<OrderItemRequest>,
        paymentTerms: PaymentTerms,
        paymentMethod: PaymentMethod?,
        deliveryAddress: String?,
        deliveryCity: String?,
        deliveryDepartment: String?,
        deliveryDate: String?,
        preferredDistributionCenter: String?,
        notes: String?
    ): Flow<Resource<Order>> = flow {
        try {
            emit(Resource.Loading())
            
            // Map domain items to DTO items
            val itemsDto = items.map { item ->
                CreateOrderItemRequest(
                    productSku = item.productSku,
                    quantity = item.quantity,
                    discountPercentage = item.discountPercentage,
                    taxPercentage = item.taxPercentage
                )
            }
            
            // Create request DTO
            val request = CreateOrderRequest(
                customerId = customerId,
                sellerId = sellerId,
                sellerName = sellerName,
                items = itemsDto,
                paymentTerms = paymentTerms.value,
                paymentMethod = paymentMethod?.value,
                deliveryAddress = deliveryAddress,
                deliveryCity = deliveryCity,
                deliveryDepartment = deliveryDepartment,
                deliveryDate = deliveryDate,
                preferredDistributionCenter = preferredDistributionCenter,
                notes = notes
            )
            
            val response = apiService.createOrder(request)
            
            if (response.isSuccessful) {
                response.body()?.let { orderDto ->
                    emit(Resource.Success(orderDto.toDomain()))
                } ?: emit(Resource.Error("Response body is null"))
            } else {
                // Parse error response
                val errorBody = response.errorBody()?.string()
                val errorMessage = when (response.code()) {
                    400 -> "Validación fallida: $errorBody"
                    404 -> "Cliente o producto no encontrado"
                    409 -> "Stock insuficiente"
                    503 -> "Servicio no disponible. Intente nuevamente."
                    else -> "Error ${response.code()}: ${response.message()}"
                }
                emit(Resource.Error(errorMessage))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Error HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Error de red: Verifique su conexión a internet"))
        } catch (e: Exception) {
            emit(Resource.Error("Error inesperado: ${e.message}"))
        }
    }
    
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
                    val orders = ordersResponse.orders.map { it.toDomain() }
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
    
    override fun getOrderById(orderId: Int): Flow<Resource<Order>> = flow {
        try {
            // Emit loading state
            emit(Resource.Loading())
            
            // Make API call
            val response = apiService.getOrderById(orderId)
            
            // Handle response
            if (response.isSuccessful) {
                val orderDto = response.body()
                if (orderDto != null) {
                    // Convert DTO to domain model
                    emit(Resource.Success(orderDto.toDomain()))
                } else {
                    emit(Resource.Error(Constants.ErrorMessages.NO_DATA))
                }
            } else {
                // Handle HTTP errors
                val errorMessage = when (response.code()) {
                    401 -> Constants.ErrorMessages.UNAUTHORIZED
                    404 -> "Pedido no encontrado"
                    500 -> Constants.ErrorMessages.SERVER_ERROR
                    else -> "${Constants.ErrorMessages.SERVER_ERROR} (${response.code()})"
                }
                emit(Resource.Error(errorMessage))
            }
            
        } catch (e: HttpException) {
            // HTTP exceptions (4xx, 5xx responses)
            val errorMessage = when (e.code()) {
                401 -> Constants.ErrorMessages.UNAUTHORIZED
                404 -> "Pedido no encontrado"
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
    
    override fun deleteOrder(orderId: Int): Flow<Resource<Unit>> = flow {
        try {
            // Emit loading state
            emit(Resource.Loading())
            
            // Make API call
            val response = apiService.deleteOrder(orderId)
            
            // Handle response
            if (response.isSuccessful) {
                emit(Resource.Success(Unit))
            } else {
                // Handle HTTP errors
                val errorMessage = when (response.code()) {
                    400 -> "No se puede eliminar. Solo se pueden eliminar pedidos en estado Pendiente o Cancelado"
                    401 -> Constants.ErrorMessages.UNAUTHORIZED
                    403 -> "No tiene permisos para eliminar este pedido"
                    404 -> "Pedido no encontrado"
                    500 -> Constants.ErrorMessages.SERVER_ERROR
                    else -> "${Constants.ErrorMessages.SERVER_ERROR} (${response.code()})"
                }
                emit(Resource.Error(errorMessage))
            }
            
        } catch (e: HttpException) {
            // HTTP exceptions (4xx, 5xx responses)
            val errorMessage = when (e.code()) {
                400 -> "No se puede eliminar. Solo se pueden eliminar pedidos en estado Pendiente o Cancelado"
                401 -> Constants.ErrorMessages.UNAUTHORIZED
                403 -> "No tiene permisos para eliminar este pedido"
                404 -> "Pedido no encontrado"
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
    
    override fun updateOrder(
        orderId: Int,
        customerId: Int,
        items: List<OrderItemRequest>,
        paymentTerms: PaymentTerms,
        paymentMethod: PaymentMethod?,
        deliveryAddress: String?,
        deliveryCity: String?,
        deliveryDepartment: String?,
        deliveryDate: String?,
        preferredDistributionCenter: String?,
        notes: String?
    ): Flow<Resource<Order>> = flow {
        try {
            emit(Resource.Loading())
            
            android.util.Log.d("OrderRepositoryImpl", "updateOrder() called for orderId: $orderId")
            
            // Map domain items to DTO items
            val itemsDto = items.map { item ->
                UpdateOrderItemRequest(
                    productSku = item.productSku,
                    productName = null, // Backend will fetch from product service
                    quantity = item.quantity,
                    unitPrice = null, // Backend will fetch from product service
                    discountPercentage = item.discountPercentage,
                    taxPercentage = item.taxPercentage
                )
            }
            
            // Create update request DTO
            val request = UpdateOrderRequest(
                customerId = customerId,
                items = itemsDto,
                paymentTerms = paymentTerms.value,
                paymentMethod = paymentMethod?.value,
                deliveryAddress = deliveryAddress,
                deliveryCity = deliveryCity,
                deliveryDepartment = deliveryDepartment,
                deliveryDate = deliveryDate,
                preferredDistributionCenter = preferredDistributionCenter,
                notes = notes
            )
            
            android.util.Log.d("OrderRepositoryImpl", "Request: customerId=$customerId, items=${items.size}, paymentTerms=${paymentTerms.value}")
            
            // Make API call
            val response = apiService.updateOrder(orderId, request)
            
            android.util.Log.d("OrderRepositoryImpl", "Response code: ${response.code()}")
            
            if (response.isSuccessful) {
                response.body()?.let { orderDto ->
                    android.util.Log.d("OrderRepositoryImpl", "Success! Order: ${orderDto.orderNumber}, Status: ${orderDto.status}")
                    emit(Resource.Success(orderDto.toDomain()))
                } ?: run {
                    android.util.Log.e("OrderRepositoryImpl", "Response body is null")
                    emit(Resource.Error("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("OrderRepositoryImpl", "Error response: code=${response.code()}, body=$errorBody")
                val errorMessage = when (response.code()) {
                    400 -> "Validación fallida: Solo se pueden actualizar pedidos en estado Pendiente"
                    404 -> "Pedido no encontrado"
                    409 -> "Stock insuficiente"
                    500 -> Constants.ErrorMessages.SERVER_ERROR
                    else -> "Error ${response.code()}: ${response.message()}"
                }
                emit(Resource.Error(errorMessage))
            }
        } catch (e: HttpException) {
            android.util.Log.e("OrderRepositoryImpl", "HttpException: ${e.message()}", e)
            emit(Resource.Error("Error HTTP: ${e.message()}"))
        } catch (e: IOException) {
            android.util.Log.e("OrderRepositoryImpl", "IOException: ${e.message}", e)
            emit(Resource.Error("Error de red: Verifique su conexión a internet"))
        } catch (e: Exception) {
            android.util.Log.e("OrderRepositoryImpl", "Exception: ${e.message}", e)
            emit(Resource.Error("Error inesperado: ${e.message}"))
        }
    }
}
