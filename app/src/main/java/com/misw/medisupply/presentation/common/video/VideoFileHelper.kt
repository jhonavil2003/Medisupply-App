package com.misw.medisupply.presentation.common.video

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

/**
 * Helper para convertir Uri a File
 */
object VideoFileHelper {
    
    /**
     * Convertir Uri de video a File temporal
     * 
     * @param context Contexto de la aplicación
     * @param uri Uri del video
     * @return File temporal con el contenido del video
     */
    fun uriToFile(context: Context, uri: Uri, fileName: String = "temp_video.mp4"): File? {
        try {
            // Obtener información del archivo original
            val projection = arrayOf(
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE
            )
            
            var originalFileName = fileName
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                    originalFileName = cursor.getString(nameIndex) ?: fileName
                }
            }
            
            // Crear archivo temporal en cache
            val tempFile = File(context.cacheDir, originalFileName)
            
            // Copiar contenido del Uri al archivo temporal
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            
            return tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Crear archivo temporal para video capturado
     * 
     * @param context Contexto de la aplicación
     * @return File temporal para guardar video
     */
    fun createTempVideoFile(context: Context): File {
        val timestamp = System.currentTimeMillis()
        val fileName = "VIDEO_$timestamp.mp4"
        return File(context.cacheDir, fileName)
    }
    
    /**
     * Eliminar archivos temporales de videos
     */
    fun cleanupTempFiles(context: Context) {
        context.cacheDir.listFiles()?.forEach { file ->
            if (file.name.startsWith("VIDEO_") || file.name.startsWith("temp_video")) {
                file.delete()
            }
        }
    }
    
    /**
     * Formatear tamaño de archivo
     */
    fun formatFileSize(bytes: Long): String {
        val kb = bytes / 1024
        val mb = kb / 1024
        
        return when {
            mb > 0 -> "$mb MB"
            kb > 0 -> "$kb KB"
            else -> "$bytes bytes"
        }
    }
}
