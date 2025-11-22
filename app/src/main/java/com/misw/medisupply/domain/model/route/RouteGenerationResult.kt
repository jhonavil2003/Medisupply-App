package com.misw.medisupply.domain.model.route

/**
 * Resultado de generación de ruta que incluye warnings
 */
data class RouteGenerationResult(
    val route: Route,
    val computationTime: Double? = null,
    val warnings: List<String> = emptyList()
)