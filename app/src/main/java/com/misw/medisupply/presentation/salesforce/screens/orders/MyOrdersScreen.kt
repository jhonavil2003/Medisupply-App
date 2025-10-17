package com.misw.medisupply.presentation.salesforce.screens.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.presentation.common.components.ErrorView
import com.misw.medisupply.presentation.common.components.LoadingIndicator
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
import com.misw.medisupply.presentation.salesforce.components.OrderCard

/**
 * My Orders Screen
 * Displays list of orders for the logged-in seller with filters and search
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    viewModel: MyOrdersViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()
    
    // Show error in snackbar
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(MyOrdersEvent.ClearError)
        }
    }
    
    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = "Mis Pedidos",
                subtitle = "Fuerza de ventas - Medisupply",
                onNavigateBack = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.onEvent(MyOrdersEvent.RefreshOrders) },
            state = pullToRefreshState,
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Statistics card
                if (!state.isLoading && state.hasOrders()) {
                    StatisticsCard(
                        totalOrders = state.getTotalOrdersCount(),
                        totalAmount = state.getFormattedTotalAmount()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Search bar
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.onEvent(MyOrdersEvent.SearchOrders(it)) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Filter chips
                FilterChips(
                    orderStatuses = viewModel.getOrderStatuses(),
                    selectedStatus = state.selectedStatus,
                    onStatusSelected = { viewModel.onEvent(MyOrdersEvent.FilterByStatus(it)) },
                    getOrdersCount = { status -> state.getOrdersCountByStatus(status) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Content
                when {
                    state.isLoading && !state.isRefreshing -> {
                        LoadingIndicator()
                    }
                    state.error != null && !state.hasOrders() -> {
                        ErrorView(
                            message = state.error!!,
                            onRetry = { viewModel.onEvent(MyOrdersEvent.LoadOrders) }
                        )
                    }
                    state.hasOrders() -> {
                        val filteredOrders = state.getFilteredOrders()
                        if (filteredOrders.isEmpty()) {
                            EmptyFilteredState()
                        } else {
                            OrdersList(
                                orders = filteredOrders,
                                onOrderClick = { order ->
                                    viewModel.onEvent(MyOrdersEvent.SelectOrder(order))
                                }
                            )
                        }
                    }
                    else -> {
                        EmptyState()
                    }
                }
            }
        }
    }
}

/**
 * Statistics card component
 */
@Composable
private fun StatisticsCard(
    totalOrders: Int,
    totalAmount: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = totalOrders.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Pedidos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = totalAmount,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Monto Total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * Search bar component
 */
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Buscar pedidos...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        singleLine = true
    )
}

/**
 * Filter chips component
 */
@Composable
private fun FilterChips(
    orderStatuses: List<OrderStatus>,
    selectedStatus: OrderStatus?,
    onStatusSelected: (OrderStatus?) -> Unit,
    getOrdersCount: (OrderStatus) -> Int,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" chip
        item {
            FilterChip(
                selected = selectedStatus == null,
                onClick = { onStatusSelected(null) },
                label = { Text("Todos") }
            )
        }
        
        // Status chips with count
        items(orderStatuses) { status ->
            val count = getOrdersCount(status)
            FilterChip(
                selected = selectedStatus == status,
                onClick = { 
                    onStatusSelected(if (selectedStatus == status) null else status)
                },
                label = { 
                    Text("${status.displayName} ($count)")
                }
            )
        }
    }
}

/**
 * Orders list component
 */
@Composable
private fun OrdersList(
    orders: List<com.misw.medisupply.domain.model.order.Order>,
    onOrderClick: (com.misw.medisupply.domain.model.order.Order) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(orders, key = { it.id }) { order ->
            OrderCard(
                order = order,
                onClick = { onOrderClick(order) }
            )
        }
    }
}

/**
 * Empty state when no orders exist
 */
@Composable
private fun EmptyState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No hay pedidos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Aún no has registrado ningún pedido",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Empty state when filters don't match any orders
 */
@Composable
private fun EmptyFilteredState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No se encontraron pedidos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Intenta con otros filtros o términos de búsqueda",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
