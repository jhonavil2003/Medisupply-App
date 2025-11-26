package com.misw.medisupply.data.aws

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.misw.medisupply.BuildConfig
import java.text.SimpleDateFormat
import java.util.*

/**
 * Configuración para AWS S3
 * Credenciales cargadas desde local.properties (no versionadas en Git)
 */
object S3Config {
    // Credenciales desde BuildConfig (generadas desde local.properties)
    private val ACCESS_KEY_ID = BuildConfig.AWS_ACCESS_KEY_ID
    private val SECRET_ACCESS_KEY = BuildConfig.AWS_SECRET_ACCESS_KEY
    
    val BUCKET_NAME = BuildConfig.AWS_S3_BUCKET_NAME
    val REGION = BuildConfig.AWS_S3_REGION
    const val MAX_VIDEO_SIZE_MB = 50
    const val MAX_VIDEO_SIZE_BYTES = MAX_VIDEO_SIZE_MB * 1024 * 1024L // 50MB
    
    /**
     * Crear cliente S3 con credenciales fijas.
     */
    fun createS3Client(): AmazonS3Client {
        val credentials = BasicAWSCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY)
        return AmazonS3Client(credentials).apply {
            setRegion(Region.getRegion(Regions.fromName(REGION)))
        }
    }
    
    /**
     * Generar key único para video en S3.
     * Formato: videos/YYYY/MM/DD/cliente_{customerId}_visita_{visitId}_{timestamp}.{ext}
     * Si no hay visitId, usa solo timestamp único
     */
    fun generateVideoKey(fileName: String, customerId: Int? = null, visitId: Int? = null): String {
        val datePath = SimpleDateFormat("yyyy/MM/dd", Locale.US).format(Date())
        val timestamp = System.currentTimeMillis()
        val extension = fileName.substringAfterLast('.', "mp4")
        
        return when {
            customerId != null && visitId != null -> 
                "videos/$datePath/cliente_${customerId}_visita_${visitId}_${timestamp}.${extension}"
            customerId != null -> 
                "videos/$datePath/cliente_${customerId}_${timestamp}.${extension}"
            else -> 
                "videos/$datePath/${timestamp}.${extension}"
        }
    }
    
    /**
     * Generar URL del video en S3
     */
    fun generateVideoUrl(videoKey: String): String {
        return "https://$BUCKET_NAME.s3.$REGION.amazonaws.com/$videoKey"
    }
}
