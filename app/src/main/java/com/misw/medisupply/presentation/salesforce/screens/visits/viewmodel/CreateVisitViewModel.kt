package com.misw.medisupply.presentation.salesforce.screens.visits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.presentation.salesforce.screens.visits.state.CreateVisitUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject
import java.time.LocalDate
import java.time.LocalTime

@HiltViewModel
class CreateVisitViewModel @Inject constructor(
    private val createVisitUseCase: com.misw.medisupply.domain.usecase.visit.CreateVisitUseCase,
    private val updateVisitUseCase: com.misw.medisupply.domain.usecase.visit.UpdateVisitUseCase,
    private val completeVisitUseCase: com.misw.medisupply.domain.usecase.visit.CompleteVisitUseCase,
    private val getCustomersUseCase: com.misw.medisupply.domain.usecase.customer.GetCustomersUseCase,
    private val userSessionManager: com.misw.medisupply.core.session.UserSessionManager,
    private val uploadFileUseCase: com.misw.medisupply.domain.usecase.visit.UploadFileUseCase,
    private val getVisitFilesUseCase: com.misw.medisupply.domain.usecase.visit.GetVisitFilesUseCase,
    private val deleteFileUseCase: com.misw.medisupply.domain.usecase.visit.DeleteFileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateVisitUiState())
    val uiState: StateFlow<CreateVisitUiState> = _uiState.asStateFlow()
    
    // Job para el debounce del auto-save
    private var autoSaveJob: Job? = null
    private val autoSaveDelayMs = 1500L // 1.5 segundos de delay
    
    init {
        // Ejecutar validación inicial para configurar correctamente isFormValid
        validateForm()
    }

    // Customer search methods
    fun searchCustomers(query: String) {
        // Log: searchCustomers called with query: $query
        
        _uiState.value = _uiState.value.copy(
            customerSearchQuery = query,
            showCustomerDropdown = query.isNotBlank()
        )
        
        if (query.isNotBlank()) {
            // Log: Starting search for query: $query
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isSearchingCustomers = true)
                try {
                    // Get all customers from backend
                    getCustomersUseCase().collect { resource ->
                        when (resource) {
                            is com.misw.medisupply.core.base.Resource.Success -> {
                                val allCustomers = resource.data ?: emptyList()
                                val filteredResults = allCustomers.filter { customer ->
                                    customer.businessName.contains(query, ignoreCase = true) ||
                                    customer.tradeName?.contains(query, ignoreCase = true) == true ||
                                    customer.contactName?.contains(query, ignoreCase = true) == true ||
                                    customer.documentNumber.contains(query, ignoreCase = true)
                                }
                                
                                // Log: Search completed. Found ${filteredResults.size} results
                                
                                val shouldShowDropdown = filteredResults.isNotEmpty()
                                // Log: Updating state - showDropdown: $shouldShowDropdown, results: ${filteredResults.size}
                                
                                _uiState.value = _uiState.value.copy(
                                    customerSearchResults = filteredResults,
                                    isSearchingCustomers = false,
                                    showCustomerDropdown = shouldShowDropdown
                                )
                            }
                            is com.misw.medisupply.core.base.Resource.Error -> {
                                // Log: Failed to get customers: ${resource.message}
                                _uiState.value = _uiState.value.copy(
                                    customerSearchResults = emptyList(),
                                    isSearchingCustomers = false,
                                    showCustomerDropdown = false,
                                    error = "Error al obtener clientes: ${resource.message}"
                                )
                            }
                            is com.misw.medisupply.core.base.Resource.Loading -> {
                                // Ya estamos mostrando loading con isSearchingCustomers = true
                            }
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        customerSearchResults = emptyList(),
                        isSearchingCustomers = false,
                        showCustomerDropdown = false,
                        error = "Error al buscar clientes: ${e.message}"
                    )
                }
            }
        } else {
            // Log: Query too short, clearing results
            _uiState.value = _uiState.value.copy(
                customerSearchResults = emptyList(),
                showCustomerDropdown = false
            )
        }
    }
    
    fun selectCustomer(customer: Customer) {
        _uiState.value = _uiState.value.copy(
            selectedCustomer = customer,
            customerSearchQuery = "",
            customerSearchResults = emptyList(),
            showCustomerDropdown = false,
            isCustomerSelected = true
        )
        validateForm()
    }
    
    fun clearCustomerSelection() {
        _uiState.value = _uiState.value.copy(
            selectedCustomer = null,
            customerSearchQuery = "",
            customerSearchResults = emptyList(),
            showCustomerDropdown = false,
            isCustomerSelected = false,
            areVisitFieldsComplete = false,
            isVisitSaved = false,
            hasModifiedDate = false,
            hasModifiedTime = false
        )
        validateForm()
    }
    


    fun updateContactedPersons(contactedPersons: String) {
        _uiState.value = _uiState.value.copy(contactedPersons = contactedPersons)
        validateForm()
        
        // Auto-actualizar con debounce si ya existe una visita guardada
        if (_uiState.value.isVisitSaved && _uiState.value.createdVisitId != null) {
            scheduleAutoSave()
        }
    }

    fun updateClinicalFindings(clinicalFindings: String) {
        _uiState.value = _uiState.value.copy(clinicalFindings = clinicalFindings)
        validateForm()
        
        // Auto-actualizar con debounce si ya existe una visita guardada
        if (_uiState.value.isVisitSaved && _uiState.value.createdVisitId != null) {
            scheduleAutoSave()
        }
    }

    fun updateAdditionalNotes(additionalNotes: String) {
        _uiState.value = _uiState.value.copy(additionalNotes = additionalNotes)
        validateForm()
        
        // Auto-actualizar con debounce si ya existe una visita guardada
        if (_uiState.value.isVisitSaved && _uiState.value.createdVisitId != null) {
            scheduleAutoSave()
        }
    }

    fun updateVisitDate(visitDate: LocalDate) {
        _uiState.value = _uiState.value.copy(
            visitDate = visitDate,
            hasModifiedDate = true
        )
        validateForm()
    }

    fun updateVisitTime(visitTime: LocalTime) {
        _uiState.value = _uiState.value.copy(
            visitTime = visitTime,
            hasModifiedTime = true
        )
        validateForm()
    }

    fun updateAddress(address: String) {
        _uiState.value = _uiState.value.copy(address = address)
        validateForm()
        
        // Auto-actualizar con debounce si ya existe una visita guardada
        if (_uiState.value.isVisitSaved && _uiState.value.createdVisitId != null) {
            scheduleAutoSave()
        }
    }

    private fun validateForm() {
        val state = _uiState.value
        
        // Requiere: Cliente + Fecha confirmada + Hora confirmada (interacción explícita del usuario)
        val areEssentialFieldsComplete = state.selectedCustomer != null && 
                                        state.hasModifiedDate && 
                                        state.hasModifiedTime
        
        // Check if ALL visit fields are complete (para el indicador de progreso)
        val areVisitFieldsComplete = state.selectedCustomer != null &&
                state.contactedPersons.isNotBlank() &&
                state.clinicalFindings.isNotBlank()
        
        // Form is valid when essential fields are complete (Cliente + Fecha + Hora)
        val isFormValid = areEssentialFieldsComplete
        
        _uiState.value = state.copy(
            areVisitFieldsComplete = areVisitFieldsComplete,
            isFormValid = isFormValid
        )
    }

    fun saveVisit() {
        if (!_uiState.value.isFormValid) return
        
        val state = _uiState.value
        val customer = state.selectedCustomer ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                // Obtener vendedor de la sesión
                val salesperson = try {
                    userSessionManager.requireSalesperson()
                } catch (e: IllegalStateException) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = "Error: Usuario no autenticado"
                    )
                    return@launch
                }
                
                val visit = com.misw.medisupply.domain.model.visit.Visit(
                    customerId = customer.id,
                    salespersonId = salesperson.id,
                    visitDate = state.visitDate,
                    visitTime = state.visitTime,
                    contactedPersons = state.contactedPersons.takeIf { it.isNotBlank() },
                    clinicalFindings = state.clinicalFindings.takeIf { it.isNotBlank() },
                    additionalNotes = state.additionalNotes.takeIf { it.isNotBlank() },
                    address = state.address.takeIf { it.isNotBlank() },
                    latitude = state.latitude,
                    longitude = state.longitude
                )
                
                val result = createVisitUseCase(visit)
                
                if (result.isSuccess) {
                    val createdVisit = result.getOrNull()
                    // Log: Visit created successfully: ${createdVisit?.id}
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        saveSuccess = true,
                        isVisitSaved = true,
                        createdVisitId = createdVisit?.id
                    )
                    // Cargar archivos existentes (si los hay)
                    if (createdVisit?.id != null) {
                        loadVisitFiles()
                    }
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    // Log: Failed to create visit: $error
                    val enhancedError = if (error.contains("Vendedor no encontrado") || error.contains("404")) {
                        "Vendedor no encontrado (ID: ${salesperson.id}). Verifique la configuración del usuario."
                    } else {
                        "Error al guardar la visita: $error"
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = enhancedError
                    )
                }
            } catch (e: Exception) {
                // Log: Exception saving visit: ${e.message}
                val enhancedError = if (e.message?.contains("Vendedor no encontrado") == true || e.message?.contains("404") == true) {
                    try {
                        val salesperson = userSessionManager.requireSalesperson()
                        "Vendedor no encontrado en el sistema (ID: ${salesperson.id}, Nombre: ${salesperson.fullName}). Contacte al administrador."
                    } catch (sessionException: IllegalStateException) {
                        "Error de autenticación: ${sessionException.message}"
                    }
                } else {
                    e.message ?: "Error al guardar la visita"
                }
                
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = enhancedError
                )
            }
        }
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false, successMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Programa el auto-guardado con debounce para evitar múltiples peticiones
     */
    private fun scheduleAutoSave() {
        // Cancelar el job anterior si existe
        autoSaveJob?.cancel()
        
        autoSaveJob = viewModelScope.launch {
            delay(autoSaveDelayMs)
            autoSaveVisitChanges()
        }
    }
    
    private fun autoSaveVisitChanges() {
        val state = _uiState.value
        val visitId = state.createdVisitId ?: return
        val customer = state.selectedCustomer ?: return
        
        // Evitar guardado múltiple simultáneo
        if (state.isAutoSaving || state.isSaving) return
        
        viewModelScope.launch {
            try {
                val salesperson = userSessionManager.requireSalesperson()
                
                val updatedVisit = com.misw.medisupply.domain.model.visit.Visit(
                    id = visitId,
                    customerId = customer.id,
                    salespersonId = salesperson.id,
                    visitDate = state.visitDate,
                    visitTime = state.visitTime,
                    contactedPersons = state.contactedPersons.takeIf { it.isNotBlank() },
                    clinicalFindings = state.clinicalFindings.takeIf { it.isNotBlank() },
                    additionalNotes = state.additionalNotes.takeIf { it.isNotBlank() },
                    address = state.address.takeIf { it.isNotBlank() },
                    latitude = state.latitude,
                    longitude = state.longitude
                )
                
                _uiState.value = _uiState.value.copy(isAutoSaving = true)
                val result = updateVisitUseCase(visitId, updatedVisit)
                
                if (result.isSuccess) {
                    // Feedback sutil de auto-guardado exitoso
                    _uiState.value = _uiState.value.copy(
                        isAutoSaving = false,
                        saveSuccess = true
                    )
                    
                    // Limpiar el éxito después de un breve período
                    viewModelScope.launch {
                        delay(1000) // Mostrar éxito por 1 segundo
                        _uiState.value = _uiState.value.copy(saveSuccess = false)
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isAutoSaving = false,
                        error = "Error al auto-guardar: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAutoSaving = false,
                    error = "Error al auto-guardar: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Switch to different salesperson for testing
     * TODO: Remove this method when real authentication is implemented
     */
    fun switchSalesperson(salespersonId: Int) {
        userSessionManager.switchToSalesperson(salespersonId)
    }
    
    /**
     * Get current salesperson info for debugging
     */
    fun getCurrentSalespersonInfo(): String {
        return try {
            val salesperson = userSessionManager.requireSalesperson()
            "Vendedor actual: ${salesperson.fullName} (ID: ${salesperson.id})"
        } catch (e: IllegalStateException) {
            "Sin vendedor configurado"
        }
    }
    
    /**
     * Complete the current visit by changing its status to COMPLETADA
     */
    fun completeVisit() {
        val state = _uiState.value
        val visitId = state.visitId ?: return
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)
                
                val result = completeVisitUseCase(visitId)
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        saveSuccess = true,
                        successMessage = "Visita completada exitosamente"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = "Error al completar visita: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Error al completar visita: ${e.message}"
                )
            }
        }
    }
    
    // ================================
    // MÉTODOS PARA ARCHIVOS
    // ================================
    
    /**
     * Cargar archivos de la visita
     */
    fun loadVisitFiles() {
        val visitId = _uiState.value.createdVisitId ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingFiles = true, fileError = null)
            
            getVisitFilesUseCase(visitId).collect { resource ->
                when (resource) {
                    is com.misw.medisupply.core.base.Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoadingFiles = true)
                    }
                    is com.misw.medisupply.core.base.Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoadingFiles = false,
                            visitFiles = resource.data ?: emptyList(),
                            fileError = null
                        )
                    }
                    is com.misw.medisupply.core.base.Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoadingFiles = false,
                            fileError = resource.message
                        )
                    }
                }
            }
        }
    }
    
    /**
     * Subir archivo a la visita
     */
    fun uploadFile(file: java.io.File, originalFileName: String? = null) {
        val visitId = _uiState.value.createdVisitId ?: return
        val displayName = originalFileName ?: file.name
        
        android.util.Log.d("CreateVisitViewModel", "Subiendo archivo: $displayName (temp: ${file.name}), tamaño: ${file.length()} bytes, visitId: $visitId")
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingFile = true, fileError = null)
            
            uploadFileUseCase(visitId, file, originalFileName).collect { resource ->
                when (resource) {
                    is com.misw.medisupply.core.base.Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isUploadingFile = true)
                    }
                    is com.misw.medisupply.core.base.Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isUploadingFile = false,
                            fileError = null
                        )
                        // Recargar la lista de archivos después de subir
                        loadVisitFiles()
                    }
                    is com.misw.medisupply.core.base.Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isUploadingFile = false,
                            fileError = resource.message
                        )
                    }
                }
            }
        }
    }
    
    /**
     * Eliminar archivo de la visita
     */
    fun deleteFile(fileId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeletingFile = true, fileError = null)
            
            deleteFileUseCase(fileId).collect { resource ->
                when (resource) {
                    is com.misw.medisupply.core.base.Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isDeletingFile = true)
                    }
                    is com.misw.medisupply.core.base.Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isDeletingFile = false,
                            fileError = null
                        )
                        // Recargar la lista de archivos después de eliminar
                        loadVisitFiles()
                    }
                    is com.misw.medisupply.core.base.Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isDeletingFile = false,
                            fileError = resource.message
                        )
                    }
                }
            }
        }
    }
    
    /**
     * Limpiar errores de archivos
     */
    fun clearFileError() {
        _uiState.value = _uiState.value.copy(fileError = null)
    }
}