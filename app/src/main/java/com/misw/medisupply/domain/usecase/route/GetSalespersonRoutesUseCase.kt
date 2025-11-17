package com.misw.medisupply.domain.usecase.route

import com.misw.medisupply.domain.model.route.Route
import com.misw.medisupply.domain.model.route.RouteStatus
import com.misw.medisupply.domain.repository.RouteRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Caso de uso para obtener las rutas de un vendedor
 */
class GetSalespersonRoutesUseCase @Inject constructor(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(
        salespersonId: Int,
        date: LocalDate? = null,
        status: RouteStatus? = null,
        page: Int = 1,
        perPage: Int = 10
    ): Result<List<Route>> {
        return repository.getSalespersonRoutes(
            salespersonId = salespersonId,
            date = date,
            status = status,
            page = page,
            perPage = perPage
        )
    }
}
