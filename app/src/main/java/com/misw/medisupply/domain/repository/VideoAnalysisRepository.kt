package com.misw.medisupply.domain.repository

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.video.VideoAnalysisResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository para análisis de videos
 */
interface VideoAnalysisRepository {
    
    /**
     * Analizar video y obtener recomendaciones
     * @param videoUrl URL del video en S3
     * @param useRag Si se deben incluir recomendaciones del catálogo MediSupply
     * @return Flow con el resultado del análisis
     */
    fun analyzeVideo(
        videoUrl: String,
        useRag: Boolean = true
    ): Flow<Resource<VideoAnalysisResult>>
    
    /**
     * Verificar estado del servicio de análisis
     */
    suspend fun checkHealth(): Resource<Boolean>
}
