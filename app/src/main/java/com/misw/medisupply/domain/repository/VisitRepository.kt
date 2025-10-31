package com.misw.medisupply.domain.repository

import com.misw.medisupply.domain.model.visit.Visit

interface VisitRepository {
    suspend fun createVisit(visit: Visit): Result<Visit>
    suspend fun updateVisit(visitId: Int, visit: Visit): Result<Visit>
    suspend fun completeVisit(visitId: Int): Result<Visit>
}