package com.misw.medisupply.domain.model.visit

/**
 * Representa un archivo adjunto a una visita
 */
data class VisitFile(
    val id: Int = 0,
    val visitId: Int,
    val fileName: String = "",
    val filePath: String = "",
    val fileSize: Long = 0L,
    val mimeType: String = "application/octet-stream",
    val uploadDate: String = ""
)

/**
 * Respuesta de la API al subir un archivo
 */
data class UploadFileResponse(
    val success: Boolean,
    val message: String,
    val file: VisitFile?
)

/**
 * Respuesta de la API al eliminar un archivo
 */
data class DeleteFileResponse(
    val success: Boolean,
    val message: String,
    val deletedFileId: Int?
)

/**
 * Respuesta de la API al obtener archivos con metadata
 */
data class FilesWithMetadataResponse(
    val visitId: Int,
    val files: List<VisitFile>,
    val totalFiles: Int,
    val totalSize: Long
)

/**
 * Estadísticas de archivos de una visita
 */
data class FileStatsResponse(
    val visitId: Int,
    val totalFiles: Int,
    val totalSize: Long,
    val averageSize: Long,
    val fileTypes: Map<String, FileTypeStats>
)

data class FileTypeStats(
    val count: Int,
    val size: Long
)

/**
 * Extensiones de archivo permitidas según la documentación de API
 */
object AllowedFileExtensions {
    val DOCUMENTS = listOf(".pdf", ".doc", ".docx", ".txt", ".rtf")
    val IMAGES = listOf(".jpg", ".jpeg", ".png", ".gif", ".bmp")
    val SPREADSHEETS = listOf(".xlsx", ".xls", ".csv")
    val COMPRESSED = listOf(".zip", ".rar")
    
    val ALL = DOCUMENTS + IMAGES + SPREADSHEETS + COMPRESSED
    
    /**
     * Validar si una extensión es permitida
     */
    fun isAllowed(extension: String?): Boolean {
        if (extension.isNullOrBlank()) return false
        return ALL.contains(extension.lowercase())
    }
    
    /**
     * Obtener extensión de un nombre de archivo
     */
    fun getExtension(fileName: String?): String {
        if (fileName.isNullOrBlank()) return ""
        return fileName.substringAfterLast(".", "")
            .let { if (it.isNotEmpty()) ".$it" else "" }
    }
}

/**
 * Constantes para validación de archivos
 */
object FileValidation {
    const val MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024L // 10MB
    const val MAX_FILE_SIZE_MB = 10
    
    /**
     * Validar tamaño de archivo
     */
    fun isValidSize(sizeBytes: Long): Boolean {
        return sizeBytes <= MAX_FILE_SIZE_BYTES
    }
    
    /**
     * Formatear tamaño de archivo para mostrar en UI
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