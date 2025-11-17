package com.misw.medisupply.domain.usecase.route

import com.misw.medisupply.domain.model.route.Route
import com.misw.medisupply.domain.repository.RouteRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener el detalle de una ruta
 */
class GetRouteUseCase @Inject constructor(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(routeId: Int): Result<Route> {
        return repository.getRoute(routeId)
    }
}
