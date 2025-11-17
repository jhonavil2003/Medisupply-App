package com.misw.medisupply.presentation.salesforce.screens.routes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.domain.model.route.RouteStatus
import com.misw.medisupply.presentation.salesforce.screens.routes.components.RouteCard
import com.misw.medisupply.presentation.salesforce.screens.routes.viewmodel.RouteListViewModel
import com.misw.medisupply.ui.theme.ColorTextSecondary
import com.misw.medisupply.ui.theme.ColorTextPrimary
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteListScreen(
    onNavigateToGenerate: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RouteListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    
    var showFiltersDialog by remember { mutableStateOf(false) }
    
    // Load more cuando llega al final
    LaunchedEffect(listState.canScrollForward) {
        if (!listState.canScrollForward && uiState.canLoadMore) {
            viewModel.loadMore()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rutas de Visita") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Filtros
                    IconButton(onClick = { showFiltersDialog = true }) {
                        Badge(
                            containerColor = if (uiState.hasActiveFilters) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filtros")
                        }
                    }
                    
                    // Refresh
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToGenerate,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nueva Ruta") }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    
                    uiState.error != null -> {
                        ErrorState(
                            message = uiState.error ?: "Error desconocido",
                            onRetry = { viewModel.loadRoutes() },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    
                    uiState.filteredRoutes.isEmpty() -> {
                        EmptyState(
                            hasFilters = uiState.hasActiveFilters,
                            onClearFilters = { viewModel.clearFilters() },
                            onCreateNew = onNavigateToGenerate,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    
                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Filtros activos
                            if (uiState.hasActiveFilters) {
                                item {
                                    ActiveFiltersChip(
                                        dateFilter = uiState.selectedDate,
                                        statusFilter = uiState.selectedStatus,
                                        onClearFilters = { viewModel.clearFilters() }
                                    )
                                }
                            }
                            
                            // Estadísticas
                            item {
                                RouteStatisticsCard(
                                    totalRoutes = uiState.totalRoutes,
                                    displayedRoutes = uiState.filteredRoutes.size
                                )
                            }
                            
                            // Lista de rutas
                            items(
                                items = uiState.filteredRoutes,
                                key = { it.id }
                            ) { route ->
                                RouteCard(
                                    route = route,
                                    onClick = { onNavigateToDetail(route.id) }
                                )
                            }
                            
                            // Loading más
                            if (uiState.isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                            
                            // Espaciado para FAB
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Dialogs
    if (showFiltersDialog) {
        FiltersDialog(
            selectedDate = uiState.selectedDate,
            selectedStatus = uiState.selectedStatus,
            onDateSelected = { viewModel.updateDateFilter(it) },
            onStatusSelected = { viewModel.updateStatusFilter(it) },
            onClearFilters = { viewModel.clearFilters() },
            onDismiss = { showFiltersDialog = false }
        )
    }
    
    // Error snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            viewModel.clearError()
        }
    }
}

@Composable
private fun ActiveFiltersChip(
    dateFilter: LocalDate?,
    statusFilter: RouteStatus?,
    onClearFilters: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Filtros activos:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                if (dateFilter != null) {
                    Text(
                        text = "• Fecha: ${dateFilter.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                if (statusFilter != null) {
                    Text(
                        text = "• Estado: ${getStatusDisplayName(statusFilter)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            TextButton(onClick = onClearFilters) {
                Text("Limpiar")
            }
        }
    }
}

@Composable
private fun RouteStatisticsCard(
    totalRoutes: Int,
    displayedRoutes: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.Route,
                label = "Total de rutas",
                value = totalRoutes.toString()
            )
            
            if (displayedRoutes != totalRoutes) {
                StatItem(
                    icon = Icons.Default.FilterList,
                    label = "Mostrando",
                    value = displayedRoutes.toString()
                )
            }
        }
    }
}

@Composable
private fun StatItem(
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
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = ColorTextPrimary
            )
        }
    }
}

@Composable
private fun EmptyState(
    hasFilters: Boolean,
    onClearFilters: () -> Unit,
    onCreateNew: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (hasFilters) Icons.Default.FilterList else Icons.Default.Route,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = ColorTextSecondary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = if (hasFilters) {
                "No hay rutas con estos filtros"
            } else {
                "No hay rutas creadas"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (hasFilters) {
                "Intenta ajustar los filtros"
            } else {
                "Crea tu primera ruta optimizada"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = ColorTextSecondary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (hasFilters) {
            OutlinedButton(onClick = onClearFilters) {
                Icon(Icons.Default.Clear, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Limpiar filtros")
            }
        } else {
            Button(onClick = onCreateNew) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear ruta")
            }
        }
    }
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
            text = "Error al cargar rutas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = ColorTextSecondary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reintentar")
        }
    }
}

@Composable
private fun FiltersDialog(
    selectedDate: LocalDate?,
    selectedStatus: RouteStatus?,
    onDateSelected: (LocalDate?) -> Unit,
    onStatusSelected: (RouteStatus?) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtros de Rutas") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Filtro de fecha
                Column {
                    Text(
                        text = "Fecha",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Opciones de fecha rápidas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedDate == LocalDate.now(),
                            onClick = { onDateSelected(LocalDate.now()) },
                            label = { Text("Hoy") }
                        )
                        FilterChip(
                            selected = selectedDate == null,
                            onClick = { onDateSelected(null) },
                            label = { Text("Todas") }
                        )
                    }
                }
                
                Divider()
                
                // Filtro de estado
                Column {
                    Text(
                        text = "Estado",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(null to "Todos") + RouteStatus.values().map { 
                            it to getStatusDisplayName(it) 
                        }.forEach { (status, name) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedStatus == status,
                                    onClick = { onStatusSelected(status) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = name)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onClearFilters()
                onDismiss()
            }) {
                Text("Limpiar")
            }
        }
    )
}

private fun getStatusDisplayName(status: RouteStatus): String {
    return when (status) {
        RouteStatus.DRAFT -> "Borrador"
        RouteStatus.CONFIRMED -> "Confirmada"
        RouteStatus.IN_PROGRESS -> "En Curso"
        RouteStatus.COMPLETED -> "Completada"
        RouteStatus.CANCELLED -> "Cancelada"
    }
}
