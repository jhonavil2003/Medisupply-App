package com.misw.medisupply.data.repository

import com.misw.medisupply.data.network.api.VisitApiService
import com.misw.medisupply.data.network.dto.visit.CreateVisitRequest
import com.misw.medisupply.data.network.dto.visit.UpdateVisitRequest
import com.misw.medisupply.domain.model.visit.Visit
import com.misw.medisupply.domain.model.visit.VisitStatus
import com.misw.medisupply.domain.repository.VisitRepository
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitRepositoryImpl @Inject constructor(
    private val visitApiService: VisitApiService
) : VisitRepository {

    override suspend fun createVisit(visit: Visit): Result<Visit> {
        return try {
            val request = CreateVisitRequest(
                customerId = visit.customerId,
                salespersonId = visit.salespersonId,
                visitDate = visit.visitDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                visitTime = visit.visitTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                contactedPersons = visit.contactedPersons?.takeIf { it.isNotBlank() },
                clinicalFindings = visit.clinicalFindings?.takeIf { it.isNotBlank() },
                additionalNotes = visit.additionalNotes?.takeIf { it.isNotBlank() },
                address = visit.address?.takeIf { it.isNotBlank() },
                latitude = visit.latitude,
                longitude = visit.longitude,
                status = visit.status.name
            )

            val response = visitApiService.createVisit(request)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val createdVisit = Visit(
                        id = responseBody.id,
                        customerId = responseBody.customerId,
                        salespersonId = responseBody.salespersonId,
                        visitDate = java.time.LocalDate.parse(responseBody.visitDate),
                        visitTime = java.time.LocalTime.parse(responseBody.visitTime),
                        contactedPersons = responseBody.contactedPersons,
                        clinicalFindings = responseBody.clinicalFindings,
                        additionalNotes = responseBody.additionalNotes,
                        address = responseBody.address,
                        latitude = responseBody.latitude,
                        longitude = responseBody.longitude,
                        createdAt = responseBody.createdAt,
                        updatedAt = responseBody.updatedAt,
                        status = responseBody.status?.let { VisitStatus.valueOf(it) } ?: VisitStatus.PROGRAMADA
                    )
                    Result.success(createdVisit)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateVisit(visitId: Int, visit: Visit): Result<Visit> {
        return try {
            val request = UpdateVisitRequest(
                customerId = visit.customerId,
                salespersonId = visit.salespersonId,
                visitDate = visit.visitDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                visitTime = visit.visitTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                contactedPersons = visit.contactedPersons?.takeIf { it.isNotBlank() },
                clinicalFindings = visit.clinicalFindings?.takeIf { it.isNotBlank() },
                additionalNotes = visit.additionalNotes?.takeIf { it.isNotBlank() },
                address = visit.address?.takeIf { it.isNotBlank() },
                latitude = visit.latitude,
                longitude = visit.longitude
            )

            val response = visitApiService.updateVisit(visitId, request)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val updatedVisit = Visit(
                        id = responseBody.id,
                        customerId = responseBody.customerId,
                        salespersonId = responseBody.salespersonId,
                        visitDate = java.time.LocalDate.parse(responseBody.visitDate),
                        visitTime = java.time.LocalTime.parse(responseBody.visitTime),
                        contactedPersons = responseBody.contactedPersons,
                        clinicalFindings = responseBody.clinicalFindings,
                        additionalNotes = responseBody.additionalNotes,
                        address = responseBody.address,
                        latitude = responseBody.latitude,
                        longitude = responseBody.longitude,
                        createdAt = responseBody.createdAt,
                        updatedAt = responseBody.updatedAt,
                        status = responseBody.status?.let { VisitStatus.valueOf(it) } ?: VisitStatus.PROGRAMADA
                    )
                    Result.success(updatedVisit)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completeVisit(visitId: Int): Result<Visit> {
        return try {
            val response = visitApiService.completeVisit(visitId)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val completedVisit = Visit(
                        id = responseBody.id,
                        customerId = responseBody.customerId,
                        salespersonId = responseBody.salespersonId,
                        visitDate = java.time.LocalDate.parse(responseBody.visitDate),
                        visitTime = java.time.LocalTime.parse(responseBody.visitTime),
                        contactedPersons = responseBody.contactedPersons,
                        clinicalFindings = responseBody.clinicalFindings,
                        additionalNotes = responseBody.additionalNotes,
                        address = responseBody.address,
                        latitude = responseBody.latitude,
                        longitude = responseBody.longitude,
                        createdAt = responseBody.createdAt,
                        updatedAt = responseBody.updatedAt,
                        status = responseBody.status?.let { VisitStatus.valueOf(it) } ?: VisitStatus.COMPLETADA
                    )
                    Result.success(completedVisit)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}