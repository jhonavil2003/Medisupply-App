package com.misw.medisupply.presentation.salesforce.screens.orders.list

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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.material3.ButtonDefaults
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.R
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.presentation.components.localizedStringResource
import com.misw.medisupply.presentation.salesforce.screens.orders.list.components.FilterModal
import com.misw.medisupply.presentation.salesforce.screens.orders.list.DateRange
import com.misw.medisupply.presentation.salesforce.screens.orders.list.viewmodel.MyOrdersScreenViewModel
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.presentation.common.components.ErrorView
import com.misw.medisupply.presentation.salesforce.components.OrderCard

/**
 * My Orders Screen
 * Displays list of orders for the logged-in seller with dropdown filter
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    viewModel: MyOrdersViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToEditOrder: (String) -> Unit = {},
    screenViewModel: MyOrdersScreenViewModel = hiltViewModel()
) {
    // Obtener LocaleManager del ViewModel
    val localeManager = screenViewModel.localeManager
    val currentLanguage = localeManager.currentLanguage.collectAsState().value
    
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()
    
    // Filter modal state
    var showFilterModal by remember { mutableStateOf(false) }
    
    // Reload orders when screen is displayed
    LaunchedEffect(Unit) {
        viewModel.onEvent(MyOrdersEvent.LoadOrders)
    }
    
    // Show error in snackbar
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(MyOrdersEvent.ClearError)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = localizedStringResource(R.string.my_orders_title, localeManager),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                        Text(
                            text = localizedStringResource(R.string.orders_subtitle, localeManager),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1565C0).copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    onNavigateBack?.let { callback ->
                        IconButton(onClick = callback) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color(0xFF1565C0)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showFilterModal = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtros",
                            tint = if (state.hasActiveFilters()) Color(0xFF4CAF50) else Color(0xFF1565C0)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFDAE5FF),
                    titleContentColor = Color(0xFF1565C0),
                    navigationIconContentColor = Color(0xFF1565C0),
                    actionIconContentColor = Color(0xFF1565C0)
                )
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
                    .padding(top = 12.dp)
            ) {
                // Active filters indicator
                if (state.hasActiveFilters()) {
                    ActiveFiltersCard(
                        selectedStatus = state.selectedStatus,
                        selectedCustomerId = state.selectedCustomerId,
                        selectedDateRange = state.selectedDateRange,
                        customers = state.customers,
                        localeManager = localeManager,
                        onClearFilters = { viewModel.onEvent(MyOrdersEvent.ClearFilters) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Content
                when {
                    state.isLoading -> {
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
                            EmptyFilteredState(localeManager = localeManager)
                        } else {
                            OrdersList(
                                orders = filteredOrders,
                                totalOrders = state.getTotalFilteredOrderCount(),
                                localeManager = localeManager,
                                onDetailClick = { order: Order ->
                                    viewModel.onEvent(MyOrdersEvent.SelectOrder(order))
                                },
                                onEditClick = { order: Order ->
                                    order.id?.let { orderId ->
                                        onNavigateToEditOrder(orderId.toString())
                                    }
                                }
                            )
                        }
                    }
                    else -> {
                        EmptyState(localeManager = localeManager)
                    }
                }
            }
        }
        
        // Filter Modal
        if (showFilterModal) {
            FilterModal(
                selectedStatus = state.selectedStatus,
                selectedCustomerId = state.selectedCustomerId,
                selectedDateRange = state.selectedDateRange,
                customers = state.customers,
                localeManager = localeManager,
                onStatusSelected = { viewModel.onEvent(MyOrdersEvent.FilterByStatus(it)) },
                onCustomerSelected = { viewModel.onEvent(MyOrdersEvent.FilterByCustomer(it)) },
                onDateRangeSelected = { viewModel.onEvent(MyOrdersEvent.FilterByDateRange(it)) },
                onApplyFilters = { /* Filters are applied immediately */ },
                onClearFilters = { viewModel.onEvent(MyOrdersEvent.ClearFilters) },
                onDismiss = { showFilterModal = false }
            )
        }
    }
}

/**
 * Active filters indicator card
 */
@Composable
private fun ActiveFiltersCard(
    selectedStatus: OrderStatus?,
    selectedCustomerId: Int?,
    selectedDateRange: DateRange?,
    customers: List<Customer>,
    localeManager: com.misw.medisupply.core.i18n.LocaleManager,
    onClearFilters: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        ),
        shape = RoundedCornerShape(8.dp)
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
                    text = "Filtros activos",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E7D32)
                )
                
                val filterTexts = mutableListOf<String>()
                
                selectedStatus?.let {
                    filterTexts.add(getStatusDisplayName(it, localeManager))
                }
                
                selectedCustomerId?.let { customerId ->
                    customers.find { it.id == customerId }?.let { customer ->
                        filterTexts.add(customer.getDisplayName())
                    }
                }
                
                selectedDateRange?.let {
                    filterTexts.add("Rango de fechas")
                }
                
                Text(
                    text = filterTexts.joinToString(", "),
                    fontSize = 11.sp,
                    color = Color(0xFF388E3C)
                )
            }
            
            Button(
                onClick = onClearFilters,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                ),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Limpiar",
                    fontSize = 11.sp
                )
            }
        }
    }
}

/**
 * Loading indicator component
 */
@Composable
private fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Empty state when filters don't match any orders
 */
@Composable
private fun EmptyFilteredState(
    localeManager: com.misw.medisupply.core.i18n.LocaleManager,
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
                text = localizedStringResource(R.string.orders_no_orders_found, localeManager),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = localizedStringResource(R.string.orders_try_other_filters, localeManager),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Orders list component without pagination
 */
@Composable
private fun OrdersList(
    orders: List<Order>,
    totalOrders: Int,
    localeManager: com.misw.medisupply.core.i18n.LocaleManager,
    onDetailClick: (Order) -> Unit,
    onEditClick: (Order) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Orders count info
        Text(
            text = "$totalOrders pedidos encontrados",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Orders list
        LazyColumn(
            modifier = modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(orders, key = { it.id ?: 0 }) { order ->
                OrderCard(
                    order = order,
                    localeManager = localeManager,
                    onDetailClick = { onDetailClick(order) },
                    onEditClick = { onEditClick(order) }
                )
            }
        }
    }
}



/**
 * Empty state when no orders exist
 */
@Composable
private fun EmptyState(
    localeManager: com.misw.medisupply.core.i18n.LocaleManager,
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
                text = localizedStringResource(R.string.orders_no_orders_title, localeManager),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = localizedStringResource(R.string.orders_no_orders_message, localeManager),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Get localized display name for order status
 */
@Composable
private fun getStatusDisplayName(status: OrderStatus, localeManager: com.misw.medisupply.core.i18n.LocaleManager): String {
    return when (status) {
        OrderStatus.PENDING -> localizedStringResource(R.string.order_status_pending, localeManager)
        OrderStatus.CONFIRMED -> localizedStringResource(R.string.order_status_confirmed, localeManager)
        OrderStatus.PROCESSING -> localizedStringResource(R.string.order_status_processing, localeManager)
        OrderStatus.SHIPPED -> localizedStringResource(R.string.order_status_shipped, localeManager)
        OrderStatus.DELIVERED -> localizedStringResource(R.string.order_status_delivered, localeManager)
        OrderStatus.CANCELLED -> localizedStringResource(R.string.order_status_cancelled, localeManager)
    }
}
