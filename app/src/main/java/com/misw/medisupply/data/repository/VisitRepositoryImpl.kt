package com.misw.medisupply.data.repository

import com.misw.medisupply.data.network.api.VisitApiService
import com.misw.medisupply.data.network.dto.visit.CreateVisitRequest
import com.misw.medisupply.data.network.dto.visit.UpdateVisitRequest
import com.misw.medisupply.domain.model.visit.Visit
import com.misw.medisupply.domain.model.visit.VisitFile
import com.misw.medisupply.domain.model.visit.VisitStatus
import com.misw.medisupply.domain.repository.VisitRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File
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
                    val visitData = responseBody.visit
                    val createdVisit = Visit(
                        id = visitData.id,
                        customerId = visitData.customerId,
                        salespersonId = visitData.salespersonId,
                        visitDate = java.time.LocalDate.parse(visitData.visitDate),
                        visitTime = java.time.LocalTime.parse(visitData.visitTime),
                        contactedPersons = visitData.contactedPersons,
                        clinicalFindings = visitData.clinicalFindings,
                        additionalNotes = visitData.additionalNotes,
                        address = visitData.address,
                        latitude = visitData.latitude,
                        longitude = visitData.longitude,
                        createdAt = visitData.createdAt,
                        updatedAt = visitData.updatedAt,
                        status = visitData.status?.let { VisitStatus.valueOf(it) } ?: VisitStatus.PROGRAMADA
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
                    val visitData = responseBody.visit
                    val updatedVisit = Visit(
                        id = visitData.id,
                        customerId = visitData.customerId,
                        salespersonId = visitData.salespersonId,
                        visitDate = java.time.LocalDate.parse(visitData.visitDate),
                        visitTime = java.time.LocalTime.parse(visitData.visitTime),
                        contactedPersons = visitData.contactedPersons,
                        clinicalFindings = visitData.clinicalFindings,
                        additionalNotes = visitData.additionalNotes,
                        address = visitData.address,
                        latitude = visitData.latitude,
                        longitude = visitData.longitude,
                        createdAt = visitData.createdAt,
                        updatedAt = visitData.updatedAt,
                        status = visitData.status?.let { VisitStatus.valueOf(it) } ?: VisitStatus.PROGRAMADA
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
                    val visitData = responseBody.visit
                    val completedVisit = Visit(
                        id = visitData.id,
                        customerId = visitData.customerId,
                        salespersonId = visitData.salespersonId,
                        visitDate = java.time.LocalDate.parse(visitData.visitDate),
                        visitTime = java.time.LocalTime.parse(visitData.visitTime),
                        contactedPersons = visitData.contactedPersons,
                        clinicalFindings = visitData.clinicalFindings,
                        additionalNotes = visitData.additionalNotes,
                        address = visitData.address,
                        latitude = visitData.latitude,
                        longitude = visitData.longitude,
                        createdAt = visitData.createdAt,
                        updatedAt = visitData.updatedAt,
                        status = visitData.status?.let { VisitStatus.valueOf(it) } ?: VisitStatus.COMPLETADA
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

    // ================================
    // IMPLEMENTACIÓN PARA ARCHIVOS
    // ================================

    override suspend fun uploadFile(visitId: Int, file: File, originalFileName: String?): Result<VisitFile> {
        return try {
            // Determinar el tipo MIME usando el nombre original si está disponible
            val fileNameToUse = originalFileName ?: file.name
            val extension = fileNameToUse.substringAfterLast('.', "").lowercase()
            val mimeType = when (extension) {
                "pdf" -> "application/pdf"
                "doc" -> "application/msword"
                "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                "txt" -> "text/plain"
                "rtf" -> "application/rtf"
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                "gif" -> "image/gif"
                "bmp" -> "image/bmp"
                "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                "xls" -> "application/vnd.ms-excel"
                "csv" -> "text/csv"
                "zip" -> "application/zip"
                "rar" -> "application/vnd.rar"
                else -> "application/octet-stream"
            }
            
            android.util.Log.d("VisitRepository", "Preparando upload: archivo=$fileNameToUse (temp: ${file.name}), tamaño=${file.length()}, mimeType=$mimeType")

            // Crear RequestBody para el archivo
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", fileNameToUse, requestFile)

            val response = visitApiService.uploadFile(visitId, multipartBody)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                android.util.Log.d("VisitRepository", "Upload response: ${response.code()}, body=$responseBody")
                if (responseBody != null && responseBody.success && responseBody.file != null) {
                    android.util.Log.d("VisitRepository", "Upload exitoso: ${responseBody.file.fileName}")
                    Result.success(responseBody.file)
                } else {
                    val errorMsg = responseBody?.message ?: "Error uploading file"
                    android.util.Log.e("VisitRepository", "Upload fallido: $errorMsg")
                    Result.failure(Exception(errorMsg))
                }
            } else {
                val errorMsg = "Error ${response.code()}: ${response.message()}"
                android.util.Log.e("VisitRepository", "HTTP Error: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getVisitFiles(visitId: Int): Result<List<VisitFile>> {
        return try {
            val response = visitApiService.getVisitFiles(visitId, false)
            
            if (response.isSuccessful) {
                val files = response.body() ?: emptyList()
                Result.success(files)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun downloadFile(visitId: Int, fileId: Int): Result<ResponseBody> {
        return try {
            val response = visitApiService.downloadFile(visitId, fileId)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Result.success(responseBody)
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

    override suspend fun deleteFile(fileId: Int): Result<Boolean> {
        return try {
            val response = visitApiService.deleteFileGlobal(fileId)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.success) {
                    Result.success(true)
                } else {
                    Result.failure(Exception(responseBody?.message ?: "Error deleting file"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}