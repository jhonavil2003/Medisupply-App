package com.misw.medisupply.presentation.common.video

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File

/**
 * Componente para seleccionar video y subirlo a S3
 */
@Composable
fun VideoUploadSection(
    videoUrl: String?,
    isUploading: Boolean,
    uploadProgress: Float,
    error: String?,
    onVideoSelected: (File) -> Unit,
    onClearVideo: () -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedVideoFile by remember { mutableStateOf<File?>(null) }
    
    // Launcher para seleccionar video de galería
    val selectVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val videoFile = VideoFileHelper.uriToFile(context, it)
            if (videoFile != null) {
                selectedVideoFile = videoFile
                showConfirmDialog = true
            }
        }
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Título
        Text(
            text = "📹 Video de la Visita",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        
        // Información
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Selecciona un video de la visita al cliente",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1565C0)
                )
                Text(
                    text = "Graba el video con tu app de cámara y luego selecciónalo aquí",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF424242)
                )
                Text(
                    text = "• Formatos: MP4, AVI, MOV, MKV, 3GP",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF616161)
                )
                Text(
                    text = "• Tamaño máximo: 50 MB",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF616161)
                )
            }
        }
        
        // Botón para seleccionar video
        if (videoUrl == null && !isUploading) {
            Button(
                onClick = {
                    selectVideoLauncher.launch("video/*")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.VideoLibrary, contentDescription = "Seleccionar")
                Spacer(Modifier.width(8.dp))
                Text("Seleccionar Video")
            }
        }
        
        // Progreso de subida
        if (isUploading) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Subiendo video a S3...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (uploadProgress > 0) {
                        LinearProgressIndicator(
                            progress = uploadProgress / 100f,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "${uploadProgress.toInt()}%",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
        
        // Video subido exitosamente
        if (videoUrl != null && !isUploading) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E8)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Éxito",
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "✅ Video subido a S3",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                text = "URL: ${videoUrl.takeLast(40)}...",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF616161)
                            )
                        }
                    }
                    
                    IconButton(onClick = onClearVideo) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color(0xFF757575)
                        )
                    }
                }
            }
        }
        
        // Error
        error?.let { errorMessage ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    
                    IconButton(onClick = onClearError) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
    
    // Modal de confirmación
    if (showConfirmDialog && selectedVideoFile != null) {
        AlertDialog(
            onDismissRequest = {
                showConfirmDialog = false
                selectedVideoFile = null
            },
            icon = {
                Icon(
                    Icons.Default.CloudUpload,
                    contentDescription = "Subir",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text("Confirmar Subida de Video")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("¿Deseas subir este video?")
                    
                    selectedVideoFile?.let { file ->
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Archivo: ${file.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF616161)
                        )
                        Text(
                            text = "Tamaño: ${"%.2f".format(file.length() / (1024f * 1024f))} MB",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF616161)
                        )
                    }
                    
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "⚠️ El video se subirá inmediatamente a AWS S3",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE65100)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedVideoFile?.let { onVideoSelected(it) }
                        showConfirmDialog = false
                        selectedVideoFile = null
                    }
                ) {
                    Text("Subir Video")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        selectedVideoFile = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

