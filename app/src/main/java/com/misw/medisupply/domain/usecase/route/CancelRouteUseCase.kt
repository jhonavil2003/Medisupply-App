package com.misw.medisupply.domain.usecase.route

import com.misw.medisupply.domain.repository.RouteRepository
import javax.inject.Inject

/**
 * Caso de uso para cancelar una ruta
 */
class CancelRouteUseCase @Inject constructor(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(routeId: Int): Result<Unit> {
        return repository.cancelRoute(routeId)
    }
}
