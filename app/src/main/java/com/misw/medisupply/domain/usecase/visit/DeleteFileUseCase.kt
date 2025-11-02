package com.misw.medisupply.domain.usecase.visit

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.repository.VisitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case para eliminar archivos de una visita
 */
class DeleteFileUseCase @Inject constructor(
    private val visitRepository: VisitRepository
) {
    
    /**
     * Eliminar archivo de una visita
     * 
     * @param fileId ID del archivo a eliminar
     * @return Flow con Resource<Boolean>
     */
    operator fun invoke(fileId: Int): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        
        try {
            val result = visitRepository.deleteFile(fileId)
            
            if (result.isSuccess) {
                emit(Resource.Success(true))
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error desconocido al eliminar archivo"
                emit(Resource.Error(error))
            }
            
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error inesperado al eliminar archivo"))
        }
    }
}