package com.misw.medisupply.data.network.dto.video

import com.google.gson.annotations.SerializedName

/**
 * Response del análisis de video
 */
data class VideoAnalysisResponse(
    val status: String,
    
    @SerializedName("video_url")
    val videoUrl: String,
    
    val metadata: AnalysisMetadataDto,
    
    @SerializedName("video_analysis")
    val videoAnalysis: VideoAnalysisDto,
    
    val recommendations: RecommendationsDto?,
    
    @SerializedName("rag_context")
    val ragContext: String?
)

/**
 * Metadatos del análisis
 */
data class AnalysisMetadataDto(
    @SerializedName("analysis_type")
    val analysisType: String,
    
    @SerializedName("frames_analyzed")
    val framesAnalyzed: Int,
    
    @SerializedName("gemini_model")
    val geminiModel: String,
    
    @SerializedName("processing_time_seconds")
    val processingTimeSeconds: Float
)

/**
 * Análisis del video
 */
data class VideoAnalysisDto(
    @SerializedName("detected_products")
    val detectedProducts: List<String>,
    
    @SerializedName("competitor_brands")
    val competitorBrands: List<String>,
    
    val context: String,
    
    @SerializedName("user_needs")
    val userNeeds: String,
    
    val opportunities: List<String>,
    
    @SerializedName("suggested_categories")
    val suggestedCategories: List<String>,
    
    val confidence: Float
)

/**
 * Recomendaciones basadas en RAG
 */
data class RecommendationsDto(
    val products: List<String>,
    
    val opportunities: List<String>,
    
    @SerializedName("actionable_insights")
    val actionableInsights: List<ActionableInsightDto>,
    
    @SerializedName("confidence_score")
    val confidenceScore: Float,
    
    val reasoning: String,
    
    @SerializedName("rag_full_context")
    val ragFullContext: String
)

/**
 * Insight accionable
 */
data class ActionableInsightDto(
    val type: String,
    val message: String,
    val action: String
)

/**
 * Response de error
 */
data class VideoAnalysisErrorResponse(
    val error: String,
    
    @SerializedName("status_code")
    val statusCode: Int
)
