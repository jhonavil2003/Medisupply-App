package com.misw.medisupply.presentation.salesforce.screens.visits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    // Inject repository when created
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateVisitUiState())
    val uiState: StateFlow<CreateVisitUiState> = _uiState.asStateFlow()

    fun updateCustomerName(customerName: String) {
        _uiState.value = _uiState.value.copy(customerName = customerName)
        validateForm()
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

    fun updateLocation(location: String) {
        _uiState.value = _uiState.value.copy(location = location)
        validateForm()
    }

    private fun validateForm() {
        val state = _uiState.value
        val isValid = state.customerName.isNotBlank() &&
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