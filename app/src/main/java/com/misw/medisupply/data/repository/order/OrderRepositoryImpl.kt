package com.misw.medisupply.data.repository.order

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.remote.api.order.OrderApiService
import com.misw.medisupply.data.remote.dto.order.CreateOrderItemRequest
import com.misw.medisupply.data.remote.dto.order.CreateOrderRequest
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
}
