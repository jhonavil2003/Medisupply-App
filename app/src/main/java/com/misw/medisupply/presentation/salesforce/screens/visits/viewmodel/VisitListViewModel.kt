package com.misw.medisupply.presentation.salesforce.screens.visits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.presentation.salesforce.screens.visits.state.VisitListUiState
import com.misw.medisupply.presentation.salesforce.screens.visits.state.Visit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@HiltViewModel
class VisitListViewModel @Inject constructor(
    // Inject repository when created
) : ViewModel() {

    private val _uiState = MutableStateFlow(VisitListUiState())
    val uiState: StateFlow<VisitListUiState> = _uiState.asStateFlow()

    init {
        loadVisits()
    }

    fun loadVisits() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // TODO: Load visits from repository - Using mock data for now
                val mockVisits = listOf(
                    Visit(
                        id = 1,
                        customerName = "Clínica Santa Fe",
                        visitDate = LocalDate.of(2025, 10, 24),
                        visitTime = LocalTime.of(9, 30),
                        contactedPersons = "Dr. Rodriguez",
                        clinicalFindings = "Revisión de equipos",
                        location = "Cra 7 #123-45, Bogotá"
                    ),
                    Visit(
                        id = 2,
                        customerName = "Hospital San José",
                        visitDate = LocalDate.of(2025, 10, 24),
                        visitTime = LocalTime.of(11, 0),
                        contactedPersons = "Dra. Martinez",
                        clinicalFindings = "Capacitación de personal",
                        location = "Cra 10 #456-78, Bogotá"
                    )
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    visits = mockVisits,
                    filteredVisits = mockVisits
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun searchVisits(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        val filteredVisits = if (query.isBlank()) {
            _uiState.value.visits
        } else {
            _uiState.value.visits.filter { visit ->
                visit.customerName.contains(query, ignoreCase = true) ||
                visit.contactedPersons.contains(query, ignoreCase = true)
            }
        }
        _uiState.value = _uiState.value.copy(filteredVisits = filteredVisits)
    }

    fun refreshVisits() {
        loadVisits()
    }
}