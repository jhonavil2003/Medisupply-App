package com.misw.medisupply.domain.model.route

/**
 * Métricas de una ruta de visitas
 */
data class RouteMetrics(
    val totalStops: Int,
    val totalDistanceKm: Double,
    val estimatedDurationMinutes: Int,
    val optimizationScore: Double,
    val completedStops: Int = 0,
    val skippedStops: Int = 0
) {
    /**
     * Paradas pendientes
     */
    val pendingStops: Int
        get() = totalStops - completedStops - skippedStops
    
    /**
     * Porcentaje de progreso
     */
    val progressPercentage: Int
        get() = if (totalStops > 0) {
            ((completedStops * 100) / totalStops)
        } else 0
    
    /**
     * Distancia formateada
     */
    val formattedDistance: String
        get() = String.format("%.1f km", totalDistanceKm)
    
    /**
     * Duración formateada
     */
    val formattedDuration: String
        get() {
            val hours = estimatedDurationMinutes / 60
            val minutes = estimatedDurationMinutes % 60
            return if (hours > 0) {
                "${hours}h ${minutes}min"
            } else {
                "${minutes}min"
            }
        }
}
