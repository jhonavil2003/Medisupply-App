package com.misw.medisupply.domain.model.route

/**
 * Estrategias de optimización para generación de rutas
 */
enum class OptimizationStrategy(val apiValue: String, val displayName: String) {
    MINIMIZE_DISTANCE("minimize_distance", "Minimizar distancia"),
    MINIMIZE_TIME("minimize_time", "Minimizar tiempo"),
    BALANCED("balanced", "Equilibrado");
    
    companion object {
        fun fromApiValue(value: String): OptimizationStrategy {
            return values().firstOrNull { it.apiValue == value.lowercase() }
                ?: MINIMIZE_DISTANCE
        }
    }
}
