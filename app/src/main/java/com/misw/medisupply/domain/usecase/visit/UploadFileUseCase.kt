package com.misw.medisupply.domain.usecase.visit

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.visit.AllowedFileExtensions
import com.misw.medisupply.domain.model.visit.FileValidation
import com.misw.medisupply.domain.model.visit.VisitFile
import com.misw.medisupply.domain.repository.VisitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

/**
 * Use case para subir archivos a una visita
 */
class UploadFileUseCase @Inject constructor(
    private val visitRepository: VisitRepository
) {
    
    /**
     * Subir archivo a una visita con validaciones
     * 
     * @param visitId ID de la visita
     * @param file Archivo a subir
     * @param originalFileName Nombre original del archivo (opcional)
     * @return Flow con Resource<VisitFile>
     */
    operator fun invoke(visitId: Int, file: File, originalFileName: String? = null): Flow<Resource<VisitFile>> = flow {
        emit(Resource.Loading())
        
        try {
            // Validar que el archivo existe
            if (!file.exists()) {
                emit(Resource.Error("El archivo no existe"))
                return@flow
            }
            
            // Validar tamaño del archivo (10MB máximo)
            if (!FileValidation.isValidSize(file.length())) {
                emit(Resource.Error("El archivo no puede superar ${FileValidation.MAX_FILE_SIZE_MB}MB"))
                return@flow
            }
            
            // Usar el nombre original si está disponible, sino el nombre del archivo
            val fileNameToValidate = originalFileName ?: file.name
            
            // Validar nombre del archivo
            if (fileNameToValidate.isNullOrBlank()) {
                emit(Resource.Error("El archivo no tiene un nombre válido"))
                return@flow
            }
            
            // Validar extensión del archivo
            val extension = AllowedFileExtensions.getExtension(fileNameToValidate)
            if (!AllowedFileExtensions.isAllowed(extension)) {
                val allowedExts = listOf("PDF", "DOC", "DOCX", "TXT", "JPG", "PNG", "XLS", "XLSX", "ZIP").joinToString(", ")
                emit(Resource.Error("Extensión no permitida. Formatos válidos: $allowedExts"))
                return@flow
            }
            
            // Subir archivo
            val result = visitRepository.uploadFile(visitId, file, originalFileName)
            
            if (result.isSuccess) {
                emit(Resource.Success(result.getOrThrow()))
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error desconocido al subir archivo"
                emit(Resource.Error(error))
            }
            
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error inesperado al subir archivo"))
        }
    }
}