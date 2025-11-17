package com.misw.medisupply.domain.usecase.route

import com.misw.medisupply.domain.model.route.RouteStop
import com.misw.medisupply.domain.repository.RouteRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Caso de uso para completar una parada
 */
class CompleteStopUseCase @Inject constructor(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(
        routeId: Int,
        stopId: Int,
        actualArrival: LocalDateTime,
        actualDeparture: LocalDateTime,
        notes: String? = null
    ): Result<RouteStop> {
        // Validación: departure debe ser después de arrival
        if (actualDeparture.isBefore(actualArrival)) {
            return Result.failure(Exception("La hora de salida debe ser posterior a la de llegada"))
        }
        
        return repository.completeStop(
            routeId = routeId,
            stopId = stopId,
            actualArrival = actualArrival,
            actualDeparture = actualDeparture,
            notes = notes
        )
    }
}
