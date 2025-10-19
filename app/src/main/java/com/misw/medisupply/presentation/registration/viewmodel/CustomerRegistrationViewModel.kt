package com.misw.medisupply.presentation.registration.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.remote.dto.customer.CreateCustomerRequest
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.usecase.customer.RegisterCustomerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Customer Registration Screen
 */
@HiltViewModel
class CustomerRegistrationViewModel @Inject constructor(
    private val registerCustomerUseCase: RegisterCustomerUseCase
) : ViewModel() {
    
    companion object {
        private const val TAG = "CustomerRegistrationVM"
    }
    
    private val _uiState = MutableStateFlow(CustomerRegistrationUiState())
    val uiState: StateFlow<CustomerRegistrationUiState> = _uiState.asStateFlow()
    
    private var validationJob: Job? = null
    
    /**
     * Update business name and validate
     */
    fun updateBusinessName(businessName: String) {
        _uiState.value = _uiState.value.copy(
            businessName = businessName,
            businessNameError = null,
            generalError = null
        )
        validateBusinessName(businessName)
    }
    
    /**
     * Update document number and validate
     */
    fun updateDocumentNumber(documentNumber: String) {
        _uiState.value = _uiState.value.copy(
            documentNumber = documentNumber,
            documentNumberError = null,
            isDocumentValidated = false,
            generalError = null
        )
        validateDocumentNumber(documentNumber)
    }
    
    /**
     * Update contact email and validate
     */
    fun updateContactEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            contactEmail = email,
            contactEmailError = null,
            generalError = null
        )
        validateEmail(email)
    }
    
    /**
     * Update contact phone and validate
     */
    fun updateContactPhone(phone: String) {
        _uiState.value = _uiState.value.copy(
            contactPhone = phone,
            contactPhoneError = null,
            generalError = null
        )
        validatePhone(phone)
    }
    
    /**
     * Update address and validate
     */
    fun updateAddress(address: String) {
        _uiState.value = _uiState.value.copy(
            address = address,
            addressError = null,
            generalError = null
        )
        validateAddress(address)
    }
    
    /**
     * Update city
     */
    fun updateCity(city: String) {
        _uiState.value = _uiState.value.copy(
            city = city,
            generalError = null
        )
    }
    
    /**
     * Update department
     */
    fun updateDepartment(department: String) {
        _uiState.value = _uiState.value.copy(
            department = department,
            generalError = null
        )
    }
    
    /**
     * Update internal code
     */
    fun updateInternalCode(internalCode: String) {
        _uiState.value = _uiState.value.copy(internalCode = internalCode)
    }
    
    /**
     * Update username
     */
    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }
    
    /**
     * Update password
     */
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }
    
    /**
     * Register customer
     */
    fun registerCustomer() {
        Log.d(TAG, "=== INICIANDO REGISTRO DE CLIENTE ===")
        val currentState = _uiState.value
        
        Log.d(TAG, "Estado actual del formulario:")
        Log.d(TAG, "businessName: '${currentState.businessName}'")
        Log.d(TAG, "documentNumber: '${currentState.documentNumber}'")
        Log.d(TAG, "contactEmail: '${currentState.contactEmail}'")
        Log.d(TAG, "contactPhone: '${currentState.contactPhone}'")
        Log.d(TAG, "address: '${currentState.address}'")
        Log.d(TAG, "city: '${currentState.city}'")
        Log.d(TAG, "department: '${currentState.department}'")
        Log.d(TAG, "internalCode: '${currentState.internalCode}'")
        Log.d(TAG, "username: '${currentState.username}'")
        Log.d(TAG, "password: '${currentState.password}'")
        
        // Force validation of all fields before submitting
        Log.d(TAG, "Ejecutando validaciones...")
        validateBusinessName(currentState.businessName)
        validateDocumentNumber(currentState.documentNumber)
        validateEmail(currentState.contactEmail)
        validatePhone(currentState.contactPhone)
        validateAddress(currentState.address)
        
        // Wait a moment for validations to complete
        viewModelScope.launch {
            kotlinx.coroutines.delay(100) // Small delay to ensure validations are processed
            
            val updatedState = _uiState.value
            
            Log.d(TAG, "Errores de validación:")
            Log.d(TAG, "businessNameError: ${updatedState.businessNameError}")
            Log.d(TAG, "documentNumberError: ${updatedState.documentNumberError}")
            Log.d(TAG, "contactEmailError: ${updatedState.contactEmailError}")
            Log.d(TAG, "contactPhoneError: ${updatedState.contactPhoneError}")
            Log.d(TAG, "addressError: ${updatedState.addressError}")
            Log.d(TAG, "isValidatingDocument: ${updatedState.isValidatingDocument}")
            Log.d(TAG, "isFormValid: ${isFormValid()}")
            
            // Final validation check
            if (!isFormValid()) {
                Log.e(TAG, "Formulario no válido - no se puede continuar")
                _uiState.value = _uiState.value.copy(
                    generalError = "Por favor complete todos los campos requeridos correctamente"
                )
                return@launch
            }
            
            Log.d(TAG, "Formulario válido - procediendo con el registro...")
            
            val registrationData = CreateCustomerRequest(
                businessName = updatedState.businessName,
                documentNumber = updatedState.documentNumber,
                contactEmail = updatedState.contactEmail,
                contactPhone = updatedState.contactPhone,
                address = updatedState.address,
                city = updatedState.city.takeIf { it.isNotBlank() },
                department = updatedState.department.takeIf { it.isNotBlank() }
            )
            
            Log.d(TAG, "Datos a enviar al UseCase:")
            Log.d(TAG, "businessName: '${registrationData.businessName}'")
            Log.d(TAG, "documentNumber: '${registrationData.documentNumber}'")
            Log.d(TAG, "contactEmail: '${registrationData.contactEmail}'")
            Log.d(TAG, "contactPhone: '${registrationData.contactPhone}'")
            Log.d(TAG, "address: '${registrationData.address}'")
            Log.d(TAG, "city: '${registrationData.city}'")
            Log.d(TAG, "department: '${registrationData.department}'")
            
            registerCustomerUseCase(
                businessName = updatedState.businessName,
                documentNumber = updatedState.documentNumber,
                contactEmail = updatedState.contactEmail,
                contactPhone = updatedState.contactPhone,
                address = updatedState.address,
                city = updatedState.city.takeIf { it.isNotBlank() },
                department = updatedState.department.takeIf { it.isNotBlank() }
            ).collect { resource ->
                Log.d(TAG, "Respuesta del UseCase: ${resource.javaClass.simpleName}")
                when (resource) {
                    is Resource.Loading -> {
                        Log.d(TAG, "UseCase responde: Loading - mostrando indicador de carga")
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            generalError = null
                        )
                    }
                    is Resource.Success -> {
                        Log.d(TAG, "UseCase responde: Success - registro exitoso")
                        Log.d(TAG, "Cliente registrado: ${resource.data}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            registeredCustomer = resource.data,
                            isRegistrationComplete = true
                        )
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "UseCase responde: Error - ${resource.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            generalError = resource.message
                        )
                    }
                }
            }
        }
    }
    
    /**
     * Clear general error
     */
    fun clearGeneralError() {
        _uiState.value = _uiState.value.copy(generalError = null)
    }
    
    /**
     * Validate business name
     */
    private fun validateBusinessName(businessName: String) {
        Log.d(TAG, "Validando businessName: '$businessName'")
        val error = when {
            businessName.isBlank() -> {
                Log.d(TAG, "businessName error: está en blanco")
                "La razón social es requerida"
            }
            businessName.trim().length < 3 -> {
                Log.d(TAG, "businessName error: muy corto (${businessName.trim().length} caracteres)")
                "La razón social debe tener al menos 3 caracteres"
            }
            else -> {
                Log.d(TAG, "businessName válido ✓")
                null
            }
        }
        _uiState.value = _uiState.value.copy(businessNameError = error)
    }
    
    /**
     * Validate document number with debouncing
     */
    private fun validateDocumentNumber(documentNumber: String) {
        Log.d(TAG, "Validando documentNumber: '$documentNumber'")
        validationJob?.cancel()
        
        val cleanDocument = documentNumber.trim().replace("-", "").replace(".", "")
        Log.d(TAG, "documentNumber limpio: '$cleanDocument'")
        
        val error = when {
            documentNumber.isBlank() -> {
                Log.d(TAG, "documentNumber error: está en blanco")
                "El NIT/RUC es requerido"
            }
            !cleanDocument.all { it.isDigit() } -> {
                Log.d(TAG, "documentNumber error: contiene caracteres no numéricos")
                "El NIT/RUC solo debe contener números"
            }
            cleanDocument.length < 8 -> {
                Log.d(TAG, "documentNumber error: muy corto (${cleanDocument.length} dígitos)")
                "El NIT/RUC debe tener al menos 8 dígitos"
            }
            else -> {
                Log.d(TAG, "documentNumber formato válido ✓")
                null
            }
        }
        
        _uiState.value = _uiState.value.copy(documentNumberError = error)
        
        // If no validation error, check if document exists (with debouncing)
        if (error == null && cleanDocument.length >= 8) {
            validationJob = viewModelScope.launch {
                delay(800) // Debounce for 800ms
                checkDocumentAvailability(cleanDocument)
            }
        }
    }
    
    /**
     * Check document availability
     */
    private suspend fun checkDocumentAvailability(documentNumber: String) {
        _uiState.value = _uiState.value.copy(isValidatingDocument = true)
        
        registerCustomerUseCase.validateDocumentNumber(documentNumber).collect { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isValidatingDocument = true)
                }
                is Resource.Success -> {
                    val exists = resource.data ?: false
                    _uiState.value = _uiState.value.copy(
                        isValidatingDocument = false,
                        isDocumentValidated = true,
                        documentNumberError = if (exists) "Este NIT/RUC ya está registrado" else null
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isValidatingDocument = false,
                        documentNumberError = resource.message
                    )
                }
            }
        }
    }
    
    /**
     * Validate email
     */
    private fun validateEmail(email: String) {
        Log.d(TAG, "Validando email: '$email'")
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        val error = when {
            email.isBlank() -> {
                Log.d(TAG, "email error: está en blanco")
                "El correo electrónico es requerido"
            }
            !email.trim().matches(emailRegex.toRegex()) -> {
                Log.d(TAG, "email error: formato inválido")
                "El correo electrónico no tiene un formato válido"
            }
            else -> {
                Log.d(TAG, "email válido ✓")
                null
            }
        }
        _uiState.value = _uiState.value.copy(contactEmailError = error)
    }
    
    /**
     * Validate phone
     */
    private fun validatePhone(phone: String) {
        Log.d(TAG, "Validando phone: '$phone'")
        val cleanPhone = phone.trim().replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
        Log.d(TAG, "phone limpio: '$cleanPhone'")
        val error = when {
            phone.isBlank() -> {
                Log.d(TAG, "phone error: está en blanco")
                "El teléfono es requerido"
            }
            cleanPhone.length < 7 -> {
                Log.d(TAG, "phone error: muy corto (${cleanPhone.length} dígitos)")
                "El teléfono debe tener al menos 7 dígitos"
            }
            !cleanPhone.all { it.isDigit() || it == '+' } -> {
                Log.d(TAG, "phone error: contiene caracteres inválidos")
                "El teléfono solo debe contener números"
            }
            else -> {
                Log.d(TAG, "phone válido ✓")
                null
            }
        }
        _uiState.value = _uiState.value.copy(contactPhoneError = error)
    }
    
    /**
     * Validate address
     */
    private fun validateAddress(address: String) {
        Log.d(TAG, "Validando address: '$address'")
        val error = when {
            address.isBlank() -> {
                Log.d(TAG, "address error: está en blanco")
                "La dirección es requerida"
            }
            address.trim().length < 5 -> {
                Log.d(TAG, "address error: muy corto (${address.trim().length} caracteres)")
                "La dirección debe tener al menos 5 caracteres"
            }
            else -> {
                Log.d(TAG, "address válido ✓")
                null
            }
        }
        _uiState.value = _uiState.value.copy(addressError = error)
    }
    
    /**
     * Check if form is valid
     */
    private fun isFormValid(): Boolean {
        val currentState = _uiState.value
        
        Log.d(TAG, "=== VALIDACIÓN DE FORMULARIO ===")
        Log.d(TAG, "businessNameError == null: ${currentState.businessNameError == null} (valor: ${currentState.businessNameError})")
        Log.d(TAG, "documentNumberError == null: ${currentState.documentNumberError == null} (valor: ${currentState.documentNumberError})")
        Log.d(TAG, "contactEmailError == null: ${currentState.contactEmailError == null} (valor: ${currentState.contactEmailError})")
        Log.d(TAG, "contactPhoneError == null: ${currentState.contactPhoneError == null} (valor: ${currentState.contactPhoneError})")
        Log.d(TAG, "addressError == null: ${currentState.addressError == null} (valor: ${currentState.addressError})")
        Log.d(TAG, "businessName.isNotBlank(): ${currentState.businessName.isNotBlank()} (valor: '${currentState.businessName}')")
        Log.d(TAG, "documentNumber.isNotBlank(): ${currentState.documentNumber.isNotBlank()} (valor: '${currentState.documentNumber}')")
        Log.d(TAG, "contactEmail.isNotBlank(): ${currentState.contactEmail.isNotBlank()} (valor: '${currentState.contactEmail}')")
        Log.d(TAG, "contactPhone.isNotBlank(): ${currentState.contactPhone.isNotBlank()} (valor: '${currentState.contactPhone}')")
        Log.d(TAG, "address.isNotBlank(): ${currentState.address.isNotBlank()} (valor: '${currentState.address}')")
        Log.d(TAG, "!isValidatingDocument: ${!currentState.isValidatingDocument} (isValidatingDocument: ${currentState.isValidatingDocument})")
        
        val isValid = currentState.businessNameError == null &&
                currentState.documentNumberError == null &&
                currentState.contactEmailError == null &&
                currentState.contactPhoneError == null &&
                currentState.addressError == null &&
                currentState.businessName.isNotBlank() &&
                currentState.documentNumber.isNotBlank() &&
                currentState.contactEmail.isNotBlank() &&
                currentState.contactPhone.isNotBlank() &&
                currentState.address.isNotBlank() &&
                !currentState.isValidatingDocument
                
        Log.d(TAG, "RESULTADO FINAL isFormValid(): $isValid")
        return isValid
    }
}

/**
 * UI State for Customer Registration
 */
data class CustomerRegistrationUiState(
    // Form fields
    val businessName: String = "",
    val documentNumber: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val address: String = "",
    val city: String = "",
    val department: String = "",
    val internalCode: String = "",
    val username: String = "",
    val password: String = "",
    
    // Validation errors
    val businessNameError: String? = null,
    val documentNumberError: String? = null,
    val contactEmailError: String? = null,
    val contactPhoneError: String? = null,
    val addressError: String? = null,
    val generalError: String? = null,
    
    // States
    val isLoading: Boolean = false,
    val isValidatingDocument: Boolean = false,
    val isDocumentValidated: Boolean = false,
    val isRegistrationComplete: Boolean = false,
    val registeredCustomer: Customer? = null
) {
    val isFormValid: Boolean
        get() = businessNameError == null &&
                documentNumberError == null &&
                contactEmailError == null &&
                contactPhoneError == null &&
                addressError == null &&
                businessName.isNotBlank() &&
                documentNumber.isNotBlank() &&
                contactEmail.isNotBlank() &&
                contactPhone.isNotBlank() &&
                address.isNotBlank() &&
                !isValidatingDocument
}