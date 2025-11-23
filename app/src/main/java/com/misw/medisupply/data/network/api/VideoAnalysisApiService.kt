package com.misw.medisupply.data.network.api

import com.misw.medisupply.data.network.dto.video.VideoAnalysisRequest
import com.misw.medisupply.data.network.dto.video.VideoAnalysisResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * API Service para análisis de videos con IA
 * Base URL: http://medisupply-alb-590358283.us-east-1.elb.amazonaws.com
 */
interface VideoAnalysisApiService {
    
    /**
     * Health check del servicio de análisis
     */
    @GET("/health")
    suspend fun healthCheck(): Response<HealthCheckResponse>
    
    /**
     * Analizar video y obtener recomendaciones
     * Timeout recomendado: 60 segundos (el análisis puede tomar 10-30 segundos)
     */
    @POST("/api/videos/analyze")
    suspend fun analyzeVideo(
        @Body request: VideoAnalysisRequest
    ): Response<VideoAnalysisResponse>
}

/**
 * Response del health check
 */
data class HealthCheckResponse(
    val message: String,
    val service: String,
    val status: String,
    val version: String
)
