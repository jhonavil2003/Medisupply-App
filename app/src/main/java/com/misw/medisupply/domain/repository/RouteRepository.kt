package com.misw.medisupply.domain.repository

import com.misw.medisupply.domain.model.route.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Interfaz del repositorio de rutas
 */
interface RouteRepository {
    
    /**
     * Generar ruta optimizada
     */
    suspend fun generateRoute(
        salespersonId: Int,
        salespersonName: String,
        employeeId: String,
        customerIds: List<Int>,
        plannedDate: LocalDate,
        optimizationStrategy: OptimizationStrategy = OptimizationStrategy.MINIMIZE_DISTANCE,
        startLocation: Location? = null,
        endLocation: Location? = null,
        workHours: WorkHours? = null,
        serviceTimePerVisitMinutes: Int = 30
    ): Result<RouteGenerationResult>
    
    /**
     * Obtener detalle de una ruta
     */
    suspend fun getRoute(routeId: Int): Result<Route>
    
    /**
     * Obtener rutas de un vendedor con filtros
     */
    suspend fun getSalespersonRoutes(
        salespersonId: Int,
        date: LocalDate? = null,
        status: RouteStatus? = null,
        page: Int = 1,
        perPage: Int = 10
    ): Result<List<Route>>
    
    /**
     * Confirmar ruta (DRAFT -> CONFIRMED)
     */
    suspend fun confirmRoute(routeId: Int): Result<Route>
    
    /**
     * Iniciar ruta (CONFIRMED -> IN_PROGRESS)
     */
    suspend fun startRoute(routeId: Int): Result<Route>
    
    /**
     * Completar una parada
     */
    suspend fun completeStop(
        routeId: Int,
        stopId: Int,
        actualArrival: LocalDateTime,
        actualDeparture: LocalDateTime,
        notes: String? = null
    ): Result<RouteStop>
    
    /**
     * Omitir una parada
     */
    suspend fun skipStop(
        routeId: Int,
        stopId: Int,
        skipReason: String
    ): Result<RouteStop>
    
    /**
     * Completar ruta (IN_PROGRESS -> COMPLETED)
     */
    suspend fun completeRoute(routeId: Int): Result<Route>
    
    /**
     * Cancelar ruta
     */
    suspend fun cancelRoute(routeId: Int): Result<Unit>
}
