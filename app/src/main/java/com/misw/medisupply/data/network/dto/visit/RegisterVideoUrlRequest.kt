package com.misw.medisupply.data.network.dto.visit

import com.google.gson.annotations.SerializedName

/**
 * Request para registrar URL de video S3 en el backend
 * Según documentación: POST /visits/{visit_id}/files
 */
data class RegisterVideoUrlRequest(
    @SerializedName("file_url")
    val fileUrl: String,
    
    @SerializedName("file_name")
    val fileName: String,
    
    @SerializedName("file_size")
    val fileSize: Long? = null,
    
    @SerializedName("mime_type")
    val mimeType: String? = null
)
