package com.misw.medisupply.presentation.salesforce.screens.visits.components

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.misw.medisupply.domain.model.visit.AllowedFileExtensions
import com.misw.medisupply.domain.model.visit.FileValidation
import com.misw.medisupply.domain.model.visit.VisitFile
import com.misw.medisupply.presentation.salesforce.screens.visits.state.CreateVisitUiState
import com.misw.medisupply.presentation.salesforce.screens.visits.viewmodel.CreateVisitViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun ArchivosTabContent(
    uiState: CreateVisitUiState,
    viewModel: CreateVisitViewModel
) {
    val context = LocalContext.current
    
    // File picker launcher
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { 
            handleFileSelection(uri, context, viewModel)
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Archivos adjuntos", 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )
            
            // Mensaje informativo
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
            ) {
                Text(
                    text = "📎 Adjunta fotos, documentos y otros archivos relacionados con tu visita. Formatos: PDF, DOC, TXT, JPG, PNG, etc. Máximo 10MB por archivo.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2E7D32)
                )
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Mostrar error si hay alguno
            uiState.fileError?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD32F2F)
                        )
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { viewModel.clearFileError() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
            
            // Botón para seleccionar archivo
            OutlinedButton(
                onClick = { filePicker.launch("*/*") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isUploadingFile,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF1565C0)
                )
            ) {
                if (uiState.isUploadingFile) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color(0xFF1565C0),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Subiendo...")
                } else {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Seleccionar archivo")
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Lista de archivos
            if (uiState.isLoadingFiles) {
                LoadingFilesCard()
            } else if (uiState.visitFiles.isEmpty()) {
                EmptyFilesCard()
            } else {
                FilesListCard(
                    files = uiState.visitFiles,
                    onDeleteFile = { fileId -> viewModel.deleteFile(fileId) },
                    isDeleting = uiState.isDeletingFile
                )
            }
            
            // Botón "Completar Visita" - Solo visible cuando la visita está guardada
            if (uiState.isVisitSaved) {
                Spacer(Modifier.height(24.dp))
                
                Button(
                    onClick = { viewModel.completeVisit() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Completar Visita")
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingFilesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = Color(0xFF1565C0)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Cargando archivos...",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}

@Composable
private fun EmptyFilesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.FilePresent,
                    contentDescription = null,
                    tint = Color(0xFF9E9E9E),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "No hay archivos adjuntos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF757575)
                )
                Text(
                    text = "Usa el botón de arriba para agregar archivos",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9E9E9E)
                )
            }
        }
    }
}

@Composable
private fun FilesListCard(
    files: List<VisitFile>,
    onDeleteFile: (Int) -> Unit,
    isDeleting: Boolean
) {
    LazyColumn(
        modifier = Modifier.heightIn(max = 300.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(files) { file ->
            FileItem(
                file = file,
                onDelete = { onDeleteFile(file.id) },
                isDeleting = isDeleting
            )
        }
    }
}

@Composable
private fun FileItem(
    file: VisitFile,
    onDelete: () -> Unit,
    isDeleting: Boolean
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del tipo de archivo
            Icon(
                imageVector = getFileIcon(file.fileName),
                contentDescription = null,
                tint = getFileIconColor(file.fileName),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(Modifier.width(12.dp))
            
            // Información del archivo
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.fileName.takeIf { !it.isNullOrBlank() } ?: "Archivo sin nombre",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )
                Text(
                    text = FileValidation.formatFileSize(file.fileSize),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF757575)
                )
            }
            
            // Botón eliminar
            IconButton(
                onClick = { showDeleteDialog = true },
                enabled = !isDeleting
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color(0xFFD32F2F),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar archivo",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
    
    // Diálogo de confirmación de eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar archivo") },
            text = { Text("¿Estás seguro de que quieres eliminar \"${file.fileName.takeIf { !it.isNullOrBlank() } ?: "este archivo"}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("Eliminar", color = Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun getFileIcon(fileName: String?): androidx.compose.ui.graphics.vector.ImageVector {
    if (fileName.isNullOrBlank()) {
        return Icons.Default.InsertDriveFile
    }
    
    val extension = try {
        AllowedFileExtensions.getExtension(fileName).lowercase()
    } catch (e: Exception) {
        ""
    }
    
    return when {
        extension in listOf(".pdf") -> Icons.Default.PictureAsPdf
        extension in listOf(".doc", ".docx", ".txt", ".rtf") -> Icons.Default.Description
        extension in listOf(".jpg", ".jpeg", ".png", ".gif", ".bmp") -> Icons.Default.Image
        extension in listOf(".xlsx", ".xls", ".csv") -> Icons.Default.TableChart
        extension in listOf(".zip", ".rar") -> Icons.Default.Archive
        else -> Icons.Default.InsertDriveFile
    }
}

private fun getFileIconColor(fileName: String?): Color {
    if (fileName.isNullOrBlank()) {
        return Color(0xFF757575) // Gris por defecto
    }
    
    val extension = try {
        AllowedFileExtensions.getExtension(fileName).lowercase()
    } catch (e: Exception) {
        ""
    }
    
    return when {
        extension in listOf(".pdf") -> Color(0xFFD32F2F) // Rojo
        extension in listOf(".doc", ".docx", ".txt", ".rtf") -> Color(0xFF1565C0) // Azul
        extension in listOf(".jpg", ".jpeg", ".png", ".gif", ".bmp") -> Color(0xFF388E3C) // Verde
        extension in listOf(".xlsx", ".xls", ".csv") -> Color(0xFF388E3C) // Verde
        extension in listOf(".zip", ".rar") -> Color(0xFF6A1B9A) // Morado
        else -> Color(0xFF757575) // Gris
    }
}

/**
 * Maneja la selección de archivo y lo sube
 */
private fun handleFileSelection(uri: Uri, context: Context, viewModel: CreateVisitViewModel) {
    try {
        val contentResolver = context.contentResolver
        var fileName = "archivo_${System.currentTimeMillis()}"
        var fileSize = 0L
        
        // Intentar obtener metadatos del archivo de forma segura
        try {
            contentResolver.query(
                uri,
                arrayOf(
                    android.provider.OpenableColumns.DISPLAY_NAME,
                    android.provider.OpenableColumns.SIZE
                ),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    // Obtener nombre del archivo
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0 && !cursor.isNull(nameIndex)) {
                        val displayName = cursor.getString(nameIndex)
                        if (!displayName.isNullOrBlank()) {
                            fileName = displayName
                        }
                    }
                    
                    // Obtener tamaño del archivo
                    val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                    if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                        fileSize = cursor.getLong(sizeIndex)
                    }
                }
            }
        } catch (e: Exception) {
            // Si falla obtener metadatos, continúa con valores por defecto
            android.util.Log.w("FileSelection", "No se pudieron obtener metadatos: ${e.message}")
        }
        
        // Si no se pudo obtener el tamaño, intentar obtenerlo del InputStream
        if (fileSize == 0L) {
            try {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    fileSize = inputStream.available().toLong()
                }
            } catch (e: Exception) {
                android.util.Log.w("FileSelection", "No se pudo obtener tamaño del archivo: ${e.message}")
            }
        }
        
        // Validar extensión
        val extension = AllowedFileExtensions.getExtension(fileName)
        if (!AllowedFileExtensions.isAllowed(extension)) {
            viewModel.clearFileError() // Limpiar error anterior
            // Usar un handler para mostrar el error después de un pequeño delay
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                // El error será manejado por el ViewModel cuando se llame uploadFile
            }
            return
        }
        
        // Validar tamaño (solo si se pudo obtener)
        if (fileSize > 0 && !FileValidation.isValidSize(fileSize)) {
            viewModel.clearFileError()
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                // El error será manejado por el ViewModel cuando se llame uploadFile  
            }
            return
        }
        
        // Crear archivo temporal con nombre único para evitar conflictos
        val sanitizedFileName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val tempFile = File(context.cacheDir, "temp_${System.currentTimeMillis()}_$sanitizedFileName")
        
        // Copiar contenido del URI al archivo temporal
        contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        
        // Verificar que el archivo se creó correctamente
        if (tempFile.exists() && tempFile.length() > 0) {
            // Subir archivo usando el archivo temporal con el nombre original
            viewModel.uploadFile(tempFile, fileName)
            
            // Programar limpieza del archivo temporal después de 30 segundos
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                try {
                    if (tempFile.exists()) {
                        tempFile.delete()
                    }
                } catch (e: Exception) {
                    android.util.Log.w("FileSelection", "Error limpiando archivo temporal: ${e.message}")
                }
            }, 30000) // 30 segundos
            
        } else {
            android.util.Log.e("FileSelection", "Error: No se pudo crear archivo temporal")
        }
        
    } catch (e: Exception) {
        android.util.Log.e("FileSelection", "Error procesando archivo: ${e.message}", e)
        // Mostrar error genérico en el ViewModel
        viewModel.clearFileError()
    }
}