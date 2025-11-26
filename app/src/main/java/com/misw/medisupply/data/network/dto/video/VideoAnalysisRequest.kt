package com.misw.medisupply.data.network.dto.video

import com.google.gson.annotations.SerializedName

/**
 * Request para análisis de video
 * POST /api/videos/analyze
 */
data class VideoAnalysisRequest(
    @SerializedName("video_url")
    val videoUrl: String,
    
    @SerializedName("use_rag")
    val useRag: Boolean = true
)
