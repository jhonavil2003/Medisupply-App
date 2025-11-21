package com.misw.medisupply.presentation.salesforce.screens.routes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.misw.medisupply.R
import com.misw.medisupply.core.i18n.LocaleManager
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.route.Route
import com.misw.medisupply.domain.model.route.RouteMetrics
import com.misw.medisupply.domain.model.route.RouteStatus
import com.misw.medisupply.domain.model.route.RouteStop
import com.misw.medisupply.presentation.components.localizedStringResource
import com.misw.medisupply.ui.theme.ColorPrimaryDark
import com.misw.medisupply.ui.theme.ColorSuccess
import com.misw.medisupply.ui.theme.ColorWarning
import com.misw.medisupply.ui.theme.ButtonDangerBg
import com.misw.medisupply.ui.theme.ColorTextPrimary
import com.misw.medisupply.ui.theme.ColorTextSecondary
import java.time.format.DateTimeFormatter

/**
 * Item de cliente con checkbox para selección
 */
@Composable
fun CustomerCheckboxItem(
    customer: Customer,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    localeManager: com.misw.medisupply.core.i18n.LocaleManager,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelectionChanged(!isSelected) },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                Color(0xFFD6E3FF) // Color personalizado para selección
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChanged
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = customer.getDisplayName(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = customer.address ?: localeManager.getLocalizedString(com.misw.medisupply.R.string.route_customer_no_address),
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (customer.latitude != null && customer.longitude != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = localeManager.getLocalizedString(com.misw.medisupply.R.string.route_customer_gps_coordinates) + ": ${customer.latitude}, ${customer.longitude}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card de ruta en lista
 */
@Composable
fun RouteCard(
    route: Route,
    localeManager: LocaleManager,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header con estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ruta #${route.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = route.salespersonName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                RouteStatusChip(
                    status = route.status,
                    localeManager = localeManager
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Fecha
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = route.plannedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Métricas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RouteMetricItem(
                    icon = Icons.Default.Store,
                    label = "Paradas",
                    value = "${route.stops.size}"
                )
                
                RouteMetricItem(
                    icon = Icons.Default.Route,
                    label = "Distancia",
                    value = String.format("%.1f km", route.metrics.totalDistanceKm)
                )
                
                RouteMetricItem(
                    icon = Icons.Default.AccessTime,
                    label = "Tiempo",
                    value = "${route.metrics.estimatedDurationMinutes} min"
                )
            }
            
            // Progreso si está en curso
            if (route.status == RouteStatus.IN_PROGRESS) {
                Spacer(modifier = Modifier.height(12.dp))
                
                val completed = route.stops.count { it.completedAt != null }
                val total = route.stops.size
                val progress = if (total > 0) completed.toFloat() / total else 0f
                
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progreso",
                            style = MaterialTheme.typography.labelMedium,
                            color = ColorTextSecondary
                        )
                        Text(
                            text = "$completed / $total paradas",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Chip de estado de ruta
 */
@Composable
fun RouteStatusChip(
    status: RouteStatus,
    localeManager: LocaleManager,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, stringRes) = when (status) {
        RouteStatus.DRAFT -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            ColorTextPrimary,
            R.string.route_status_draft
        )
        RouteStatus.CONFIRMED -> Triple(
            ColorPrimaryDark.copy(alpha = 0.2f),
            ColorPrimaryDark,
            R.string.route_status_confirmed
        )
        RouteStatus.IN_PROGRESS -> Triple(
            ColorWarning.copy(alpha = 0.2f),
            ColorWarning,
            R.string.route_status_in_progress
        )
        RouteStatus.COMPLETED -> Triple(
            ColorSuccess.copy(alpha = 0.2f),
            ColorSuccess,
            R.string.route_status_completed
        )
        RouteStatus.CANCELLED -> Triple(
            ButtonDangerBg.copy(alpha = 0.2f),
            ButtonDangerBg,
            R.string.route_status_cancelled
        )
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = localizedStringResource(stringRes, localeManager),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

/**
 * Item de métrica de ruta
 */
@Composable
fun RouteMetricItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = ColorTextSecondary
            )
        }
    }
}

/**
 * Card de parada en ruta
 */
@Composable
fun StopItem(
    stop: RouteStop,
    stopNumber: Int,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onNavigateClick: (() -> Unit)? = null,
    onCompleteClick: (() -> Unit)? = null,
    onSkipClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onExpandToggle)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Número de parada
                Surface(
                    shape = CircleShape,
                    color = when {
                        stop.completedAt != null -> ColorSuccess
                        stop.skippedAt != null -> ColorWarning
                        else -> MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stopNumber.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Info cliente
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stop.customerName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = stop.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Estado
                    when {
                        stop.completedAt != null -> {
                            Text(
                                text = "✓ Completada",
                                style = MaterialTheme.typography.labelSmall,
                                color = ColorSuccess,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        stop.skippedAt != null -> {
                            Text(
                                text = "⊘ Omitida",
                                style = MaterialTheme.typography.labelSmall,
                                color = ColorWarning,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Contraer" else "Expandir"
                )
            }
            
            // Detalles expandidos
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                
                // Métricas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Distancia",
                            style = MaterialTheme.typography.labelSmall,
                            color = ColorTextSecondary
                        )
                        Text(
                            text = String.format("%.2f km", stop.distanceFromPreviousKm),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Tiempo",
                            style = MaterialTheme.typography.labelSmall,
                            color = ColorTextSecondary
                        )
                        Text(
                            text = "${stop.serviceMinutes} min",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    if (stop.estimatedArrival != null) {
                        Column {
                            Text(
                                text = "Llegada Est.",
                                style = MaterialTheme.typography.labelSmall,
                                color = ColorTextSecondary
                            )
                            Text(
                                text = stop.estimatedArrival.format(DateTimeFormatter.ofPattern("HH:mm")),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                
                // Notas si fue omitida
                if (stop.skipReason != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFFF9800).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Razón de omisión:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFF9800)
                            )
                            Text(
                                text = stop.skipReason,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                
                // Notas si fue completada
                if (stop.notes != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Notas:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = stop.notes,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                
                // Acciones si está pendiente
                if (stop.completedAt == null && stop.skippedAt == null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (onNavigateClick != null) {
                            OutlinedButton(
                                onClick = onNavigateClick,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Navigation,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Navegar")
                            }
                        }
                        
                        if (onCompleteClick != null) {
                            FilledTonalButton(
                                onClick = onCompleteClick,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Completar")
                            }
                        }
                        
                        if (onSkipClick != null) {
                            OutlinedButton(
                                onClick = onSkipClick,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Omitir")
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card de métricas de ruta
 */
@Composable
fun RouteMetricsCard(
    metrics: RouteMetrics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Métricas de Ruta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricBox(
                    label = "Distancia Total",
                    value = String.format("%.1f km", metrics.totalDistanceKm),
                    icon = Icons.Default.Route
                )
                
                MetricBox(
                    label = "Duración Est.",
                    value = "${metrics.estimatedDurationMinutes} min",
                    icon = Icons.Default.AccessTime
                )
                
                MetricBox(
                    label = "Paradas",
                    value = "${metrics.totalStops}",
                    icon = Icons.Default.Store
                )
            }
            
            if (metrics.completedStops > 0 || metrics.skippedStops > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricBox(
                        label = "Completadas",
                        value = "${metrics.completedStops}",
                        icon = Icons.Default.CheckCircle
                    )
                    
                    MetricBox(
                        label = "Omitidas",
                        value = "${metrics.skippedStops}",
                        icon = Icons.Default.SkipNext
                    )
                    
                    MetricBox(
                        label = "Pendientes",
                        value = "${metrics.pendingStops}",
                        icon = Icons.Default.PendingActions
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}
