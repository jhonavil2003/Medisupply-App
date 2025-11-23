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
import com.misw.medisupply.R
import com.misw.medisupply.presentation.components.localizedStringResource
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
    viewModel: CreateVisitViewModel,
    localeManager: com.misw.medisupply.core.i18n.LocaleManager
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ================================
        // SECCIÓN DE VIDEO S3
        // ================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(16.dp)) {
                com.misw.medisupply.presentation.common.video.VideoUploadSection(
                    videoUrl = uiState.videoUrl,
                    isUploading = uiState.isUploadingVideoToS3,
                    uploadProgress = uiState.videoUploadProgress,
                    error = uiState.videoUploadError,
                    onVideoSelected = { videoFile ->
                        viewModel.uploadVideoToS3(videoFile)
                    },
                    onClearVideo = {
                        viewModel.clearVideoState()
                    },
                    onClearError = {
                        viewModel.clearVideoError()
                    }
                )
            }
        }
        
        // ================================
        // SECCIÓN DE ANÁLISIS DE VIDEO CON IA
        // ================================
        if (uiState.videoUrl != null || uiState.isAnalyzingVideo || uiState.videoAnalysisResult != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp)) {
                    com.misw.medisupply.presentation.common.video.VideoAnalysisResultsSection(
                        analysisResult = uiState.videoAnalysisResult,
                        isAnalyzing = uiState.isAnalyzingVideo,
                        error = uiState.videoAnalysisError,
                        onDismissError = {
                            viewModel.clearVideoAnalysis()
                        }
                    )
                }
            }
        }
        
        // Card separado para completar visita - Solo visible cuando la visita está guardada
        if (uiState.isVisitSaved) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        localizedStringResource(R.string.finalize_visit_title, localeManager), 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    
                    // Mensaje de advertencia verde
                    Spacer(Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = localizedStringResource(R.string.finalize_visit_warning, localeManager),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Botón para completar visita
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
                            Text(localizedStringResource(R.string.complete_visit_button, localeManager))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingFilesCard(
    localeManager: com.misw.medisupply.core.i18n.LocaleManager
) {
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
                    text = localizedStringResource(R.string.loading_files, localeManager),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}

@Composable
private fun EmptyFilesCard(
    localeManager: com.misw.medisupply.core.i18n.LocaleManager
) {
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
                    text = localizedStringResource(R.string.no_files_attached, localeManager),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF757575)
                )
                Text(
                    text = localizedStringResource(R.string.use_button_add_files, localeManager),
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