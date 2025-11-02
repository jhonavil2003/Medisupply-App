package com.misw.medisupply.presentation.salesforce.screens.visits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.presentation.salesforce.screens.visits.state.VisitUiState
import com.misw.medisupply.presentation.salesforce.screens.visits.state.Visit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisitViewModel @Inject constructor(
    // Inject repository when created
) : ViewModel() {

    private val _uiState = MutableStateFlow(VisitUiState())
    val uiState: StateFlow<VisitUiState> = _uiState.asStateFlow()

    fun loadVisits() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // TODO: Load visits from repository
                val mockVisits = emptyList<Visit>()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    visits = mockVisits
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun refreshVisits() {
        loadVisits()
    }
}