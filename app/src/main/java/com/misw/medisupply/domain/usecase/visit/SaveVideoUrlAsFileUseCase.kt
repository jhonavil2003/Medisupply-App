package com.misw.medisupply.domain.usecase.visit

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.visit.VisitFile
import com.misw.medisupply.domain.repository.VisitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case para guardar URL de video S3 como archivo de visita
 * Envía la URL del video directamente al backend usando JSON (Content-Type: application/json)
 * en lugar de subir un archivo físico (multipart/form-data)
 */
class SaveVideoUrlAsFileUseCase @Inject constructor(
    private val visitRepository: VisitRepository
) {
    
    /**
     * Registra la URL del video S3 en el backend
     * 
     * @param visitId ID de la visita
     * @param videoUrl URL completa del video en S3 (debe ser HTTPS)
     * @return Flow con Resource<VisitFile>
     */
    operator fun invoke(visitId: Int, videoUrl: String): Flow<Resource<VisitFile>> = flow {
        emit(Resource.Loading())
        
        try {
            // Validar que la URL no esté vacía
            if (videoUrl.isBlank()) {
                emit(Resource.Error("La URL del video no puede estar vacía"))
                return@flow
            }
            
            // Validar que sea una URL HTTPS válida
            if (!videoUrl.startsWith("https://")) {
                emit(Resource.Error("La URL debe usar HTTPS"))
                return@flow
            }
            
            // Extraer nombre del archivo de la URL
            val fileName = videoUrl.substringAfterLast("/")
            
            // Validar extensión del archivo
            val extension = fileName.substringAfterLast('.', "").lowercase()
            val validExtensions = listOf("mp4", "avi", "mov", "mkv", "3gp")
            if (extension !in validExtensions) {
                emit(Resource.Error("Extensión de video no válida: .$extension"))
                return@flow
            }
            
            // Determinar MIME type
            val mimeType = when (extension) {
                "mp4" -> "video/mp4"
                "avi" -> "video/x-msvideo"
                "mov" -> "video/quicktime"
                "mkv" -> "video/x-matroska"
                "3gp" -> "video/3gpp"
                else -> "video/mp4"
            }
            
            // Registrar URL directamente en el backend (usando JSON, no multipart)
            val result = visitRepository.registerVideoUrl(
                visitId = visitId,
                videoUrl = videoUrl,
                fileName = fileName,
                fileSize = null, // No tenemos el tamaño exacto aquí
                mimeType = mimeType
            )
            
            if (result.isSuccess) {
                emit(Resource.Success(result.getOrThrow()))
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error desconocido al guardar URL del video"
                emit(Resource.Error(error))
            }
            
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error inesperado al guardar URL del video"))
        }
    }
}
