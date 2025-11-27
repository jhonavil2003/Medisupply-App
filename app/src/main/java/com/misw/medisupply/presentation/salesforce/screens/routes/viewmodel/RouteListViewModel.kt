package com.misw.medisupply.presentation.salesforce.screens.routes.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.session.UserSessionManager
import com.misw.medisupply.domain.model.route.RouteStatus
import com.misw.medisupply.domain.usecase.route.GetSalespersonRoutesUseCase
import com.misw.medisupply.presentation.salesforce.screens.routes.state.RouteListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RouteListViewModel @Inject constructor(
    private val getSalespersonRoutesUseCase: GetSalespersonRoutesUseCase,
    private val userSessionManager: UserSessionManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RouteListUiState())
    val uiState: StateFlow<RouteListUiState> = _uiState.asStateFlow()
    
    init {
        loadRoutes()
    }
    
    /**
     * Cargar rutas del vendedor
     */
    fun loadRoutes(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }
            
            try {
                // TODO: Obtener sellerId del UserSessionManager cuando login esté implementado
                // Por ahora usar sellerId = 1 (hardcodeado temporalmente)
                val sellerId = 1
                
                val state = _uiState.value

                val currentSellerId = userSessionManager.requireSalespersonId()
                Log.d("HomeViewModel", "SalespersonId = $currentSellerId")
                
                val result = getSalespersonRoutesUseCase(
                    salespersonId = currentSellerId,
                    date = state.selectedDate,
                    status = state.selectedStatus,
                    page = 1,
                    perPage = state.itemsPerPage
                )
                
                result.fold(
                    onSuccess = { routes ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                routes = routes,
                                totalRoutes = routes.size,
                                currentPage = 1,
                                hasMorePages = routes.size >= state.itemsPerPage,
                                error = null
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = error.message ?: "Error al cargar rutas"
                            )
                        }
                    }
                )
            } catch (e: IllegalStateException) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = "Usuario no autenticado"
                    )
                }
            }
        }
    }
    
    /**
     * Refrescar lista
     */
    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadRoutes(showLoading = false)
    }
    
    /**
     * Cargar más rutas (paginación)
     */
    fun loadMore() {
        val state = _uiState.value
        
        if (!state.canLoadMore) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            
            try {
                // TODO: Obtener sellerId del UserSessionManager cuando login esté implementado
                // Por ahora usar sellerId = 1 (hardcodeado temporalmente)
                val currentSellerId = userSessionManager.requireSalespersonId()
                Log.d("HomeViewModel", "SalespersonId = $currentSellerId")
                val nextPage = state.currentPage + 1
                
                val result = getSalespersonRoutesUseCase(
                    salespersonId = currentSellerId,
                    date = state.selectedDate,
                    status = state.selectedStatus,
                    page = nextPage,
                    perPage = state.itemsPerPage
                )
                
                result.fold(
                    onSuccess = { newRoutes ->
                        _uiState.update { 
                            it.copy(
                                isLoadingMore = false,
                                routes = it.routes + newRoutes,
                                currentPage = nextPage,
                                hasMorePages = newRoutes.size >= state.itemsPerPage
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                isLoadingMore = false,
                                error = error.message ?: "Error al cargar más rutas"
                            )
                        }
                    }
                )
            } catch (e: IllegalStateException) {
                _uiState.update { 
                    it.copy(
                        isLoadingMore = false,
                        error = "Usuario no autenticado"
                    )
                }
            }
        }
    }
    
    /**
     * Actualizar filtro de fecha
     */
    fun updateDateFilter(date: LocalDate?) {
        _uiState.update { it.copy(selectedDate = date) }
        loadRoutes()
    }
    
    /**
     * Actualizar filtro de estado
     */
    fun updateStatusFilter(status: RouteStatus?) {
        _uiState.update { it.copy(selectedStatus = status) }
        loadRoutes()
    }
    
    /**
     * Actualizar búsqueda
     */
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
    
    /**
     * Limpiar filtros
     */
    fun clearFilters() {
        _uiState.update { 
            it.copy(
                selectedDate = null,
                selectedStatus = null,
                searchQuery = ""
            )
        }
        loadRoutes()
    }
    
    /**
     * Limpiar error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
