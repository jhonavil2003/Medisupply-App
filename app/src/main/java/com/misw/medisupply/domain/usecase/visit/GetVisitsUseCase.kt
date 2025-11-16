package com.misw.medisupply.domain.usecase.visit

import com.misw.medisupply.domain.model.visit.Visit
import com.misw.medisupply.domain.repository.VisitRepository
import javax.inject.Inject

/**
 * Use case para obtener visitas con filtros opcionales
 */
class GetVisitsUseCase @Inject constructor(
    private val repository: VisitRepository
) {
    suspend operator fun invoke(
        customerId: Int? = null,
        salespersonId: Int? = null,
        status: String? = null
    ): Result<List<Visit>> {
        return repository.getVisits(
            customerId = customerId,
            salespersonId = salespersonId,
            status = status
        )
    }
}
