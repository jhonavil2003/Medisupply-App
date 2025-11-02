package com.misw.medisupply.domain.usecase.visit

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.repository.VisitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import javax.inject.Inject

/**
 * Use case para descargar archivos de una visita
 */
class DownloadFileUseCase @Inject constructor(
    private val visitRepository: VisitRepository
) {
    
    /**
     * Descargar archivo de una visita
     * 
     * @param visitId ID de la visita
     * @param fileId ID del archivo a descargar
     * @return Flow con Resource<ResponseBody>
     */
    operator fun invoke(visitId: Int, fileId: Int): Flow<Resource<ResponseBody>> = flow {
        emit(Resource.Loading())
        
        try {
            val result = visitRepository.downloadFile(visitId, fileId)
            
            if (result.isSuccess) {
                emit(Resource.Success(result.getOrThrow()))
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error desconocido al descargar archivo"
                emit(Resource.Error(error))
            }
            
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error inesperado al descargar archivo"))
        }
    }
}