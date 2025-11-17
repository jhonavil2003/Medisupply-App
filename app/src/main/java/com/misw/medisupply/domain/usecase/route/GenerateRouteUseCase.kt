package com.misw.medisupply.domain.usecase.route

import com.misw.medisupply.domain.model.route.Location
import com.misw.medisupply.domain.model.route.OptimizationStrategy
import com.misw.medisupply.domain.model.route.Route
import com.misw.medisupply.domain.model.route.WorkHours
import com.misw.medisupply.domain.repository.RouteRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Caso de uso para generar una ruta optimizada
 */
class GenerateRouteUseCase @Inject constructor(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(
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
    ): Result<Pair<Route, Double?>> {
        // Validaciones
        if (customerIds.isEmpty()) {
            return Result.failure(Exception("Debe seleccionar al menos un cliente"))
        }
        
        if (customerIds.size > 20) {
            return Result.failure(Exception("Máximo 20 clientes por ruta"))
        }
        
        if (plannedDate.isBefore(LocalDate.now())) {
            return Result.failure(Exception("La fecha de planificación debe ser futura"))
        }
        
        return repository.generateRoute(
            salespersonId = salespersonId,
            salespersonName = salespersonName,
            employeeId = employeeId,
            customerIds = customerIds,
            plannedDate = plannedDate,
            optimizationStrategy = optimizationStrategy,
            startLocation = startLocation,
            endLocation = endLocation,
            workHours = workHours,
            serviceTimePerVisitMinutes = serviceTimePerVisitMinutes
        )
    }
}
