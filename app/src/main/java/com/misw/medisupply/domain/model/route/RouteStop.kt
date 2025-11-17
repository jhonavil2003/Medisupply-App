package com.misw.medisupply.domain.model.route

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Parada individual en una ruta de visitas
 */
data class RouteStop(
    val id: Int,
    val sequenceOrder: Int,
    val customerId: Int,
    val customerName: String,
    val customerDocument: String,
    val customerType: String,
    val address: String,
    val neighborhood: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val contactName: String,
    val contactPhone: String,
    val contactEmail: String,
    val estimatedArrival: LocalDateTime,
    val estimatedDeparture: LocalDateTime,
    val serviceMinutes: Int,
    val distanceFromPreviousKm: Double,
    val travelTimeMinutes: Int,
    val actualArrival: LocalDateTime? = null,
    val actualDeparture: LocalDateTime? = null,
    val actualServiceMinutes: Int? = null,
    val isCompleted: Boolean = false,
    val isSkipped: Boolean = false,
    val completedAt: LocalDateTime? = null,
    val skippedAt: LocalDateTime? = null,
    val notes: String? = null,
    val skipReason: String? = null
) {
    /**
     * Estado visual de la parada
     */
    val status: StopStatus
        get() = when {
            isCompleted -> StopStatus.COMPLETED
            isSkipped -> StopStatus.SKIPPED
            else -> StopStatus.PENDING
        }
    
    /**
     * Tiempo de servicio real o estimado
     */
    val serviceTime: Int
        get() = actualServiceMinutes ?: serviceMinutes
    
    /**
     * Formato de hora estimada de llegada
     */
    fun formattedEstimatedArrival(pattern: String = "HH:mm"): String {
        return estimatedArrival.format(DateTimeFormatter.ofPattern(pattern))
    }
    
    /**
     * Formato de hora estimada de salida
     */
    fun formattedEstimatedDeparture(pattern: String = "HH:mm"): String {
        return estimatedDeparture.format(DateTimeFormatter.ofPattern(pattern))
    }
    
    /**
     * Distancia formateada
     */
    val formattedDistance: String
        get() = String.format("%.1f km", distanceFromPreviousKm)
}

/**
 * Estados visuales de una parada
 */
enum class StopStatus {
    PENDING,
    COMPLETED,
    SKIPPED
}
