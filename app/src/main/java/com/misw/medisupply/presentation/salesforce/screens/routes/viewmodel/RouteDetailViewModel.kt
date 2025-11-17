package com.misw.medisupply.presentation.salesforce.screens.routes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.domain.usecase.route.*
import com.misw.medisupply.presentation.salesforce.screens.routes.state.RouteDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteDetailViewModel @Inject constructor(
    private val getRouteUseCase: GetRouteUseCase,
    private val confirmRouteUseCase: ConfirmRouteUseCase,
    private val startRouteUseCase: StartRouteUseCase,
    private val completeRouteUseCase: CompleteRouteUseCase,
    private val cancelRouteUseCase: CancelRouteUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RouteDetailUiState())
    val uiState: StateFlow<RouteDetailUiState> = _uiState.asStateFlow()
    
    /**
     * Cargar detalle de ruta
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
     * Refrescar ruta
     */
    fun refresh() {
        val routeId = _uiState.value.route?.id ?: return
        loadRoute(routeId)
    }
    
    /**
     * Confirmar ruta
     */
    fun confirmRoute(onSuccess: () -> Unit = {}) {
        val routeId = _uiState.value.route?.id ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isConfirming = true, error = null) }
            
            val result = confirmRouteUseCase(routeId)
            
            result.fold(
                onSuccess = { route ->
                    _uiState.update { 
                        it.copy(
                            isConfirming = false,
                            route = route,
                            successMessage = "Ruta confirmada exitosamente",
                            showConfirmDialog = false
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isConfirming = false,
                            error = error.message ?: "Error al confirmar ruta",
                            showConfirmDialog = false
                        )
                    }
                }
            )
        }
    }
    
    /**
     * Iniciar ruta
     */
    fun startRoute(onSuccess: (Int) -> Unit = {}) {
        val route = _uiState.value.route ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isStarting = true, error = null) }
            
            val result = startRouteUseCase(route.id)
            
            result.fold(
                onSuccess = { updatedRoute ->
                    _uiState.update { 
                        it.copy(
                            isStarting = false,
                            route = updatedRoute,
                            successMessage = "Ruta iniciada exitosamente",
                            showStartDialog = false
                        )
                    }
                    onSuccess(updatedRoute.id)
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isStarting = false,
                            error = error.message ?: "Error al iniciar ruta",
                            showStartDialog = false
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
            _uiState.update { it.copy(isCompleting = true, error = null) }
            
            val result = completeRouteUseCase(routeId)
            
            result.fold(
                onSuccess = { route ->
                    _uiState.update { 
                        it.copy(
                            isCompleting = false,
                            route = route,
                            successMessage = "Ruta completada exitosamente",
                            showCompleteDialog = false
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isCompleting = false,
                            error = error.message ?: "Error al completar ruta",
                            showCompleteDialog = false
                        )
                    }
                }
            )
        }
    }
    
    /**
     * Cancelar ruta
     */
    fun cancelRoute(onSuccess: () -> Unit = {}) {
        val routeId = _uiState.value.route?.id ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isCancelling = true, error = null) }
            
            val result = cancelRouteUseCase(routeId)
            
            result.fold(
                onSuccess = {
                    _uiState.update { 
                        it.copy(
                            isCancelling = false,
                            successMessage = "Ruta cancelada exitosamente",
                            showCancelDialog = false
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isCancelling = false,
                            error = error.message ?: "Error al cancelar ruta",
                            showCancelDialog = false
                        )
                    }
                }
            )
        }
    }
    
    /**
     * Toggle expansión de parada
     */
    fun toggleStopExpansion(stopId: Int) {
        _uiState.update { state ->
            val expandedIds = if (stopId in state.expandedStopIds) {
                state.expandedStopIds - stopId
            } else {
                state.expandedStopIds + stopId
            }
            state.copy(expandedStopIds = expandedIds)
        }
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
    fun showConfirmDialog(show: Boolean) {
        _uiState.update { it.copy(showConfirmDialog = show) }
    }
    
    fun showCancelDialog(show: Boolean) {
        _uiState.update { it.copy(showCancelDialog = show) }
    }
    
    fun showStartDialog(show: Boolean) {
        _uiState.update { it.copy(showStartDialog = show) }
    }
    
    fun showCompleteDialog(show: Boolean) {
        _uiState.update { it.copy(showCompleteDialog = show) }
    }
    
    /**
     * Toggle mapa fullscreen
     */
    fun toggleMapFullscreen() {
        _uiState.update { it.copy(showMapFullscreen = !it.showMapFullscreen) }
    }
    
    /**
     * Limpiar mensajes
     */
    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
