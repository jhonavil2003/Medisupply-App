package com.misw.medisupply.domain.usecase.route

import com.misw.medisupply.domain.model.route.RouteStop
import com.misw.medisupply.domain.repository.RouteRepository
import javax.inject.Inject

/**
 * Caso de uso para omitir una parada
 */
class SkipStopUseCase @Inject constructor(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(
        routeId: Int,
        stopId: Int,
        skipReason: String
    ): Result<RouteStop> {
        // Validación: razón no debe estar vacía
        if (skipReason.isBlank()) {
            return Result.failure(Exception("Debe proporcionar una razón para omitir la parada"))
        }
        
        if (skipReason.length > 200) {
            return Result.failure(Exception("La razón no debe exceder 200 caracteres"))
        }
        
        return repository.skipStop(
            routeId = routeId,
            stopId = stopId,
            skipReason = skipReason
        )
    }
}
