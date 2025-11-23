package com.misw.medisupply.domain.model.video

/**
 * Resultado del análisis de video
 */
data class VideoAnalysisResult(
    val status: String,
    val videoUrl: String,
    val metadata: AnalysisMetadata,
    val videoAnalysis: VideoAnalysis,
    val recommendations: Recommendations?,
    val ragContext: String?
)

/**
 * Metadatos del análisis
 */
data class AnalysisMetadata(
    val analysisType: String,
    val framesAnalyzed: Int,
    val geminiModel: String,
    val processingTimeSeconds: Float
)

/**
 * Análisis del video con productos y oportunidades detectadas
 */
data class VideoAnalysis(
    val detectedProducts: List<String>,
    val competitorBrands: List<String>,
    val context: String,
    val userNeeds: String,
    val opportunities: List<String>,
    val suggestedCategories: List<String>,
    val confidence: Float
)

/**
 * Recomendaciones basadas en el catálogo MediSupply
 */
data class Recommendations(
    val products: List<String>,
    val opportunities: List<String>,
    val actionableInsights: List<ActionableInsight>,
    val confidenceScore: Float,
    val reasoning: String,
    val ragFullContext: String
)

/**
 * Insight accionable para el asesor comercial
 */
data class ActionableInsight(
    val type: String,
    val message: String,
    val action: String
)
