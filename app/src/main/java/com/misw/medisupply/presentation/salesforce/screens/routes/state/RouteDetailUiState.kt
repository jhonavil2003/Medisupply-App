package com.misw.medisupply.presentation.salesforce.screens.routes.state

import com.misw.medisupply.domain.model.route.Route
import com.misw.medisupply.domain.model.route.RouteStop

/**
 * Estado de UI para la pantalla de detalle de ruta
 */
data class RouteDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // Ruta
    val route: Route? = null,
    
    // Acciones en progreso
    val isConfirming: Boolean = false,
    val isStarting: Boolean = false,
    val isCompleting: Boolean = false,
    val isCancelling: Boolean = false,
    
    // Paradas expandidas/colapsadas
    val expandedStopIds: Set<Int> = emptySet(),
    
    // Dialogs
    val showConfirmDialog: Boolean = false,
    val showCancelDialog: Boolean = false,
    val showStartDialog: Boolean = false,
    val showCompleteDialog: Boolean = false,
    
    // Mensajes de éxito
    val successMessage: String? = null,
    
    // Vista de mapa
    val showMapFullscreen: Boolean = false,
    val selectedStopId: Int? = null
) {
    /**
     * Parada seleccionada
     */
    val selectedStop: RouteStop?
        get() = route?.stops?.firstOrNull { it.id == selectedStopId }
    
    /**
     * Indica si alguna acción está en progreso
     */
    val isPerformingAction: Boolean
        get() = isConfirming || isStarting || isCompleting || isCancelling
    
    /**
     * Indica si se puede realizar alguna acción
     */
    val canPerformActions: Boolean
        get() = !isLoading && !isPerformingAction && route != null
    
    /**
     * Siguiente parada pendiente
     */
    val nextPendingStop: RouteStop?
        get() = route?.nextStop
}
