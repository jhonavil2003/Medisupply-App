package com.misw.medisupply.presentation.salesforce.screens.routes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.R
import com.misw.medisupply.core.i18n.LocaleManager
import com.misw.medisupply.presentation.components.localizedStringResource
import com.misw.medisupply.domain.model.route.RouteStatus
import com.misw.medisupply.presentation.salesforce.screens.routes.components.RouteCard
import com.misw.medisupply.presentation.salesforce.screens.routes.viewmodel.RouteListViewModel
import com.misw.medisupply.presentation.salesforce.screens.routes.viewmodel.RouteListScreenViewModel
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
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
    viewModel: RouteListViewModel = hiltViewModel(),
    localeViewModel: RouteListScreenViewModel = hiltViewModel()
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
                title = {
                    Column {
                        Text(
                            text = localizedStringResource(R.string.route_list_title, localeViewModel.localeManager),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                        Text(
                            text = localizedStringResource(R.string.sales_force_subtitle, localeViewModel.localeManager),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1565C0).copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = localizedStringResource(R.string.route_list_go_back, localeViewModel.localeManager),
                            tint = Color(0xFF1565C0)
                        )
                    }
                },
                actions = {
                    // Filtros
                    IconButton(onClick = { showFiltersDialog = true }) {
                        Badge(
                            containerColor = if (uiState.hasActiveFilters) {
                                MaterialTheme.colorScheme.error
                            } else {
                                Color(0xFFDAE5FF)
                            }
                        ) {
                            Icon(
                                Icons.Default.FilterList, 
                                contentDescription = localizedStringResource(R.string.route_list_filters, localeViewModel.localeManager),
                                tint = Color(0xFF1565C0)
                            )
                        }
                    }
                    
                    // Refresh
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            Icons.Default.Refresh, 
                            contentDescription = localizedStringResource(R.string.route_list_refresh, localeViewModel.localeManager),
                            tint = Color(0xFF1565C0)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFDAE5FF)
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToGenerate,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(localizedStringResource(R.string.route_list_new_route, localeViewModel.localeManager)) },
                containerColor = Color(0xFF3C5BAA),
                contentColor = Color.White
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
                            message = uiState.error ?: localizedStringResource(R.string.route_list_unknown_error, localeViewModel.localeManager),
                            localeManager = localeViewModel.localeManager,
                            onRetry = { viewModel.loadRoutes() },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    
                    uiState.filteredRoutes.isEmpty() -> {
                        EmptyState(
                            hasFilters = uiState.hasActiveFilters,
                            localeManager = localeViewModel.localeManager,
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
                                        localeManager = localeViewModel.localeManager,
                                        onClearFilters = { viewModel.clearFilters() }
                                    )
                                }
                            }
                            
                            // Estadísticas
                            item {
                                RouteStatisticsCard(
                                    totalRoutes = uiState.totalRoutes,
                                    displayedRoutes = uiState.filteredRoutes.size,
                                    localeManager = localeViewModel.localeManager
                                )
                            }
                            
                            // Lista de rutas
                            items(
                                items = uiState.filteredRoutes,
                                key = { it.id }
                            ) { route ->
                                RouteCard(
                                    route = route,
                                    localeManager = localeViewModel.localeManager,
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
            localeManager = localeViewModel.localeManager,
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
    localeManager: LocaleManager,
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
                    text = localeManager.getLocalizedString(R.string.route_list_active_filters),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                if (dateFilter != null) {
                    Text(
                        text = String.format(
                            localeManager.getLocalizedString(R.string.route_list_date_filter),
                            dateFilter.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                if (statusFilter != null) {
                    Text(
                        text = String.format(
                            localeManager.getLocalizedString(R.string.route_list_status_filter),
                            getStatusDisplayName(statusFilter, localeManager)
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            TextButton(onClick = onClearFilters) {
                Text(localeManager.getLocalizedString(R.string.route_list_clear))
            }
        }
    }
}

@Composable
private fun RouteStatisticsCard(
    totalRoutes: Int,
    displayedRoutes: Int,
    localeManager: LocaleManager
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.Route,
                label = localeManager.getLocalizedString(R.string.route_list_total_routes),
                value = totalRoutes.toString()
            )
            
            if (displayedRoutes != totalRoutes) {
                StatItem(
                    icon = Icons.Default.FilterList,
                    label = localeManager.getLocalizedString(R.string.route_list_showing),
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
    localeManager: LocaleManager,
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
                localeManager.getLocalizedString(R.string.route_list_no_routes_with_filters)
            } else {
                localeManager.getLocalizedString(R.string.route_list_no_routes_created)
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (hasFilters) {
                localeManager.getLocalizedString(R.string.route_list_adjust_filters)
            } else {
                localeManager.getLocalizedString(R.string.route_list_create_first_route)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = ColorTextSecondary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (hasFilters) {
            OutlinedButton(onClick = onClearFilters) {
                Icon(Icons.Default.Clear, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(localeManager.getLocalizedString(R.string.route_list_clear_filters))
            }
        } else {
            Button(onClick = onCreateNew) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(localeManager.getLocalizedString(R.string.route_list_create_route))
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    localeManager: LocaleManager,
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
            text = localeManager.getLocalizedString(R.string.route_list_error_loading),
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
            Text(localeManager.getLocalizedString(R.string.route_list_retry))
        }
    }
}

@Composable
private fun FiltersDialog(
    selectedDate: LocalDate?,
    selectedStatus: RouteStatus?,
    localeManager: LocaleManager,
    onDateSelected: (LocalDate?) -> Unit,
    onStatusSelected: (RouteStatus?) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = localeManager.getLocalizedString(R.string.route_list_filters_dialog_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            ) 
        },
        text = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Filtro de fecha
                    Column {
                        Text(
                            text = localeManager.getLocalizedString(R.string.route_list_date_label),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Opciones de fecha rápidas
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FilterChip(
                                selected = selectedDate == LocalDate.now(),
                                onClick = { onDateSelected(LocalDate.now()) },
                                label = { Text(localeManager.getLocalizedString(R.string.route_list_today)) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = selectedDate == null,
                                onClick = { onDateSelected(null) },
                                label = { Text(localeManager.getLocalizedString(R.string.route_list_all_dates)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    HorizontalDivider(color = Color(0xFF1565C0).copy(alpha = 0.3f))
                    
                    // Filtro de estado
                    Column {
                        Text(
                            text = localeManager.getLocalizedString(R.string.route_list_status_label),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(null to localeManager.getLocalizedString(R.string.route_list_all_statuses)) + RouteStatus.values().map { 
                                it to getStatusDisplayName(it, localeManager) 
                            }.forEach { (status, name) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onStatusSelected(status) }
                                        .padding(vertical = 4.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedStatus == status,
                                        onClick = { onStatusSelected(status) }
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (selectedStatus == status) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = localeManager.getLocalizedString(R.string.route_list_apply),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onClearFilters()
                onDismiss()
            }) {
                Text(localeManager.getLocalizedString(R.string.route_list_clear))
            }
        }
    )
}

// Agregamos el LocaleManager como parámetro desde el ViewModel
private fun getStatusDisplayName(status: RouteStatus, localeManager: LocaleManager): String {
    return when (status) {
        RouteStatus.DRAFT -> localeManager.getLocalizedString(R.string.route_status_draft)
        RouteStatus.CONFIRMED -> localeManager.getLocalizedString(R.string.route_status_confirmed)
        RouteStatus.IN_PROGRESS -> localeManager.getLocalizedString(R.string.route_status_in_progress)
        RouteStatus.COMPLETED -> localeManager.getLocalizedString(R.string.route_status_completed)
        RouteStatus.CANCELLED -> localeManager.getLocalizedString(R.string.route_status_cancelled)
    }
}
