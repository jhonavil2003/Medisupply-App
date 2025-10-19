package com.misw.medisupply.domain.usecase.customer

import android.util.Log
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.repository.customer.CustomerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for customer registration
 * Handles business logic for registering new customers with validation
 */
class RegisterCustomerUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    
    companion object {
        private const val TAG = "RegisterCustomerUseCase"
    }
    
    /**
     * Register a new customer with validation
     * 
     * @param businessName Company/Institution name
     * @param documentNumber NIT/RUC or identification number
     * @param contactEmail Contact email address
     * @param contactPhone Contact phone number
     * @param address Physical address
     * @param city City (optional)
     * @param department Department/State (optional)
     * @return Flow with Resource containing the registered customer
     */
    operator fun invoke(
        businessName: String,
        documentNumber: String,
        contactEmail: String,
        contactPhone: String,
        address: String,
        city: String? = null,
        department: String? = null
    ): Flow<Resource<Customer>> {
        Log.d(TAG, "=== USECASE RECIBIÓ DATOS ===")
        Log.d(TAG, "businessName: '$businessName'")
        Log.d(TAG, "documentNumber: '$documentNumber'")
        Log.d(TAG, "contactEmail: '$contactEmail'")
        Log.d(TAG, "contactPhone: '$contactPhone'")
        Log.d(TAG, "address: '$address'")
        Log.d(TAG, "city: '$city'")
        Log.d(TAG, "department: '$department'")
        
        // Validate input data
        Log.d(TAG, "Iniciando validación de datos...")
        val validationError = validateInput(
            businessName = businessName,
            documentNumber = documentNumber,
            contactEmail = contactEmail,
            contactPhone = contactPhone,
            address = address
        )
        
        if (validationError != null) {
            Log.e(TAG, "Error de validación: $validationError")
            return kotlinx.coroutines.flow.flow {
                emit(Resource.Error(validationError))
            }
        }
        
        Log.d(TAG, "Validación exitosa - procediendo con registro en repository")
        
        // Proceed with registration
        val cleanData = mapOf(
            "businessName" to businessName.trim(),
            "documentNumber" to documentNumber.trim(),
            "contactEmail" to contactEmail.trim().lowercase(),
            "contactPhone" to contactPhone.trim(),
            "address" to address.trim(),
            "city" to city?.trim(),
            "department" to department?.trim()
        )
        Log.d(TAG, "Datos limpiados para repository: $cleanData")
        
        return customerRepository.registerCustomer(
            businessName = businessName.trim(),
            documentNumber = documentNumber.trim(),
            documentType = "NIT", // Default to NIT for institutions
            contactEmail = contactEmail.trim().lowercase(),
            contactPhone = contactPhone.trim(),
            address = address.trim(),
            city = city?.trim(),
            department = department?.trim(),
            customerType = "hospital" // Default customer type - lowercase as required by API
        )
    }
    
    /**
     * Validate document number availability
     */
    fun validateDocumentNumber(documentNumber: String): Flow<Resource<Boolean>> {
        if (documentNumber.isBlank()) {
            return kotlinx.coroutines.flow.flow {
                emit(Resource.Error("El número de documento es requerido"))
            }
        }
        
        val cleanDocumentNumber = documentNumber.trim().replace("-", "").replace(".", "")
        if (cleanDocumentNumber.length < 8) {
            return kotlinx.coroutines.flow.flow {
                emit(Resource.Error("El número de documento debe tener al menos 8 dígitos"))
            }
        }
        
        return customerRepository.validateDocumentNumber(cleanDocumentNumber, "NIT")
    }
    
    /**
     * Validate input data for customer registration
     */
    private fun validateInput(
        businessName: String,
        documentNumber: String,
        contactEmail: String,
        contactPhone: String,
        address: String
    ): String? {
        Log.d(TAG, "=== VALIDACIÓN DETALLADA ===")
        
        // Business name validation
        Log.d(TAG, "Validando businessName: '$businessName'")
        if (businessName.isBlank()) {
            Log.e(TAG, "businessName está en blanco")
            return "La razón social es requerida"
        }
        if (businessName.trim().length < 3) {
            Log.e(TAG, "businessName muy corto: ${businessName.trim().length} caracteres")
            return "La razón social debe tener al menos 3 caracteres"
        }
        Log.d(TAG, "businessName válido ✓")
        
        // Document number validation
        Log.d(TAG, "Validando documentNumber: '$documentNumber'")
        if (documentNumber.isBlank()) {
            Log.e(TAG, "documentNumber está en blanco")
            return "El NIT/RUC es requerido"
        }
        val cleanDocumentNumber = documentNumber.trim().replace("-", "").replace(".", "")
        Log.d(TAG, "documentNumber limpio: '$cleanDocumentNumber'")
        if (!cleanDocumentNumber.all { it.isDigit() }) {
            Log.e(TAG, "documentNumber contiene caracteres no numéricos")
            return "El NIT/RUC solo debe contener números"
        }
        if (cleanDocumentNumber.length < 8) {
            Log.e(TAG, "documentNumber muy corto: ${cleanDocumentNumber.length} dígitos")
            return "El NIT/RUC debe tener al menos 8 dígitos"
        }
        Log.d(TAG, "documentNumber válido ✓")
        
        // Email validation
        Log.d(TAG, "Validando contactEmail: '$contactEmail'")
        if (contactEmail.isBlank()) {
            Log.e(TAG, "contactEmail está en blanco")
            return "El correo electrónico es requerido"
        }
        if (!isValidEmail(contactEmail.trim())) {
            Log.e(TAG, "contactEmail formato inválido")
            return "El correo electrónico no tiene un formato válido"
        }
        Log.d(TAG, "contactEmail válido ✓")
        
        // Phone validation
        Log.d(TAG, "Validando contactPhone: '$contactPhone'")
        if (contactPhone.isBlank()) {
            Log.e(TAG, "contactPhone está en blanco")
            return "El teléfono es requerido"
        }
        val cleanPhone = contactPhone.trim().replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
        Log.d(TAG, "contactPhone limpio: '$cleanPhone'")
        if (cleanPhone.length < 7) {
            Log.e(TAG, "contactPhone muy corto: ${cleanPhone.length} dígitos")
            return "El teléfono debe tener al menos 7 dígitos"
        }
        if (!cleanPhone.all { it.isDigit() || it == '+' }) {
            Log.e(TAG, "contactPhone contiene caracteres inválidos")
            return "El teléfono solo debe contener números"
        }
        Log.d(TAG, "contactPhone válido ✓")
        
        // Address validation
        Log.d(TAG, "Validando address: '$address'")
        if (address.isBlank()) {
            Log.e(TAG, "address está en blanco")
            return "La dirección es requerida"
        }
        if (address.trim().length < 5) {
            Log.e(TAG, "address muy corto: ${address.trim().length} caracteres")
            return "La dirección debe tener al menos 5 caracteres"
        }
        Log.d(TAG, "address válido ✓")
        
        Log.d(TAG, "TODAS LAS VALIDACIONES PASARON ✓")
        return null
    }
    
    /**
     * Validate email format
     */
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }
}