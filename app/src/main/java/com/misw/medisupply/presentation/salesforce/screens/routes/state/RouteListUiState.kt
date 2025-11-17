package com.misw.medisupply.presentation.salesforce.screens.routes.state

import com.misw.medisupply.domain.model.route.Route
import com.misw.medisupply.domain.model.route.RouteStatus
import java.time.LocalDate

/**
 * Estado de UI para la pantalla de lista de rutas
 */
data class RouteListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    
    // Rutas
    val routes: List<Route> = emptyList(),
    val totalRoutes: Int = 0,
    
    // Filtros
    val selectedDate: LocalDate? = null,
    val selectedStatus: RouteStatus? = null,
    val searchQuery: String = "",
    
    // Paginación
    val currentPage: Int = 1,
    val itemsPerPage: Int = 10,
    val hasMorePages: Boolean = false,
    val isLoadingMore: Boolean = false
) {
    /**
     * Rutas filtradas localmente por búsqueda
     */
    val filteredRoutes: List<Route>
        get() = if (searchQuery.isBlank()) {
            routes
        } else {
            routes.filter { route ->
                route.routeCode.contains(searchQuery, ignoreCase = true) ||
                route.salespersonName.contains(searchQuery, ignoreCase = true)
            }
        }
    
    /**
     * Indica si hay filtros activos
     */
    val hasActiveFilters: Boolean
        get() = selectedDate != null || selectedStatus != null
    
    /**
     * Texto del filtro de estado
     */
    val statusFilterText: String
        get() = selectedStatus?.displayName ?: "Todos los estados"
    
    /**
     * Indica si se puede cargar más datos
     */
    val canLoadMore: Boolean
        get() = hasMorePages && !isLoadingMore && !isLoading
}
