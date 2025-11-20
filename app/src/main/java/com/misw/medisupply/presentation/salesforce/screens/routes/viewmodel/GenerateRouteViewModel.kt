package com.misw.medisupply.presentation.salesforce.screens.routes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.i18n.LocaleManager
import com.misw.medisupply.core.session.UserSessionManager
import com.misw.medisupply.domain.model.route.Location
import com.misw.medisupply.domain.model.route.OptimizationStrategy
import com.misw.medisupply.domain.model.route.WorkHours
import com.misw.medisupply.domain.usecase.customer.GetCustomersUseCase
import com.misw.medisupply.domain.usecase.route.GenerateRouteUseCase
import com.misw.medisupply.presentation.salesforce.screens.routes.state.GenerateRouteUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class GenerateRouteViewModel @Inject constructor(
    private val generateRouteUseCase: GenerateRouteUseCase,
    private val getCustomersUseCase: GetCustomersUseCase,
    private val userSessionManager: UserSessionManager,
    val localeManager: LocaleManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GenerateRouteUiState())
    val uiState: StateFlow<GenerateRouteUiState> = _uiState.asStateFlow()
    
    init {
        loadCustomers()
    }
    
    /**
     * Cargar clientes asignados al vendedor
     */
    fun loadCustomers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCustomers = true, customerError = null) }
            
            try {
                // TODO: Obtener sellerId del UserSessionManager cuando login esté implementado
                // Por ahora usar sellerId = 1 (hardcodeado temporalmente)
                val sellerId = 1
                
                getCustomersUseCase(
                    isActive = true,
                    sellerId = sellerId
                ).collect { resource ->
                    when (resource) {
                        is com.misw.medisupply.core.base.Resource.Success -> {
                            // Filtrar solo clientes con coordenadas GPS
                            val customersWithGPS = resource.data?.filter { customer ->
                                customer.latitude != null && customer.longitude != null
                            } ?: emptyList()
                            
                            _uiState.update { 
                                it.copy(
                                    customers = customersWithGPS,
                                    isLoadingCustomers = false,
                                    customerError = if (customersWithGPS.isEmpty()) {
                                        "No hay clientes asignados con ubicación GPS configurada"
                                    } else null
                                )
                            }
                        }
                        is com.misw.medisupply.core.base.Resource.Error -> {
                            _uiState.update { 
                                it.copy(
                                    isLoadingCustomers = false,
                                    customerError = resource.message ?: "Error al cargar clientes"
                                )
                            }
                        }
                        is com.misw.medisupply.core.base.Resource.Loading -> {
                            // Ya está en estado de carga
                        }
                    }
                }
            } catch (e: IllegalStateException) {
                _uiState.update { 
                    it.copy(
                        isLoadingCustomers = false,
                        customerError = "Usuario no autenticado"
                    )
                }
            }
        }
    }
    
    /**
     * Actualizar búsqueda de clientes
     */
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
    
    /**
     * Seleccionar/deseleccionar cliente
     */
    fun toggleCustomerSelection(customerId: Int) {
        _uiState.update { state ->
            val newSelection = if (customerId in state.selectedCustomerIds) {
                state.selectedCustomerIds - customerId
            } else {
                if (state.selectedCustomerIds.size >= 20) {
                    // Máximo 20 clientes
                    return@update state.copy(
                        validationError = "Máximo 20 clientes por ruta"
                    )
                }
                state.selectedCustomerIds + customerId
            }
            
            state.copy(
                selectedCustomerIds = newSelection,
                validationError = null,
                isFormValid = newSelection.isNotEmpty()
            )
        }
    }
    
    /**
     * Seleccionar todos los clientes filtrados
     */
    fun selectAllFiltered() {
        _uiState.update { state ->
            val filteredIds = state.filteredCustomers.map { it.id }
            val newSelection = (state.selectedCustomerIds + filteredIds).take(20).toSet()
            
            state.copy(
                selectedCustomerIds = newSelection,
                isFormValid = newSelection.isNotEmpty(),
                validationError = if (filteredIds.size > 20) {
                    "Se seleccionaron solo los primeros 20 clientes"
                } else null
            )
        }
    }
    
    /**
     * Deseleccionar todos
     */
    fun clearSelection() {
        _uiState.update { 
            it.copy(
                selectedCustomerIds = emptySet(),
                isFormValid = false,
                validationError = null
            )
        }
    }
    
    /**
     * Actualizar fecha de la ruta
     */
    fun updateSelectedDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }
    
    /**
     * Actualizar estrategia de optimización
     */
    fun updateOptimizationStrategy(strategy: OptimizationStrategy) {
        _uiState.update { it.copy(optimizationStrategy = strategy) }
    }
    
    /**
     * Actualizar horario de inicio
     */
    fun updateWorkHoursStart(time: LocalTime) {
        _uiState.update { it.copy(workHoursStart = time) }
    }
    
    /**
     * Actualizar horario de fin
     */
    fun updateWorkHoursEnd(time: LocalTime) {
        _uiState.update { it.copy(workHoursEnd = time) }
    }
    
    /**
     * Actualizar tiempo de servicio por visita
     */
    fun updateServiceTime(minutes: Int) {
        _uiState.update { it.copy(serviceTimeMinutes = minutes.coerceIn(15, 120)) }
    }
    
    /**
     * Activar/desactivar ubicación de inicio personalizada
     */
    fun toggleCustomStartLocation(enabled: Boolean) {
        _uiState.update { it.copy(useCustomStartLocation = enabled) }
    }
    
    /**
     * Actualizar datos de ubicación de inicio
     */
    fun updateStartLocation(name: String, latitude: String, longitude: String) {
        _uiState.update { 
            it.copy(
                startLocationName = name,
                startLocationLatitude = latitude,
                startLocationLongitude = longitude
            )
        }
    }
    
    /**
     * Generar ruta optimizada
     */
    fun generateRoute(onSuccess: (Int) -> Unit) {
        val state = _uiState.value
        
        if (!state.canGenerate) {
            _uiState.update { it.copy(error = "Complete todos los campos requeridos") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, error = null) }
            
            try {
                // TODO: Obtener sellerId y salesperson del UserSessionManager cuando login esté implementado
                // Por ahora usar valores temporales (sellerId = 1)
                val sellerId = 1
                val salespersonName = "Vendedor Demo" // Temporal
                
                // Preparar ubicación de inicio si está configurada
                val startLocation = if (state.useCustomStartLocation) {
                    try {
                        Location(
                            name = state.startLocationName,
                            latitude = state.startLocationLatitude.toDouble(),
                            longitude = state.startLocationLongitude.toDouble()
                        )
                    } catch (e: NumberFormatException) {
                        _uiState.update { 
                            it.copy(
                                isGenerating = false,
                                error = "Coordenadas de ubicación inválidas"
                            )
                        }
                        return@launch
                    }
                } else null
                
                val result = generateRouteUseCase(
                    salespersonId = sellerId,
                    salespersonName = salespersonName,
                    employeeId = sellerId.toString(), // Usar ID como employeeId
                    customerIds = state.selectedCustomerIds.toList(),
                    plannedDate = state.selectedDate,
                    optimizationStrategy = state.optimizationStrategy,
                    startLocation = startLocation,
                    workHours = WorkHours(
                        start = state.workHoursStart,
                        end = state.workHoursEnd
                    ),
                    serviceTimePerVisitMinutes = state.serviceTimeMinutes
                )
                
                result.fold(
                    onSuccess = { (route, computationTime) ->
                        _uiState.update { 
                            it.copy(
                                isGenerating = false,
                                generatedRoute = route,
                                computationTime = computationTime
                            )
                        }
                        onSuccess(route.id)
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                isGenerating = false,
                                error = error.message ?: "Error al generar ruta"
                            )
                        }
                    }
                )
            } catch (e: IllegalStateException) {
                _uiState.update { 
                    it.copy(
                        isGenerating = false,
                        error = "Usuario no autenticado"
                    )
                }
            }
        }
    }
    
    /**
     * Limpiar errores
     */
    fun clearError() {
        _uiState.update { it.copy(error = null, validationError = null) }
    }
    
    /**
     * Limpiar validación
     */
    fun clearValidationError() {
        _uiState.update { it.copy(validationError = null) }
    }
}
