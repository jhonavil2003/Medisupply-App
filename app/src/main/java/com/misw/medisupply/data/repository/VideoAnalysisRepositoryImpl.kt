package com.misw.medisupply.data.repository

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.data.network.api.VideoAnalysisApiService
import com.misw.medisupply.data.network.dto.video.VideoAnalysisRequest
import com.misw.medisupply.domain.model.video.*
import com.misw.medisupply.domain.repository.VideoAnalysisRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementación del repository de análisis de videos
 */
class VideoAnalysisRepositoryImpl @Inject constructor(
    private val api: VideoAnalysisApiService
) : VideoAnalysisRepository {
    
    override fun analyzeVideo(
        videoUrl: String,
        useRag: Boolean
    ): Flow<Resource<VideoAnalysisResult>> = flow {
        try {
            emit(Resource.Loading())
            
            val request = VideoAnalysisRequest(
                videoUrl = videoUrl,
                useRag = useRag
            )
            
            val response = api.analyzeVideo(request)
            
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    // Mapear DTO a modelo de dominio
                    val result = VideoAnalysisResult(
                        status = dto.status,
                        videoUrl = dto.videoUrl,
                        metadata = AnalysisMetadata(
                            analysisType = dto.metadata.analysisType,
                            framesAnalyzed = dto.metadata.framesAnalyzed,
                            geminiModel = dto.metadata.geminiModel,
                            processingTimeSeconds = dto.metadata.processingTimeSeconds
                        ),
                        videoAnalysis = VideoAnalysis(
                            detectedProducts = dto.videoAnalysis.detectedProducts,
                            competitorBrands = dto.videoAnalysis.competitorBrands,
                            context = dto.videoAnalysis.context,
                            userNeeds = dto.videoAnalysis.userNeeds,
                            opportunities = dto.videoAnalysis.opportunities,
                            suggestedCategories = dto.videoAnalysis.suggestedCategories,
                            confidence = dto.videoAnalysis.confidence
                        ),
                        recommendations = dto.recommendations?.let { rec ->
                            Recommendations(
                                products = rec.products,
                                opportunities = rec.opportunities,
                                actionableInsights = rec.actionableInsights.map { insight ->
                                    ActionableInsight(
                                        type = insight.type,
                                        message = insight.message,
                                        action = insight.action
                                    )
                                },
                                confidenceScore = rec.confidenceScore,
                                reasoning = rec.reasoning,
                                ragFullContext = rec.ragFullContext
                            )
                        },
                        ragContext = dto.ragContext
                    )
                    
                    emit(Resource.Success(result))
                } ?: emit(Resource.Error("Respuesta vacía del servidor"))
                
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Video inválido o muy grande (máx 50MB)"
                    503 -> "Servicio de análisis temporalmente no disponible"
                    else -> "Error al analizar video: ${response.code()}"
                }
                emit(Resource.Error(errorMessage))
            }
            
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.message}"))
        }
    }
    
    override suspend fun checkHealth(): Resource<Boolean> {
        return try {
            val response = api.healthCheck()
            if (response.isSuccessful && response.body()?.status == "healthy") {
                Resource.Success(true)
            } else {
                Resource.Error("Servicio no disponible")
            }
        } catch (e: Exception) {
            Resource.Error("Error al verificar servicio: ${e.message}")
        }
    }
}
