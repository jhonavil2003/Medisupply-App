package com.misw.medisupply.domain.usecase.video

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.aws.S3Config
import com.misw.medisupply.data.aws.S3UploadService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

/**
 * Use case para subir videos a S3
 */
class UploadVideoToS3UseCase @Inject constructor(
    private val s3UploadService: S3UploadService
) {
    
    /**
     * Subir video a S3 con validaciones
     * 
     * @param videoFile Archivo de video a subir
     * @param customerId ID del cliente (opcional)
     * @param visitId ID de la visita (opcional)
     * @return Flow con Resource<String> (URL del video en S3)
     */
    operator fun invoke(videoFile: File, customerId: Int? = null, visitId: Int? = null): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        
        try {
            // 1. Validar que el archivo existe
            if (!videoFile.exists()) {
                emit(Resource.Error("El archivo no existe"))
                return@flow
            }
            
            // 2. Validar tamaño del archivo
            val fileSizeMB = videoFile.length() / (1024 * 1024)
            if (videoFile.length() > S3Config.MAX_VIDEO_SIZE_BYTES) {
                emit(Resource.Error("Video demasiado grande: $fileSizeMB MB (máximo: ${S3Config.MAX_VIDEO_SIZE_MB} MB)"))
                return@flow
            }
            
            if (videoFile.length() == 0L) {
                emit(Resource.Error("El archivo está vacío"))
                return@flow
            }
            
            // 3. Validar formato
            val validExtensions = listOf("mp4", "avi", "mov", "mkv", "3gp")
            val extension = videoFile.extension.lowercase()
            if (extension !in validExtensions) {
                emit(Resource.Error("Formato no soportado: $extension. Use: ${validExtensions.joinToString(", ").uppercase()}"))
                return@flow
            }
            
            // 4. Subir archivo a S3 (incluye customerId y visitId si están disponibles)
            val result = s3UploadService.uploadVideo(videoFile, customerId, visitId)
            
            if (result.isSuccess) {
                val videoUrl = result.getOrThrow()
                emit(Resource.Success(videoUrl))
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error desconocido al subir video"
                emit(Resource.Error(error))
            }
            
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error inesperado al subir video"))
        }
    }
}
