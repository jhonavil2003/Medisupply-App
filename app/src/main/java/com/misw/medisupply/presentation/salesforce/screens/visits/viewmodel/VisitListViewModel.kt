package com.misw.medisupply.presentation.salesforce.screens.visits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.core.i18n.LocaleManager
import com.misw.medisupply.core.session.UserSessionManager
import com.misw.medisupply.domain.usecase.customer.GetCustomersByIdsUseCase
import com.misw.medisupply.domain.usecase.visit.GetVisitsUseCase
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
import java.time.format.DateTimeFormatter

@HiltViewModel
class VisitListViewModel @Inject constructor(
    val localeManager: LocaleManager,
    private val getVisitsUseCase: GetVisitsUseCase,
    private val getCustomersByIdsUseCase: GetCustomersByIdsUseCase,
    private val userSessionManager: UserSessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VisitListUiState())
    val uiState: StateFlow<VisitListUiState> = _uiState.asStateFlow()

    init {
        loadVisits()
    }

    fun loadVisits(customerId: Int? = null, status: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val salespersonId = userSessionManager.requireSalespersonId()

                val result = getVisitsUseCase(
                    customerId = customerId,
                    salespersonId = salespersonId,
                    status = status
                )
                
                result.fold(
                    onSuccess = { domainVisits ->
                        // Obtener IDs únicos de clientes
                        val customerIds = domainVisits.map { it.customerId }.distinct()
                        
                        // Obtener información de clientes
                        getCustomersByIdsUseCase(customerIds).collect { customersResource ->
                            when (customersResource) {
                                is Resource.Success -> {
                                    val customersMap = customersResource.data ?: emptyMap()
                                    
                                    val visits = domainVisits.map { domainVisit ->
                                        val customer = customersMap[domainVisit.customerId]
                                        Visit(
                                            id = domainVisit.id,
                                            customerName = customer?.getDisplayName() 
                                                ?: "Cliente ${domainVisit.customerId}",
                                            visitDate = domainVisit.visitDate,
                                            visitTime = domainVisit.visitTime,
                                            contactedPersons = domainVisit.contactedPersons ?: "",
                                            clinicalFindings = domainVisit.clinicalFindings ?: "",
                                            additionalNotes = domainVisit.additionalNotes ?: "",
                                            location = domainVisit.address ?: "",
                                            attachments = emptyList(),
                                            createdAt = parseDateTime(domainVisit.createdAt),
                                            updatedAt = parseDateTime(domainVisit.updatedAt)
                                        )
                                    }
                                    
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        visits = visits,
                                        filteredVisits = visits
                                    )
                                }
                                is Resource.Error -> {
                                    // Si falla la carga de clientes, mostrar visitas con IDs
                                    val visits = domainVisits.map { domainVisit ->
                                        Visit(
                                            id = domainVisit.id,
                                            customerName = "Cliente ${domainVisit.customerId}",
                                            visitDate = domainVisit.visitDate,
                                            visitTime = domainVisit.visitTime,
                                            contactedPersons = domainVisit.contactedPersons ?: "",
                                            clinicalFindings = domainVisit.clinicalFindings ?: "",
                                            additionalNotes = domainVisit.additionalNotes ?: "",
                                            location = domainVisit.address ?: "",
                                            attachments = emptyList(),
                                            createdAt = parseDateTime(domainVisit.createdAt),
                                            updatedAt = parseDateTime(domainVisit.updatedAt)
                                        )
                                    }
                                    
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        visits = visits,
                                        filteredVisits = visits
                                    )
                                }
                                is Resource.Loading -> {
                                    // Mantener estado de carga
                                }
                            }
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Error desconocido al cargar visitas"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }
    
    private fun parseDateTime(dateTimeString: String?): LocalDateTime {
        return try {
            if (dateTimeString.isNullOrBlank()) {
                LocalDateTime.now()
            } else {
                // Try ISO format first
                LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
            }
        } catch (e: Exception) {
            LocalDateTime.now()
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