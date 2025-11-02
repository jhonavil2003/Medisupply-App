package com.misw.medisupply.domain.usecase.visit

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.visit.VisitFile
import com.misw.medisupply.domain.repository.VisitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case para obtener archivos de una visita
 */
class GetVisitFilesUseCase @Inject constructor(
    private val visitRepository: VisitRepository
) {
    
    /**
     * Obtener lista de archivos de una visita
     * 
     * @param visitId ID de la visita
     * @return Flow con Resource<List<VisitFile>>
     */
    operator fun invoke(visitId: Int): Flow<Resource<List<VisitFile>>> = flow {
        emit(Resource.Loading())
        
        try {
            val result = visitRepository.getVisitFiles(visitId)
            
            if (result.isSuccess) {
                val files = result.getOrThrow()
                emit(Resource.Success(files))
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error desconocido al obtener archivos"
                emit(Resource.Error(error))
            }
            
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error inesperado al obtener archivos"))
        }
    }
}