package com.misw.medisupply.domain.usecase.visit

import com.misw.medisupply.domain.model.visit.Visit
import com.misw.medisupply.domain.repository.VisitRepository
import javax.inject.Inject

class UpdateVisitUseCase @Inject constructor(
    private val visitRepository: VisitRepository
) {
    suspend operator fun invoke(visitId: Int, visit: Visit): Result<Visit> {
        return visitRepository.updateVisit(visitId, visit)
    }
}