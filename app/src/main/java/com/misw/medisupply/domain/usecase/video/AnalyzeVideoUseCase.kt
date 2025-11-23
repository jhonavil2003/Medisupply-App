package com.misw.medisupply.domain.usecase.video

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.video.VideoAnalysisResult
import com.misw.medisupply.domain.repository.VideoAnalysisRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case para analizar videos con IA
 */
class AnalyzeVideoUseCase @Inject constructor(
    private val repository: VideoAnalysisRepository
) {
    /**
     * Analizar video y obtener recomendaciones para el asesor comercial
     * 
     * @param videoUrl URL del video en S3
     * @param useRag Si es true, incluye recomendaciones del catálogo MediSupply
     * @return Flow con el resultado del análisis
     */
    operator fun invoke(
        videoUrl: String,
        useRag: Boolean = true
    ): Flow<Resource<VideoAnalysisResult>> {
        return repository.analyzeVideo(videoUrl, useRag)
    }
}
