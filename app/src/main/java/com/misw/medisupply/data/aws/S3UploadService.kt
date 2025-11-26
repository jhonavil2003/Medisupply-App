package com.misw.medisupply.data.aws

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Servicio para subir videos a AWS S3
 */
@Singleton
class S3UploadService @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val TAG = "S3UploadService"
    }
    
    /**
     * Sube un video a S3 usando credenciales fijas.
     * 
     * @param videoFile Archivo de video a subir
     * @param customerId ID del cliente (opcional)
     * @param visitId ID de la visita (opcional)
     * @return Result con la URL del video en S3
     */
    suspend fun uploadVideo(videoFile: File, customerId: Int? = null, visitId: Int? = null): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Iniciando subida de video: ${videoFile.name}")
            
            // 1. Validar que el archivo existe
            if (!videoFile.exists()) {
                Log.e(TAG, "El archivo no existe: ${videoFile.absolutePath}")
                return@withContext Result.failure(Exception("El archivo no existe"))
            }
            
            // 2. Validar tamaño del video
            val fileSizeBytes = videoFile.length()
            val fileSizeMB = fileSizeBytes / (1024 * 1024)
            Log.d(TAG, "Tamaño del video: $fileSizeMB MB")
            
            if (fileSizeBytes > S3Config.MAX_VIDEO_SIZE_BYTES) {
                Log.e(TAG, "Video demasiado grande: $fileSizeMB MB")
                return@withContext Result.failure(
                    Exception("Video demasiado grande: $fileSizeMB MB (máximo: ${S3Config.MAX_VIDEO_SIZE_MB} MB)")
                )
            }
            
            // 3. Validar formato
            if (!isValidVideoFormat(videoFile)) {
                Log.e(TAG, "Formato de video no soportado: ${videoFile.extension}")
                return@withContext Result.failure(
                    Exception("Formato de video no soportado. Use MP4, AVI, MOV o MKV")
                )
            }
            
            // 4. Crear cliente S3 con credenciales fijas
            Log.d(TAG, "Creando cliente S3...")
            val s3Client = S3Config.createS3Client()
            
            // 5. Generar key único para el video (incluye cliente y visita si están disponibles)
            val videoKey = S3Config.generateVideoKey(videoFile.name, customerId, visitId)
            Log.d(TAG, "Video key generado: $videoKey")
            
            // 6. Configurar metadata
            val metadata = ObjectMetadata().apply {
                contentType = getMimeType(videoFile)
                contentLength = fileSizeBytes
                addUserMetadata("uploaded-by", "medisupply-mobile-app")
                addUserMetadata("upload-date", System.currentTimeMillis().toString())
                addUserMetadata("device-id", getDeviceId())
                addUserMetadata("original-filename", videoFile.name)
            }
            
            Log.d(TAG, "Metadata configurado - ContentType: ${metadata.contentType}, Size: ${metadata.contentLength}")
            
            // 7. Crear request de subida
            val putRequest = PutObjectRequest(
                S3Config.BUCKET_NAME,
                videoKey,
                videoFile
            ).withMetadata(metadata)
            
            // 8. Subir archivo
            Log.d(TAG, "Subiendo archivo a S3...")
            s3Client.putObject(putRequest)
            Log.d(TAG, "Archivo subido exitosamente")
            
            // 9. Construir URL del video
            val videoUrl = S3Config.generateVideoUrl(videoKey)
            Log.d(TAG, "URL del video: $videoUrl")
            
            Result.success(videoUrl)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error al subir video a S3", e)
            Result.failure(e)
        }
    }
    
    /**
     * Validar formato de video.
     */
    private fun isValidVideoFormat(file: File): Boolean {
        val validExtensions = listOf("mp4", "avi", "mov", "mkv", "3gp")
        val extension = file.extension.lowercase()
        return extension in validExtensions
    }
    
    /**
     * Obtener MIME type del video.
     */
    private fun getMimeType(file: File): String {
        return when (file.extension.lowercase()) {
            "mp4" -> "video/mp4"
            "avi" -> "video/x-msvideo"
            "mov" -> "video/quicktime"
            "mkv" -> "video/x-matroska"
            "3gp" -> "video/3gpp"
            else -> "video/mp4"
        }
    }
    
    /**
     * Obtener ID único del dispositivo (para auditoría).
     */
    private fun getDeviceId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown"
    }
}
