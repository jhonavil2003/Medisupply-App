package com.misw.medisupply.domain.model.route

/**
 * Estados posibles de una ruta de visitas
 */
enum class RouteStatus(val displayName: String, val apiValue: String) {
    DRAFT("Borrador", "draft"),
    CONFIRMED("Confirmada", "confirmed"),
    IN_PROGRESS("En progreso", "in_progress"),
    COMPLETED("Completada", "completed"),
    CANCELLED("Cancelada", "cancelled");
    
    companion object {
        fun fromApiValue(value: String): RouteStatus {
            return values().firstOrNull { it.apiValue == value.lowercase() }
                ?: DRAFT
        }
    }
}
