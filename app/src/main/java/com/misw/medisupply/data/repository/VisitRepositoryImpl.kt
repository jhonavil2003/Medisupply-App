package com.misw.medisupply.data.repository

import com.misw.medisupply.data.network.api.VisitApiService
import com.misw.medisupply.domain.model.visit.*
import com.misw.medisupply.domain.repository.VisitRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitRepositoryImpl @Inject constructor(
    private val visitApiService: VisitApiService
) : VisitRepository {

    override suspend fun createVisit(visit: Visit): Result<Visit> {
        return try {
            val response = visitApiService.createVisit(visit)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Result.success(responseBody)
                } else {
                    Result.failure(Exception("Respuesta del servidor vacía"))
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
            val response = visitApiService.updateVisit(visitId, visit)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Result.success(responseBody)
                } else {
                    Result.failure(Exception("Respuesta del servidor vacía"))
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
                    Result.success(responseBody)
                } else {
                    Result.failure(Exception("Respuesta del servidor vacía"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    override suspend fun uploadFile(visitId: Int, file: File, originalFileName: String?): Result<VisitFile> {
        return try {
            // Determinar el tipo MIME usando el nombre original si está disponible
            val fileNameToUse = originalFileName ?: file.name
            val extension = fileNameToUse.substringAfterLast('.', "").lowercase()
            val mimeType = getMimeType(extension)
            
            // Crear RequestBody para el archivo
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", fileNameToUse, requestFile)

            val response = visitApiService.uploadFile(visitId, multipartBody)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.success && responseBody.file != null) {
                    Result.success(responseBody.file)
                } else {
                    val errorMsg = responseBody?.message ?: "Error uploading file"
                    Result.failure(Exception(errorMsg))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
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
                    Result.failure(Exception("Respuesta del servidor vacía"))
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

    private fun getMimeType(extension: String): String {
        return when (extension) {
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
            "xml" -> "application/xml"
            "zip" -> "application/zip"
            "ppt" -> "application/vnd.ms-powerpoint"
            "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            else -> "application/octet-stream"
        }
    }
}