package com.misw.medisupply.data.repository.customer

import android.util.Log
import com.misw.medisupply.core.base.AppException
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.remote.api.customer.CustomerApiService
import com.misw.medisupply.data.remote.dto.customer.CreateCustomerRequest
import com.misw.medisupply.data.remote.dto.customer.toDomain
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.model.customer.DocumentType
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
    
    companion object {
        private const val TAG = "CustomerRepositoryImpl"
    }
    
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
    
    override fun validateDocumentNumber(
        documentNumber: String,
        documentType: String
    ): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading())
            
            val response = apiService.validateDocumentNumber(
                documentNumber = documentNumber,
                documentType = documentType
            )
            
            if (response.isSuccessful) {
                val validationResponse = response.body()
                if (validationResponse != null) {
                    emit(Resource.Success(validationResponse.exists))
                } else {
                    emit(Resource.Error(Constants.ErrorMessages.NO_DATA))
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Número de documento inválido"
                    404 -> "Servicio no disponible"
                    500 -> Constants.ErrorMessages.SERVER_ERROR
                    else -> "${Constants.ErrorMessages.SERVER_ERROR} (${response.code()})"
                }
                emit(Resource.Error(errorMessage))
            }
            
        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "Número de documento inválido"
                404 -> "Servicio de validación no disponible"
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
    
    override fun registerCustomer(
        businessName: String,
        documentNumber: String,
        documentType: String,
        contactEmail: String,
        contactPhone: String,
        address: String,
        city: String?,
        department: String?,
        customerType: String
    ): Flow<Resource<Customer>> = flow {
        try {
            Log.d(TAG, "=== REPOSITORY RECIBIÓ DATOS ===")
            Log.d(TAG, "businessName: '$businessName'")
            Log.d(TAG, "documentNumber: '$documentNumber'")
            Log.d(TAG, "documentType: '$documentType'")
            Log.d(TAG, "contactEmail: '$contactEmail'")
            Log.d(TAG, "contactPhone: '$contactPhone'")
            Log.d(TAG, "address: '$address'")
            Log.d(TAG, "city: '$city'")
            Log.d(TAG, "department: '$department'")
            Log.d(TAG, "customerType: '$customerType'")
            
            emit(Resource.Loading())
            
            val request = CreateCustomerRequest(
                businessName = businessName,
                documentNumber = documentNumber,
                documentType = documentType,
                contactEmail = contactEmail,
                contactPhone = contactPhone,
                address = address,
                city = city,
                department = department,
                customerType = customerType
            )
            
            Log.d(TAG, "Request creado: $request")
            Log.d(TAG, "Enviando petición HTTP POST a API...")
            
            val response = apiService.registerCustomer(request)
            
            Log.d(TAG, "Respuesta API recibida - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")
            
            if (response.isSuccessful) {
                val registrationResponse = response.body()
                Log.d(TAG, "Response body: $registrationResponse")
                
                Log.d(TAG, "✅ Cliente registrado exitosamente en el backend")
                
                // Create customer object with the data we sent (since API response may not have complete info)
                val registeredCustomer = Customer(
                    id = registrationResponse?.id?.takeIf { it > 0 } ?: System.currentTimeMillis().toInt(), // Generate temp ID
                    documentType = DocumentType.fromString(documentType),
                    documentNumber = documentNumber,
                    businessName = businessName,
                    tradeName = null,
                    customerType = CustomerType.fromString(customerType),
                    contactName = null,
                    contactEmail = contactEmail,
                    contactPhone = contactPhone,
                    address = address,
                    city = city,
                    department = department,
                    country = "Colombia",
                    creditLimit = 0.0,
                    creditDays = 0,
                    isActive = true,
                    createdAt = null,
                    updatedAt = null
                )
                
                Log.d(TAG, "✅ Cliente objeto creado exitosamente: $registeredCustomer")
                emit(Resource.Success(registeredCustomer))
            } else {
                Log.e(TAG, "API respondió con error: ${response.code()}")
                Log.e(TAG, "Error body: ${response.errorBody()?.string()}")
                val errorMessage = when (response.code()) {
                    400 -> "Datos inválidos. Verifique la información ingresada"
                    409 -> "El NIT/RUC ya está registrado en el sistema"
                    422 -> "Email inválido o datos incompletos"
                    500 -> Constants.ErrorMessages.SERVER_ERROR
                    else -> "${Constants.ErrorMessages.SERVER_ERROR} (${response.code()})"
                }
                emit(Resource.Error(errorMessage))
            }
            
        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "Datos inválidos. Verifique la información ingresada"
                409 -> "El NIT/RUC ya está registrado en el sistema"
                422 -> "Email inválido o datos incompletos"
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
