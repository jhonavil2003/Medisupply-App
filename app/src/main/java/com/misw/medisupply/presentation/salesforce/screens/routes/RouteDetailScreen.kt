package com.misw.medisupply.presentation.salesforce.screens.routes

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.R
import com.misw.medisupply.core.i18n.LocaleManager
import com.misw.medisupply.core.i18n.localizedStringResource
import javax.inject.Inject
import com.misw.medisupply.domain.model.route.RouteStatus
import com.misw.medisupply.presentation.salesforce.screens.routes.components.*
import com.misw.medisupply.presentation.salesforce.screens.routes.viewmodel.RouteDetailViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    routeId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToExecution: (Int) -> Unit,
    localeManager: LocaleManager,
    viewModel: RouteDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(routeId) {
        viewModel.loadRoute(routeId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = localizedStringResource(R.string.route_detail_title, localeManager, routeId),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                        Text(
                            text = "Gestión de Rutas",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1565C0).copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = localizedStringResource(R.string.route_detail_back, localeManager),
                            tint = Color(0xFF1565C0)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = localizedStringResource(R.string.route_detail_refresh, localeManager),
                            tint = Color(0xFF1565C0)
                        )
                    }
                    
                    if (uiState.route?.status == RouteStatus.DRAFT) {
                        IconButton(onClick = { viewModel.showCancelDialog(true) }) {
                            Icon(
                                Icons.Default.Cancel,
                                contentDescription = localizedStringResource(R.string.route_detail_cancel_route, localeManager),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFDAE5FF)
                )
            )
        },
        bottomBar = {
            uiState.route?.let { route ->
                RouteActions(
                    status = route.status,
                    onConfirm = { viewModel.showConfirmDialog(true) },
                    onStart = { viewModel.showStartDialog(true) },
                    onExecute = { onNavigateToExecution(route.id) },
                    onComplete = { viewModel.showCompleteDialog(true) },
                    isConfirming = uiState.isConfirming,
                    isStarting = uiState.isStarting,
                    isCompleting = uiState.isCompleting
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
                    ErrorMessage(
                        message = uiState.error ?: localizedStringResource(R.string.route_detail_error_loading, localeManager),
                        onRetry = { viewModel.refresh() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.route != null -> {
                    RouteDetailContent(
                        route = uiState.route!!,
                        expandedStopIds = uiState.expandedStopIds,
                        onStopClick = { stopId -> viewModel.toggleStopExpansion(stopId) },
                        onNavigateToStop = { stopId ->
                            viewModel.selectStop(stopId)
                            // Aquí se podría abrir Google Maps
                        },
                        localeManager = localeManager
                    )
                }
            }
        }
    }
    
    // Confirm dialog
    if (uiState.showConfirmDialog) {
        ConfirmActionDialog(
            title = localizedStringResource(R.string.route_dialog_confirm_title, localeManager),
            message = localizedStringResource(R.string.route_dialog_confirm_message, localeManager),
            confirmText = localizedStringResource(R.string.route_action_confirm, localeManager),
            confirmColor = Color(0xFF4CAF50),
            onConfirm = {
                viewModel.confirmRoute {
                    // Success callback
                }
            },
            onDismiss = { viewModel.showConfirmDialog(false) },
            isLoading = uiState.isConfirming
        )
    }
    
    // Start dialog
    if (uiState.showStartDialog) {
        ConfirmActionDialog(
            title = localizedStringResource(R.string.route_dialog_start_title, localeManager),
            message = localizedStringResource(R.string.route_dialog_start_message, localeManager),
            confirmText = localizedStringResource(R.string.route_action_start, localeManager),
            confirmColor = Color(0xFF4CAF50),
            onConfirm = {
                viewModel.startRoute { routeId ->
                    onNavigateToExecution(routeId)
                }
            },
            onDismiss = { viewModel.showStartDialog(false) },
            isLoading = uiState.isStarting
        )
    }
    
    // Complete dialog
    if (uiState.showCompleteDialog) {
        ConfirmActionDialog(
            title = localizedStringResource(R.string.route_dialog_complete_title, localeManager),
            message = localizedStringResource(R.string.route_dialog_complete_message, localeManager),
            confirmText = localizedStringResource(R.string.route_action_complete, localeManager),
            onConfirm = {
                viewModel.completeRoute {
                    // Success callback
                }
            },
            onDismiss = { viewModel.showCompleteDialog(false) },
            isLoading = uiState.isCompleting
        )
    }
    
    // Cancel dialog
    if (uiState.showCancelDialog) {
        ConfirmActionDialog(
            title = localizedStringResource(R.string.route_dialog_cancel_title, localeManager),
            message = localizedStringResource(R.string.route_dialog_cancel_message, localeManager),
            confirmText = localizedStringResource(R.string.route_dialog_cancel_action, localeManager),
            confirmColor = MaterialTheme.colorScheme.error,
            onConfirm = {
                viewModel.cancelRoute {
                    onNavigateBack()
                }
            },
            onDismiss = { viewModel.showCancelDialog(false) },
            isLoading = uiState.isCancelling
        )
    }
    
    // Success message
    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            // Mostrar snackbar
            viewModel.clearMessages()
        }
    }
}

@Composable
private fun RouteDetailContent(
    route: com.misw.medisupply.domain.model.route.Route,
    expandedStopIds: Set<Int>,
    onStopClick: (Int) -> Unit,
    onNavigateToStop: (Int) -> Unit,
    localeManager: LocaleManager
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header con info básica
        item {
            RouteHeaderCard(route = route, localeManager = localeManager)
        }
        
        // Métricas header
        item {
            Text(
                text = "Métricas de Ruta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )
        }
        
        // Métricas
        item {
            RouteMetricsCard(metrics = route.metrics)
        }
        
        // Mapa con Google Maps
        item {
            RouteMapCard(
                route = route,
                onStopClick = { stop ->
                    onStopClick(stop.id)
                }
            )
        }
        
        // Paradas header
        item {
            Text(
                text = localizedStringResource(R.string.route_info_stops, localeManager, route.stops.size),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )
        }
        
        // Lista de paradas
        items(
            items = route.stops.sortedBy { it.sequenceOrder },
            key = { it.id }
        ) { stop ->
            StopItem(
                stop = stop,
                stopNumber = stop.sequenceOrder,
                isExpanded = stop.id in expandedStopIds,
                onExpandToggle = { onStopClick(stop.id) },
                onNavigateClick = { onNavigateToStop(stop.id) },
                onCompleteClick = null, // Solo en modo ejecución
                onSkipClick = null // Solo en modo ejecución
            )
        }
    }
}

@Composable
private fun RouteHeaderCard(
    route: com.misw.medisupply.domain.model.route.Route,
    localeManager: LocaleManager
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = localizedStringResource(R.string.route_info_route_number, localeManager, route.id),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = route.salespersonName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                RouteStatusChip(status = route.status, localeManager = localeManager)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            
            // Información de fechas
            InfoRow(
                icon = Icons.Default.CalendarToday,
                label = localizedStringResource(R.string.route_info_planned_date, localeManager),
                value = route.plannedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            )
            
            if (route.startedAt != null) {
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    icon = Icons.Default.PlayArrow,
                    label = localizedStringResource(R.string.route_info_started, localeManager),
                    value = route.startedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                )
            }
            
            if (route.completedAt != null) {
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    icon = Icons.Default.CheckCircle,
                    label = "Completada",
                    value = route.completedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                )
            }
            
            // Horario de trabajo
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(
                icon = Icons.Default.AccessTime,
                label = "Horario",
                value = "${route.workHours.start.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${route.workHours.end.format(DateTimeFormatter.ofPattern("HH:mm"))}"
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun RouteActions(
    status: RouteStatus,
    onConfirm: () -> Unit,
    onStart: () -> Unit,
    onExecute: () -> Unit,
    onComplete: () -> Unit,
    isConfirming: Boolean,
    isStarting: Boolean,
    isCompleting: Boolean
) {
    Surface(
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (status) {
                RouteStatus.DRAFT -> {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        enabled = !isConfirming,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        if (isConfirming) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Confirmar Ruta")
                        }
                    }
                }
                
                RouteStatus.CONFIRMED -> {
                    Button(
                        onClick = onStart,
                        modifier = Modifier.weight(1f),
                        enabled = !isStarting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        if (isStarting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Iniciar Ruta")
                        }
                    }
                }
                
                RouteStatus.IN_PROGRESS -> {
                    Button(
                        onClick = onExecute,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800)
                        )
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continuar Ejecución")
                    }
                    
                    Button(
                        onClick = onComplete,
                        modifier = Modifier.weight(1f),
                        enabled = !isCompleting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        if (isCompleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Completar")
                        }
                    }
                }
                
                RouteStatus.COMPLETED -> {
                    // Sin acciones
                }
                
                RouteStatus.CANCELLED -> {
                    // Sin acciones
                }
                
                else -> {
                    // Sin acciones para otros estados
                }
            }
        }
    }
}

@Composable
private fun ConfirmActionDialog(
    title: String,
    message: String,
    confirmText: String,
    confirmColor: Color = MaterialTheme.colorScheme.primary,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean = false
) {
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = confirmColor
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(confirmText)
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
private fun ErrorMessage(
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
