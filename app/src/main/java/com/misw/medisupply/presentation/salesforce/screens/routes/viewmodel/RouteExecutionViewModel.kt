package com.misw.medisupply.presentation.salesforce.screens.routes.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.i18n.LocaleManager
import com.misw.medisupply.domain.usecase.route.*
import com.misw.medisupply.presentation.salesforce.screens.routes.state.RouteExecutionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@HiltViewModel
class RouteExecutionViewModel @Inject constructor(
    private val getRouteUseCase: GetRouteUseCase,
    private val completeStopUseCase: CompleteStopUseCase,
    private val skipStopUseCase: SkipStopUseCase,
    private val completeRouteUseCase: CompleteRouteUseCase,
    val localeManager: LocaleManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RouteExecutionUiState())
    val uiState: StateFlow<RouteExecutionUiState> = _uiState.asStateFlow()
    
    /**
     * Cargar ruta para ejecución
     */
    fun loadRoute(routeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = getRouteUseCase(routeId)
            
            result.fold(
                onSuccess = { route ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            route = route,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error al cargar ruta"
                        )
                    }
                }
            )
        }
    }
    
    /**
     * Actualizar ubicación GPS
     */
    fun updateLocation(location: Location) {
        _uiState.update { state ->
            val currentLocation = com.misw.medisupply.domain.model.route.Location(
                name = "Mi ubicación",
                latitude = location.latitude,
                longitude = location.longitude
            )
            
            // Calcular distancia a siguiente parada
            val nextStop = state.nextPendingStop
            val distanceToNext = if (nextStop != null) {
                calculateDistance(
                    location.latitude,
                    location.longitude,
                    nextStop.latitude,
                    nextStop.longitude
                )
            } else null
            
            state.copy(
                currentLocation = currentLocation,
                distanceToNextStop = distanceToNext,
                isLocationAvailable = true
            )
        }
    }
    
    /**
     * Marcar que se llegó a una parada
     */
    fun arrivedAtStop(stopId: Int) {
        _uiState.update { 
            it.copy(
                arrivedStopIds = it.arrivedStopIds + stopId,
                showCompleteStopDialog = true,
                selectedStopId = stopId
            )
        }
    }
    
    /**
     * Completar parada
     */
    fun completeStop(stopId: Int, notes: String? = null, onSuccess: () -> Unit = {}) {
        val routeId = _uiState.value.route?.id ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isCompletingStop = true, error = null) }
            
            val now = java.time.LocalDateTime.now()
            val result = completeStopUseCase(
                routeId = routeId,
                stopId = stopId,
                actualArrival = now.minusMinutes(15), // Asumimos llegada 15 min antes
                actualDeparture = now,
                notes = notes
            )
            
            result.fold(
                onSuccess = { updatedStop ->
                    // Actualizar la parada en la ruta
                    _uiState.update { state ->
                        val updatedRoute = state.route?.copy(
                            stops = state.route.stops.map { stop ->
                                if (stop.id == stopId) updatedStop else stop
                            }
                        )
                        
                        state.copy(
                            isCompletingStop = false,
                            route = updatedRoute,
                            showCompleteStopDialog = false,
                            selectedStopId = null,
                            stopNotes = "",
                            successMessage = "Parada completada exitosamente"
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isCompletingStop = false,
                            error = error.message ?: "Error al completar parada"
                        )
                    }
                }
            )
        }
    }
    
    /**
     * Omitir parada
     */
    fun skipStop(stopId: Int, reason: String, onSuccess: () -> Unit = {}) {
        if (reason.isBlank()) {
            _uiState.update { it.copy(error = "Debe proporcionar una razón para omitir la parada") }
            return
        }
        
        val routeId = _uiState.value.route?.id ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSkippingStop = true, error = null) }
            
            val result = skipStopUseCase(
                routeId = routeId,
                stopId = stopId,
                skipReason = reason
            )
            
            result.fold(
                onSuccess = { updatedStop ->
                    // Actualizar la parada en la ruta
                    _uiState.update { state ->
                        val updatedRoute = state.route?.copy(
                            stops = state.route.stops.map { stop ->
                                if (stop.id == stopId) updatedStop else stop
                            }
                        )
                        
                        state.copy(
                            isSkippingStop = false,
                            route = updatedRoute,
                            showSkipStopDialog = false,
                            selectedStopId = null,
                            skipReason = "",
                            successMessage = "Parada omitida"
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isSkippingStop = false,
                            error = error.message ?: "Error al omitir parada"
                        )
                    }
                }
            )
        }
    }
    
    /**
     * Completar ruta
     */
    fun completeRoute(onSuccess: () -> Unit = {}) {
        val routeId = _uiState.value.route?.id ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isCompletingRoute = true, error = null) }
            
            val result = completeRouteUseCase(routeId)
            
            result.fold(
                onSuccess = { route ->
                    _uiState.update { 
                        it.copy(
                            isCompletingRoute = false,
                            route = route,
                            showCompleteRouteDialog = false,
                            successMessage = "Ruta completada exitosamente"
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isCompletingRoute = false,
                            error = error.message ?: "Error al completar ruta"
                        )
                    }
                }
            )
        }
    }
    
    /**
     * Actualizar razón para omitir
     */
    fun updateSkipReason(reason: String) {
        _uiState.update { it.copy(skipReason = reason) }
    }
    
    /**
     * Actualizar notas de parada
     */
    fun updateStopNotes(notes: String) {
        _uiState.update { it.copy(stopNotes = notes) }
    }
    
    /**
     * Seleccionar parada
     */
    fun selectStop(stopId: Int?) {
        _uiState.update { it.copy(selectedStopId = stopId) }
    }
    
    /**
     * Mostrar/ocultar dialogs
     */
    fun showCompleteStopDialog(show: Boolean, stopId: Int? = null) {
        _uiState.update { 
            it.copy(
                showCompleteStopDialog = show,
                selectedStopId = if (show) stopId else null,
                stopNotes = if (!show) "" else it.stopNotes
            )
        }
    }
    
    fun showSkipStopDialog(show: Boolean, stopId: Int? = null) {
        _uiState.update { 
            it.copy(
                showSkipStopDialog = show,
                selectedStopId = if (show) stopId else null,
                skipReason = if (!show) "" else it.skipReason
            )
        }
    }
    
    fun showCompleteRouteDialog(show: Boolean) {
        _uiState.update { it.copy(showCompleteRouteDialog = show) }
    }
    
    /**
     * Toggle centro de mapa en ubicación actual
     */
    fun toggleCenterOnLocation() {
        _uiState.update { it.copy(centerMapOnLocation = !it.centerMapOnLocation) }
    }
    
    /**
     * Navegar a parada (abrir GPS externa)
     */
    fun navigateToStop(stopId: Int) {
        _uiState.update { it.copy(navigationStopId = stopId) }
    }
    
    /**
     * Limpiar navegación
     */
    fun clearNavigation() {
        _uiState.update { it.copy(navigationStopId = null) }
    }
    
    /**
     * Toggle tracking GPS
     */
    fun toggleGpsTracking(enabled: Boolean) {
        _uiState.update { it.copy(isTrackingLocation = enabled) }
    }
    
    /**
     * Limpiar mensajes
     */
    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
    
    /**
     * Refrescar ruta
     */
    fun refresh() {
        val routeId = _uiState.value.route?.id ?: return
        loadRoute(routeId)
    }
    
    /**
     * Calcular distancia entre dos puntos GPS usando fórmula de Haversine
     * @return Distancia en metros
     */
    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadius = 6371000.0 // Radio de la Tierra en metros
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return earthRadius * c
    }
}
