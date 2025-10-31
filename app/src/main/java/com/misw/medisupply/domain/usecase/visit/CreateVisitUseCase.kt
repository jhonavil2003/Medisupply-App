package com.misw.medisupply.domain.usecase.visit

import com.misw.medisupply.domain.model.visit.Visit
import com.misw.medisupply.domain.repository.VisitRepository
import javax.inject.Inject

class CreateVisitUseCase @Inject constructor(
    private val visitRepository: VisitRepository
) {
    suspend operator fun invoke(visit: Visit): Result<Visit> {
        return visitRepository.createVisit(visit)
    }
}