package com.misw.medisupply.domain.model.route

import com.misw.medisupply.R

/**
 * Estrategias de optimización para generación de rutas
 */
enum class OptimizationStrategy(val apiValue: String, val displayName: String) {
    MINIMIZE_DISTANCE("minimize_distance", "Minimizar distancia"),
    MINIMIZE_TIME("minimize_time", "Minimizar tiempo"),
    BALANCED("balanced", "Equilibrado");
    
    /**
     * Obtiene el nombre localizado de la estrategia
     */
    fun getLocalizedDisplayName(localeManager: com.misw.medisupply.core.i18n.LocaleManager): String {
        return when (this) {
            MINIMIZE_DISTANCE -> localeManager.getLocalizedString(R.string.optimization_strategy_distance)
            MINIMIZE_TIME -> localeManager.getLocalizedString(R.string.optimization_strategy_time)
            BALANCED -> localeManager.getLocalizedString(R.string.optimization_strategy_balanced)
        }
    }
    
    /**
     * Obtiene la descripción localizada de la estrategia
     */
    fun getLocalizedDescription(localeManager: com.misw.medisupply.core.i18n.LocaleManager): String {
        return when (this) {
            MINIMIZE_DISTANCE -> localeManager.getLocalizedString(R.string.optimization_strategy_distance_desc)
            MINIMIZE_TIME -> localeManager.getLocalizedString(R.string.optimization_strategy_time_desc)
            BALANCED -> localeManager.getLocalizedString(R.string.optimization_strategy_balanced_desc)
        }
    }
    
    companion object {
        fun fromApiValue(value: String): OptimizationStrategy {
            return values().firstOrNull { it.apiValue == value.lowercase() }
                ?: MINIMIZE_DISTANCE
        }
    }
}
