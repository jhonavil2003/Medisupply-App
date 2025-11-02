package com.misw.medisupply.domain.usecase.visit

import com.misw.medisupply.domain.model.visit.Visit
import com.misw.medisupply.domain.model.visit.VisitStatus
import com.misw.medisupply.domain.repository.VisitRepository
import javax.inject.Inject

class CompleteVisitUseCase @Inject constructor(
    private val visitRepository: VisitRepository
) {
    suspend operator fun invoke(visitId: Int): Result<Visit> {
        return visitRepository.completeVisit(visitId)
    }
}