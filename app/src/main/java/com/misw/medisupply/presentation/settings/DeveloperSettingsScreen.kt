package com.misw.medisupply.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.misw.medisupply.BuildConfig
import com.misw.medisupply.core.config.ApiConfig

/**
 * Developer Settings Screen
 * Displays current API configuration and environment
 * Useful for debugging and verifying which environment is active
 */
@Composable
fun DeveloperSettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "⚙️ Configuración de Desarrollador",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Text(
            text = "Información sobre el entorno actual y configuración de APIs",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Environment Card
        EnvironmentCard()
        
        // App Info Card
        AppInfoCard()
        
        // API Endpoints Card
        ApiEndpointsCard()
        
        // Instructions Card
        InstructionsCard()
    }
}

@Composable
private fun EnvironmentCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (ApiConfig.isLocalEnvironment) {
                Color(0xFFE3F2FD) // Light blue for local
            } else {
                Color(0xFFE8F5E9) // Light green for AWS
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (ApiConfig.isLocalEnvironment) Icons.Default.Computer else Icons.Default.Cloud,
                contentDescription = "Environment",
                tint = if (ApiConfig.isLocalEnvironment) Color(0xFF1976D2) else Color(0xFF388E3C),
                modifier = Modifier.size(48.dp)
            )
            
            Column {
                Text(
                    text = "Entorno Actual",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black.copy(alpha = 0.6f)
                )
                Text(
                    text = ApiConfig.getEnvironmentDisplayName(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (ApiConfig.isLocalEnvironment) Color(0xFF1976D2) else Color(0xFF388E3C)
                )
            }
        }
    }
}

@Composable
private fun AppInfoCard() {
    InfoCard(
        title = "📱 Información de la App",
        items = listOf(
            "Nombre" to BuildConfig.APPLICATION_ID,
            "Versión" to "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            "Build Type" to BuildConfig.BUILD_TYPE,
            "Debug" to if (BuildConfig.DEBUG) "Sí" else "No"
        )
    )
}

@Composable
private fun ApiEndpointsCard() {
    InfoCard(
        title = "🌐 Endpoints de API",
        items = listOf(
            "Sales Service" to ApiConfig.salesServiceUrl,
            "Catalog Service" to ApiConfig.catalogServiceUrl,
            "Logistics Service" to ApiConfig.logisticsServiceUrl
        )
    )
}

@Composable
private fun InstructionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "¿Cómo cambiar de entorno?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = "Para cambiar entre desarrollo local y AWS, selecciona el Build Variant apropiado:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Column(
                modifier = Modifier.padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BulletPoint("Build → Select Build Variant")
                BulletPoint("debug: Local (10.0.2.2:800x) ✅")
                BulletPoint("release: AWS (solo producción)")
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "💡 Usa 'debug' siempre para desarrollo diario",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    items: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            items.forEach { (label, value) ->
                InfoRow(label = label, value = value)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
