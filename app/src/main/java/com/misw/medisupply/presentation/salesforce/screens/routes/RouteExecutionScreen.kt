package com.misw.medisupply.presentation.salesforce.screens.routes

import android.content.Intent
import android.net.Uri
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.presentation.salesforce.screens.routes.components.RouteExecutionMapCard
import com.misw.medisupply.presentation.salesforce.screens.routes.components.RouteMetricsCard
import com.misw.medisupply.presentation.salesforce.screens.routes.components.StopItem
import com.misw.medisupply.presentation.salesforce.screens.routes.viewmodel.RouteExecutionViewModel
import com.misw.medisupply.ui.theme.ColorSuccess
import com.misw.medisupply.ui.theme.ColorWarning
import com.misw.medisupply.ui.theme.ColorTextSecondary
import com.misw.medisupply.ui.theme.ColorTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteExecutionScreen(
    routeId: Int,
    onNavigateBack: () -> Unit,
    onRouteCompleted: () -> Unit,
    onNavigateToCreateVisit: (customerId: Int) -> Unit = {},
    viewModel: RouteExecutionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(routeId) {
        viewModel.loadRoute(routeId)
        viewModel.toggleGpsTracking(true)
    }
    
    // Manejar navegación a parada con app externa (Google Maps)
    LaunchedEffect(uiState.navigationStopId) {
        uiState.navigationStopId?.let { stopId ->
            uiState.route?.stops?.find { it.id == stopId }?.let { stop ->
                try {
                    // Crear URI para Google Maps con las coordenadas de la parada
                    val uri = Uri.parse(
                        "google.navigation:q=${stop.latitude},${stop.longitude}&mode=d"
                    )
                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    
                    // Intentar abrir Google Maps
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                        snackbarHostState.showSnackbar("Abriendo navegación a ${stop.customerName}")
                    } else {
                        // Si Google Maps no está instalado, abrir en el navegador
                        val browserUri = Uri.parse(
                            "https://www.google.com/maps/dir/?api=1&destination=${stop.latitude},${stop.longitude}&travelmode=driving"
                        )
                        context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
                        snackbarHostState.showSnackbar("Abriendo Google Maps en navegador")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("RouteExecution", "Error al abrir navegación", e)
                    snackbarHostState.showSnackbar("Error al abrir navegación: ${e.message}")
                }
                
                // Limpiar el estado de navegación
                viewModel.clearNavigation()
            }
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            viewModel.toggleGpsTracking(false)
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Ejecución de Ruta #$routeId")
                        uiState.route?.let { route ->
                            Text(
                                text = "Progreso: ${uiState.completionPercentage}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = ColorTextSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // GPS indicator
                    Icon(
                        imageVector = if (uiState.isLocationAvailable) Icons.Default.GpsFixed else Icons.Default.GpsNotFixed,
                        contentDescription = "GPS",
                        tint = if (uiState.isLocationAvailable) ColorSuccess else MaterialTheme.colorScheme.error
                    )
                }
            )
        },
        bottomBar = {
            uiState.route?.let { route ->
                ExecutionBottomBar(
                    canCompleteRoute = uiState.canCompleteRoute,
                    onCompleteRoute = { viewModel.showCompleteRouteDialog(true) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.error != null -> {
                    ErrorState(
                        message = uiState.error ?: "Error desconocido",
                        onRetry = { viewModel.refresh() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.route != null -> {
                    ExecutionContent(
                        route = uiState.route!!,
                        currentLocation = uiState.currentLocation,
                        nextPendingStop = uiState.nextPendingStop,
                        distanceToNext = uiState.distanceToNextStop,
                        onStopNavigate = { stopId ->
                            viewModel.navigateToStop(stopId)
                        },
                        onStopComplete = { stopId ->
                            viewModel.showCompleteStopDialog(true, stopId)
                        },
                        onStopSkip = { stopId ->
                            viewModel.showSkipStopDialog(true, stopId)
                        }
                    )
                }
            }
        }
    }
    
    // Complete stop dialog
    if (uiState.showCompleteStopDialog && uiState.selectedStopId != null) {
        val stopName = uiState.route?.stops?.find { it.id == uiState.selectedStopId }?.customerName ?: ""
        val customerId = uiState.route?.stops?.find { it.id == uiState.selectedStopId }?.customerId
        
        CompleteStopDialog(
            stopName = stopName,
            notes = uiState.stopNotes,
            onNotesChange = { viewModel.updateStopNotes(it) },
            onConfirm = {
                uiState.selectedStopId?.let { stopId ->
                    viewModel.completeStop(
                        stopId = stopId,
                        notes = uiState.stopNotes.takeIf { it.isNotBlank() }
                    )
                }
            },
            onDismiss = {
                viewModel.showCompleteStopDialog(false, null)
            },
            onRegisterVisit = {
                // Cerrar el diálogo y navegar a registro de visita
                viewModel.showCompleteStopDialog(false, null)
                customerId?.let { onNavigateToCreateVisit(it) }
            },
            isLoading = uiState.isCompletingStop
        )
    }
    
    // Skip stop dialog
    if (uiState.showSkipStopDialog && uiState.selectedStopId != null) {
        val stopName = uiState.route?.stops?.find { it.id == uiState.selectedStopId }?.customerName ?: ""
        SkipStopDialog(
            stopName = stopName,
            reason = uiState.skipReason,
            onReasonChange = { viewModel.updateSkipReason(it) },
            onConfirm = {
                uiState.selectedStopId?.let { stopId ->
                    viewModel.skipStop(
                        stopId = stopId,
                        reason = uiState.skipReason
                    )
                }
            },
            onDismiss = { viewModel.showSkipStopDialog(false) },
            isLoading = uiState.isSkippingStop
        )
    }
    
    // Complete route dialog
    if (uiState.showCompleteRouteDialog) {
        ConfirmCompleteRouteDialog(
            completedStops = uiState.route?.stops?.count { it.completedAt != null } ?: 0,
            totalStops = uiState.route?.stops?.size ?: 0,
            onConfirm = {
                viewModel.completeRoute {
                    onRouteCompleted()
                }
            },
            onDismiss = { viewModel.showCompleteRouteDialog(false) },
            isLoading = uiState.isCompletingRoute
        )
    }
    
    // Success message
    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            viewModel.clearMessages()
        }
    }
}

@Composable
private fun ExecutionContent(
    route: com.misw.medisupply.domain.model.route.Route,
    currentLocation: com.misw.medisupply.domain.model.route.Location?,
    nextPendingStop: com.misw.medisupply.domain.model.route.RouteStop?,
    distanceToNext: Double?,
    onStopNavigate: (Int) -> Unit,
    onStopComplete: (Int) -> Unit,
    onStopSkip: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mapa en tiempo real con Google Maps
        item {
            RouteExecutionMapCard(
                route = route,
                currentLocation = currentLocation,
                onStopClick = { stop ->
                    // Expandir detalles de la parada al hacer clic en el mapa
                }
            )
        }
        
        // Siguiente parada
        nextPendingStop?.let { stop ->
            item {
                NextStopCard(
                    stop = stop,
                    distance = distanceToNext,
                    onNavigate = { onStopNavigate(stop.id) },
                    onComplete = { onStopComplete(stop.id) },
                    onSkip = { onStopSkip(stop.id) }
                )
            }
        }
        
        // Métricas
        item {
            RouteMetricsCard(metrics = route.metrics)
        }
        
        // Progreso
        item {
            ProgressCard(route = route)
        }
        
        // Lista de paradas
        item {
            Text(
                text = "Todas las Paradas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        items(
            items = route.stops.sortedBy { it.sequenceOrder },
            key = { it.id }
        ) { stop ->
            StopItem(
                stop = stop,
                stopNumber = stop.sequenceOrder,
                isExpanded = false,
                onExpandToggle = { },
                onNavigateClick = if (stop.completedAt == null && stop.skippedAt == null) {
                    { onStopNavigate(stop.id) }
                } else null,
                onCompleteClick = if (stop.completedAt == null && stop.skippedAt == null) {
                    { onStopComplete(stop.id) }
                } else null,
                onSkipClick = if (stop.completedAt == null && stop.skippedAt == null) {
                    { onStopSkip(stop.id) }
                } else null
            )
        }
        
        // Espaciado para bottom bar
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun NextStopCard(
    stop: com.misw.medisupply.domain.model.route.RouteStop,
    distance: Double?,
    onNavigate: () -> Unit,
    onComplete: () -> Unit,
    onSkip: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Próxima Parada",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "#${stop.sequenceOrder}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = stop.customerName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = stop.address,
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextSecondary
            )
            
            if (distance != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (distance < 1000) {
                            "A ${distance.toInt()}m de distancia"
                        } else {
                            "A ${String.format("%.1f", distance / 1000)}km de distancia"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onNavigate,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
                ) {
                    Icon(
                        Icons.Default.Navigation,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Navegar",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                
                Button(
                    onClick = onComplete,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorSuccess
                    )
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Completar",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                
                OutlinedButton(
                    onClick = onSkip,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Omitir",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(
    route: com.misw.medisupply.domain.model.route.Route
) {
    val completed = route.stops.count { it.completedAt != null }
    val skipped = route.stops.count { it.skippedAt != null }
    val pending = route.stops.size - completed - skipped
    val progress = if (route.stops.isNotEmpty()) completed.toFloat() / route.stops.size else 0f
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Progreso de Ejecución",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$completed de ${route.stops.size} completadas",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProgressStat(
                    label = "Completadas",
                    value = completed.toString(),
                    color = ColorSuccess
                )
                ProgressStat(
                    label = "Omitidas",
                    value = skipped.toString(),
                    color = ColorWarning
                )
                ProgressStat(
                    label = "Pendientes",
                    value = pending.toString(),
                    color = ColorTextPrimary
                )
            }
        }
    }
}

@Composable
private fun ProgressStat(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ColorTextSecondary
        )
    }
}

@Composable
private fun ExecutionBottomBar(
    canCompleteRoute: Boolean,
    onCompleteRoute: () -> Unit
) {
    Surface(
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (canCompleteRoute) {
                Button(
                    onClick = onCompleteRoute,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorSuccess
                    )
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Completar Ruta")
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Completa todas las paradas para finalizar la ruta",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CompleteStopDialog(
    stopName: String,
    notes: String,
    onNotesChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onRegisterVisit: () -> Unit = {},
    isLoading: Boolean
) {
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Completar Parada") },
        text = {
            Column {
                Text("¿Confirmas que completaste la visita a $stopName?")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    label = { Text("Notas (opcional)") },
                    placeholder = { Text("Observaciones de la visita...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    enabled = !isLoading
                )
            }
        },
        confirmButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Fila superior: Cancelar y Registrar
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    
                    Button(
                        onClick = onRegisterVisit,
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Registrar")
                    }
                }
                
                // Fila inferior: Completar (ancho completo)
                Button(
                    onClick = onConfirm,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorSuccess
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Completar")
                    }
                }
            }
        },
        dismissButton = null
    )
}

@Composable
private fun SkipStopDialog(
    stopName: String,
    reason: String,
    onReasonChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean
) {
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Omitir Parada") },
        text = {
            Column {
                Text("¿Por qué omites la visita a $stopName?")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = onReasonChange,
                    label = { Text("Razón *") },
                    placeholder = { Text("Cliente cerrado, no disponible, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    enabled = !isLoading,
                    isError = reason.isBlank(),
                    supportingText = if (reason.isBlank()) {
                        { Text("La razón es obligatoria") }
                    } else null
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading && reason.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Omitir")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ConfirmCompleteRouteDialog(
    completedStops: Int,
    totalStops: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean
) {
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Completar Ruta") },
        text = {
            Column {
                Text("¿Confirmas que has finalizado la ejecución de la ruta?")
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Resumen:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• $completedStops paradas completadas")
                        Text("• ${totalStops - completedStops} paradas omitidas")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorSuccess
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Completar Ruta")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Error",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reintentar")
        }
    }
}
