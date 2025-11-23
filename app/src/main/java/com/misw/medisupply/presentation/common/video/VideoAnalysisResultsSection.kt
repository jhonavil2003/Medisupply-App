package com.misw.medisupply.presentation.common.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.misw.medisupply.domain.model.video.VideoAnalysisResult

/**
 * Componente para mostrar los resultados del análisis de video con IA
 */
@Composable
fun VideoAnalysisResultsSection(
    analysisResult: VideoAnalysisResult?,
    isAnalyzing: Boolean,
    error: String?,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título
        Text(
            text = "🤖 Análisis de Video con IA",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        // Estado de análisis en progreso
        if (isAnalyzing) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Analizando video con IA...",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Esto puede tomar 10-30 segundos",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF616161)
                    )
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
                    
                    IconButton(onClick = onDismissError) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
        
        // Resultados del análisis
        analysisResult?.let { result ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Metadatos
                MetadataCard(result.metadata)
                
                // Productos detectados
                DetectedProductsCard(result.videoAnalysis.detectedProducts)
                
                // Marcas competidoras
                if (result.videoAnalysis.competitorBrands.isNotEmpty()) {
                    CompetitorBrandsCard(result.videoAnalysis.competitorBrands)
                }
                
                // Contexto
                ContextCard(result.videoAnalysis.context, result.videoAnalysis.userNeeds)
                
                // Oportunidades
                if (result.videoAnalysis.opportunities.isNotEmpty()) {
                    OpportunitiesCard(result.videoAnalysis.opportunities)
                }
                
                // Recomendaciones RAG
                result.recommendations?.let { recommendations ->
                    RecommendationsCard(recommendations)
                    
                    // Actionable Insights
                    if (recommendations.actionableInsights.isNotEmpty()) {
                        ActionableInsightsCard(recommendations.actionableInsights)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetadataCard(metadata: com.misw.medisupply.domain.model.video.AnalysisMetadata) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "📊 Información del Análisis",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )
            Text(
                text = "Modelo: ${metadata.geminiModel}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF424242)
            )
            Text(
                text = "Frames analizados: ${metadata.framesAnalyzed}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF424242)
            )
            Text(
                text = "Tiempo de procesamiento: ${"%.1f".format(metadata.processingTimeSeconds)}s",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF424242)
            )
        }
    }
}

@Composable
private fun DetectedProductsCard(products: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Category,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Productos Detectados (${products.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
            
            products.forEach { product ->
                Row(verticalAlignment = Alignment.Top) {
                    Text("•", color = Color(0xFF4CAF50), modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = product,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF424242)
                    )
                }
            }
        }
    }
}

@Composable
private fun CompetitorBrandsCard(brands: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Marcas Competidoras Detectadas",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
            }
            
            brands.forEach { brand ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFE0B2), RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚠️", modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = brand,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242)
                    )
                }
            }
        }
    }
}

@Composable
private fun ContextCard(context: String, userNeeds: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF9C27B0),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Contexto y Necesidades",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6A1B9A)
                )
            }
            
            Text(
                text = "Contexto:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242)
            )
            Text(
                text = context,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF616161)
            )
            
            Spacer(Modifier.height(4.dp))
            
            Text(
                text = "Necesidades del Cliente:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242)
            )
            Text(
                text = userNeeds,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF616161)
            )
        }
    }
}

@Composable
private fun OpportunitiesCard(opportunities: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE))
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = Color(0xFF0277BD),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Oportunidades de Negocio",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF01579B)
                )
            }
            
            opportunities.forEach { opportunity ->
                Row(verticalAlignment = Alignment.Top) {
                    Text("💡", modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = opportunity,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF424242)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendationsCard(recommendations: com.misw.medisupply.domain.model.video.Recommendations) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Recommend,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Productos Recomendados de MediSupply",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
            
            Text(
                text = "Confianza: ${(recommendations.confidenceScore * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF616161)
            )
            
            recommendations.products.forEach { product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFC8E6C9), RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✅", modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = product,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionableInsightsCard(insights: List<com.misw.medisupply.domain.model.video.ActionableInsight>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Acciones Recomendadas",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC62828)
                )
            }
            
            insights.forEach { insight ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "📌 ${insight.message}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242)
                        )
                        Text(
                            text = "→ Acción: ${insight.action}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1976D2)
                        )
                    }
                }
            }
        }
    }
}
