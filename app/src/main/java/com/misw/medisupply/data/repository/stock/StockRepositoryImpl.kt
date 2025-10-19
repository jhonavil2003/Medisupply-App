package com.misw.medisupply.data.repository.stock

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.remote.api.stock.StockApiService
import com.misw.medisupply.domain.model.stock.MultipleStockLevels
import com.misw.medisupply.domain.model.stock.StockLevel
import com.misw.medisupply.domain.repository.stock.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Stock Repository Implementation
 * Handles stock data operations with logistics service
 */
class StockRepositoryImpl @Inject constructor(
    private val apiService: StockApiService
) : StockRepository {
    
    override fun getProductStock(
        productSku: String,
        distributionCenterId: Int?,
        includeReserved: Boolean,
        includeInTransit: Boolean
    ): Flow<Resource<StockLevel>> = flow {
        try {
            emit(Resource.Loading())
            
            val response = apiService.getSingleProductStock(
                productSku = productSku.uppercase(),
                distributionCenterId = distributionCenterId,
                includeReserved = includeReserved,
                includeInTransit = includeInTransit
            )
            
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    emit(Resource.Success(dto.toDomain()))
                } ?: emit(Resource.Error("Empty response from server"))
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Invalid request parameters"
                    404 -> "Product stock not found for SKU: $productSku"
                    500 -> "Server error. Please try again later"
                    else -> "Error loading stock: ${response.message()}"
                }
                emit(Resource.Error(errorMessage))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: Check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected error: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }
    
    override fun getMultipleProductsStock(
        productSkus: List<String>,
        distributionCenterId: Int?,
        onlyAvailable: Boolean?,
        includeReserved: Boolean,
        includeInTransit: Boolean
    ): Flow<Resource<MultipleStockLevels>> = flow {
        try {
            emit(Resource.Loading())
            
            // Convert SKUs list to comma-separated string and uppercase
            val skusString = productSkus.joinToString(",") { it.uppercase() }
            
            val response = apiService.getMultipleProductsStock(
                productSkus = skusString,
                distributionCenterId = distributionCenterId,
                onlyAvailable = onlyAvailable,
                includeReserved = includeReserved,
                includeInTransit = includeInTransit
            )
            
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    emit(Resource.Success(dto.toDomain()))
                } ?: emit(Resource.Error("Empty response from server"))
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Invalid request parameters"
                    404 -> "Stock information not found"
                    500 -> "Server error. Please try again later"
                    else -> "Error loading stock: ${response.message()}"
                }
                emit(Resource.Error(errorMessage))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: Check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected error: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }
}
