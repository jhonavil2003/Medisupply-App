package com.misw.medisupply.presentation.salesforce.screens.routes.state

import com.misw.medisupply.domain.model.route.Location
import com.misw.medisupply.domain.model.route.Route
import com.misw.medisupply.domain.model.route.RouteStop
import java.time.LocalDateTime

/**
 * Estado de UI para la pantalla de ejecución de ruta
 */
data class RouteExecutionUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // Ruta
    val route: Route? = null,
    
    // Ubicación actual
    val currentLocation: Location? = null,
    val isTrackingLocation: Boolean = false,
    val isLocationAvailable: Boolean = false,
    val locationError: String? = null,
    
    // Parada actual
    val currentStopId: Int? = null,
    val selectedStopId: Int? = null,
    val distanceToNextStopKm: Float? = null,
    val distanceToNextStop: Double? = null,
    val estimatedTimeToNextStopMinutes: Int? = null,
    
    // Acciones sobre paradas
    val isCompletingStop: Boolean = false,
    val isSkippingStop: Boolean = false,
    val isCompletingRoute: Boolean = false,
    val arrivedStopIds: Set<Int> = emptySet(),
    
    // Dialogs
    val showCompleteStopDialog: Boolean = false,
    val showSkipStopDialog: Boolean = false,
    val showCompleteRouteDialog: Boolean = false,
    
    // Formularios de diálogos
    val completeStopNotes: String = "",
    val stopNotes: String = "",
    val skipStopReason: String = "",
    val skipReason: String = "",
    val arrivalTime: LocalDateTime? = null,
    
    // Navegación
    val centerMapOnLocation: Boolean = true,
    val navigationStopId: Int? = null,
    
    // Notificaciones
    val showProximityAlert: Boolean = false,
    
    // Éxito
    val successMessage: String? = null
) {
    /**
     * Parada actual
     */
    val currentStop: RouteStop?
        get() = route?.stops?.firstOrNull { it.id == currentStopId }
    
    /**
     * Siguiente parada pendiente
     */
    val nextStop: RouteStop?
        get() = route?.nextStop
    
    /**
     * Siguiente parada pendiente (alias para compatibilidad)
     */
    val nextPendingStop: RouteStop?
        get() = route?.stops?.firstOrNull { it.completedAt == null && it.skippedAt == null }
    
    /**
     * Porcentaje de completación (0-100)
     */
    val completionPercentage: Int
        get() {
            val route = this.route ?: return 0
            val total = route.stops.size
            if (total == 0) return 0
            val completed = route.stops.count { it.completedAt != null }
            return (completed * 100) / total
        }
    
    /**
     * Indica si hay una parada activa
     */
    val hasActiveStop: Boolean
        get() = currentStop != null
    
    /**
     * Indica si se puede completar la ruta
     */
    val canCompleteRoute: Boolean
        get() = route?.canBeCompleted == true
    
    /**
     * Progreso de la ruta (0.0 a 1.0)
     */
    val routeProgress: Float
        get() {
            val route = this.route ?: return 0f
            return if (route.metrics.totalStops > 0) {
                route.metrics.completedStops.toFloat() / route.metrics.totalStops
            } else 0f
        }
    
    /**
     * Indica si está cerca de la siguiente parada (< 500m)
     */
    val isNearNextStop: Boolean
        get() = (distanceToNextStopKm ?: Float.MAX_VALUE) < 0.5f
}
