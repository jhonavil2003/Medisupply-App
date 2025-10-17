package com.misw.medisupply.presentation.salesforce.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.presentation.common.components.ErrorView
import com.misw.medisupply.presentation.common.components.LoadingIndicator
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
import com.misw.medisupply.presentation.salesforce.components.OrderCard
import com.misw.medisupply.domain.model.order.Order

/**
 * My Orders Screen
 * Displays list of orders for the logged-in seller with dropdown filter
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    viewModel: MyOrdersViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToEditOrder: (String) -> Unit = {}
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
                title = "Mis pedidos",
                subtitle = "Pedidos - Medisupply",
                onNavigateBack = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F5F5)
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
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            ) {
                // Dropdown filter
                StatusDropdown(
                    selectedStatus = state.selectedStatus,
                    onStatusSelected = { viewModel.onEvent(MyOrdersEvent.FilterByStatus(it)) }
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
                                onDetailClick = { order: Order ->
                                    // TODO: Navigate to detail
                                    viewModel.onEvent(MyOrdersEvent.SelectOrder(order))
                                },
                                onEditClick = { order: Order ->
                                    onNavigateToEditOrder(order.id.toString())
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
 * Dropdown for status filter
 */
@Composable
private fun StatusDropdown(
    selectedStatus: OrderStatus?,
    onStatusSelected: (OrderStatus?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = true },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedStatus?.displayName ?: "Todos los estados",
                fontSize = 14.sp,
                color = Color(0xFF212121)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Filtrar",
                tint = Color(0xFF757575)
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            DropdownMenuItem(
                text = { Text("Todos los estados") },
                onClick = {
                    onStatusSelected(null)
                    expanded = false
                }
            )
            OrderStatus.entries.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status.displayName) },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false
                    }
                )
            }
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
                text = "Intenta con otros filtros",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
    onDetailClick: (com.misw.medisupply.domain.model.order.Order) -> Unit,
    onEditClick: (com.misw.medisupply.domain.model.order.Order) -> Unit,
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
                onDetailClick = { onDetailClick(order) },
                onEditClick = { onEditClick(order) }
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
