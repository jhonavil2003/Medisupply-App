package com.misw.medisupply.data.repository.customer

import com.misw.medisupply.core.base.AppException
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.remote.api.customer.CustomerApiService
import com.misw.medisupply.data.remote.dto.customer.toDomain
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.repository.customer.CustomerRepository
import com.misw.medisupply.core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of CustomerRepository
 * Handles data operations for customers from remote API
 */
class CustomerRepositoryImpl @Inject constructor(
    private val apiService: CustomerApiService
) : CustomerRepository {
    
    override fun getCustomers(
        customerType: String?,
        city: String?,
        isActive: Boolean?
    ): Flow<Resource<List<Customer>>> = flow {
        try {
            // Emit loading state
            emit(Resource.Loading())
            
            // Make API call
            val response = apiService.getCustomers(
                customerType = customerType,
                city = city,
                isActive = isActive
            )
            
            // Handle response
            if (response.isSuccessful) {
                val customersResponse = response.body()
                if (customersResponse != null) {
                    // Convert DTOs to domain models
                    val customers = customersResponse.customers.toDomain()
                    emit(Resource.Success(customers))
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
    
    override fun getCustomerById(customerId: Int): Flow<Resource<Customer>> = flow {
        try {
            // Emit loading state
            emit(Resource.Loading())
            
            // Make API call
            val response = apiService.getCustomerById(customerId)
            
            // Handle response
            if (response.isSuccessful) {
                val customerDto = response.body()
                if (customerDto != null) {
                    // Convert DTO to domain model
                    val customer = customerDto.toDomain()
                    emit(Resource.Success(customer))
                } else {
                    emit(Resource.Error(Constants.ErrorMessages.NOT_FOUND))
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
            val errorMessage = when (e.code()) {
                401 -> Constants.ErrorMessages.UNAUTHORIZED
                404 -> Constants.ErrorMessages.NOT_FOUND
                408 -> Constants.ErrorMessages.TIMEOUT
                500, 502, 503 -> Constants.ErrorMessages.SERVER_ERROR
                else -> Constants.ErrorMessages.UNKNOWN_ERROR
            }
            emit(Resource.Error(errorMessage))
            
        } catch (e: IOException) {
            emit(Resource.Error(Constants.ErrorMessages.NETWORK_ERROR))
            
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ErrorMessages.UNKNOWN_ERROR))
        }
    }
}
