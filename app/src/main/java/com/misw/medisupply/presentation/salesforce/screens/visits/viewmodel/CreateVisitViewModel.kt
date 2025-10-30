package com.misw.medisupply.presentation.salesforce.screens.visits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.presentation.salesforce.screens.visits.state.CreateVisitUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject
import java.time.LocalDate
import java.time.LocalTime

@HiltViewModel
class CreateVisitViewModel @Inject constructor(
    // TODO: Inject customer repository when created
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateVisitUiState())
    val uiState: StateFlow<CreateVisitUiState> = _uiState.asStateFlow()

    // Customer search methods
    fun searchCustomers(query: String) {
        android.util.Log.d("CreateVisitViewModel", "searchCustomers called with query: '$query'")
        
        _uiState.value = _uiState.value.copy(
            customerSearchQuery = query,
            showCustomerDropdown = query.length >= 2
        )
        
        if (query.length >= 2) {
            android.util.Log.d("CreateVisitViewModel", "Starting search for query: '$query'")
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isSearchingCustomers = true)
                try {
                    // TODO: Replace with actual repository call
                    delay(300) // Simulate network delay
                    
                    // Mock data for now
                    val mockResults = getMockCustomers().filter { 
                        it.businessName.contains(query, ignoreCase = true) 
                    }
                    
                    android.util.Log.d("CreateVisitViewModel", "Search completed. Found ${mockResults.size} results")
                    
                    val shouldShowDropdown = mockResults.isNotEmpty()
                    android.util.Log.d("CreateVisitViewModel", "Updating state - showDropdown: $shouldShowDropdown, results: ${mockResults.size}")
                    
                    _uiState.value = _uiState.value.copy(
                        customerSearchResults = mockResults,
                        isSearchingCustomers = false,
                        showCustomerDropdown = shouldShowDropdown
                    )
                    
                    android.util.Log.d("CreateVisitViewModel", "State updated - current showCustomerDropdown: ${_uiState.value.showCustomerDropdown}")
                } catch (e: Exception) {
                    android.util.Log.e("CreateVisitViewModel", "Search error: ${e.message}")
                    _uiState.value = _uiState.value.copy(
                        customerSearchResults = emptyList(),
                        isSearchingCustomers = false,
                        showCustomerDropdown = false,
                        error = "Error al buscar clientes: ${e.message}"
                    )
                }
            }
        } else {
            android.util.Log.d("CreateVisitViewModel", "Query too short, clearing results")
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
            showCustomerDropdown = false
        )
        validateForm()
    }
    
    fun clearCustomerSelection() {
        _uiState.value = _uiState.value.copy(
            selectedCustomer = null,
            customerSearchQuery = "",
            customerSearchResults = emptyList(),
            showCustomerDropdown = false
        )
        validateForm()
    }
    
    // Mock data - replace with actual repository call
    private fun getMockCustomers(): List<Customer> {
        return listOf(
            Customer(
                id = 1,
                documentType = com.misw.medisupply.domain.model.customer.DocumentType.NIT,
                documentNumber = "900123456-7",
                businessName = "Farmacia San Juan",
                tradeName = "Farmacia San Juan",
                customerType = com.misw.medisupply.domain.model.customer.CustomerType.FARMACIA,
                contactName = "María García",
                contactEmail = "maria@farmacia.com",
                contactPhone = "3001234567",
                address = "Calle 123 #45-67, Bogotá",
                city = "Bogotá",
                department = "Cundinamarca",
                country = "Colombia",
                creditLimit = 5000000.0,
                creditDays = 30,
                isActive = true,
                createdAt = null,
                updatedAt = null
            ),
            Customer(
                id = 2,
                documentType = com.misw.medisupply.domain.model.customer.DocumentType.NIT,
                documentNumber = "800234567-8",
                businessName = "Clínica El Rosario",
                tradeName = "Clínica El Rosario",
                customerType = com.misw.medisupply.domain.model.customer.CustomerType.CLINICA,
                contactName = "Dr. Carlos López",
                contactEmail = "carlos@clinica.com",
                contactPhone = "3007654321",
                address = "Carrera 45 #23-89, Medellín",
                city = "Medellín",
                department = "Antioquia",
                country = "Colombia",
                creditLimit = 10000000.0,
                creditDays = 45,
                isActive = true,
                createdAt = null,
                updatedAt = null
            ),
            Customer(
                id = 3,
                documentType = com.misw.medisupply.domain.model.customer.DocumentType.NIT,
                documentNumber = "700345678-9",
                businessName = "Hospital General",
                tradeName = "Hospital General del Valle",
                customerType = com.misw.medisupply.domain.model.customer.CustomerType.HOSPITAL,
                contactName = "Ana Rodríguez",
                contactEmail = "ana@hospital.com",
                contactPhone = "3009876543",
                address = "Avenida 80 #12-34, Cali",
                city = "Cali",
                department = "Valle del Cauca",
                country = "Colombia",
                creditLimit = 15000000.0,
                creditDays = 60,
                isActive = true,
                createdAt = null,
                updatedAt = null
            )
        )
    }

    fun updateContactedPersons(contactedPersons: String) {
        _uiState.value = _uiState.value.copy(contactedPersons = contactedPersons)
        validateForm()
    }

    fun updateClinicalFindings(clinicalFindings: String) {
        _uiState.value = _uiState.value.copy(clinicalFindings = clinicalFindings)
        validateForm()
    }

    fun updateAdditionalNotes(additionalNotes: String) {
        _uiState.value = _uiState.value.copy(additionalNotes = additionalNotes)
        validateForm()
    }

    fun updateVisitDate(visitDate: LocalDate) {
        _uiState.value = _uiState.value.copy(visitDate = visitDate)
        validateForm()
    }

    fun updateVisitTime(visitTime: LocalTime) {
        _uiState.value = _uiState.value.copy(visitTime = visitTime)
        validateForm()
    }

    fun updateAddress(address: String) {
        _uiState.value = _uiState.value.copy(address = address)
        validateForm()
    }

    private fun validateForm() {
        val state = _uiState.value
        val isValid = state.selectedCustomer != null &&
                state.contactedPersons.isNotBlank()
        
        _uiState.value = state.copy(isFormValid = isValid)
    }

    fun saveVisit() {
        if (!_uiState.value.isFormValid) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                // TODO: Save visit using repository
                delay(1000) // Simulate network call
                
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Error al guardar la visita"
                )
            }
        }
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}