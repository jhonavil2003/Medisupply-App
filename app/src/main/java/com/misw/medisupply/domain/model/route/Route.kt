package com.misw.medisupply.domain.model.route

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Modelo de dominio para una ruta de visitas optimizada
 */
data class Route(
    val id: Int,
    val routeCode: String,
    val salespersonId: Int,
    val salespersonName: String,
    val salespersonEmployeeId: String,
    val plannedDate: LocalDate,
    val status: RouteStatus,
    val metrics: RouteMetrics,
    val stops: List<RouteStop> = emptyList(),
    val startLocation: Location? = null,
    val endLocation: Location? = null,
    val workHours: WorkHours = WorkHours(),
    val mapUrl: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val confirmedAt: LocalDateTime? = null,
    val startedAt: LocalDateTime? = null,
    val completedAt: LocalDateTime? = null
) {
    /**
     * Siguiente parada pendiente
     */
    val nextStop: RouteStop?
        get() = stops.firstOrNull { !it.isCompleted && !it.isSkipped }
    
    /**
     * Indica si la ruta puede ser confirmada
     */
    val canBeConfirmed: Boolean
        get() = status == RouteStatus.DRAFT
    
    /**
     * Indica si la ruta puede ser iniciada
     */
    val canBeStarted: Boolean
        get() = status == RouteStatus.CONFIRMED
    
    /**
     * Indica si la ruta puede ser completada
     */
    val canBeCompleted: Boolean
        get() = status == RouteStatus.IN_PROGRESS && 
                stops.all { it.isCompleted || it.isSkipped }
    
    /**
     * Indica si la ruta puede ser cancelada
     */
    val canBeCancelled: Boolean
        get() = status == RouteStatus.DRAFT || status == RouteStatus.CONFIRMED
    
    /**
     * Indica si está en ejecución
     */
    val isInProgress: Boolean
        get() = status == RouteStatus.IN_PROGRESS
    
    /**
     * Indica si está completada
     */
    val isCompleted: Boolean
        get() = status == RouteStatus.COMPLETED
    
    /**
     * Paradas completadas
     */
    val completedStops: List<RouteStop>
        get() = stops.filter { it.isCompleted }
    
    /**
     * Paradas pendientes
     */
    val pendingStops: List<RouteStop>
        get() = stops.filter { !it.isCompleted && !it.isSkipped }
    
    /**
     * Paradas omitidas
     */
    val skippedStops: List<RouteStop>
        get() = stops.filter { it.isSkipped }
}
