package com.misw.medisupply.presentation.customermanagement.screens.orders

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.presentation.customermanagement.screens.orders.viewmodel.OrderTrackingViewModel
import com.misw.medisupply.presentation.customermanagement.screens.orders.viewmodel.OrderTrackingUiState
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Customer Orders Screen
 * Pantalla de pedidos para clientes - historial y seguimiento con filtros
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOrdersScreen(
    onNavigateToOrderDetail: (Int) -> Unit = {},
    viewModel: OrderTrackingViewModel = hiltViewModel()
) {
    Log.d("CustomerOrdersScreen", "=== PANTALLA DE PEDIDOS INICIADA ===")
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error message
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Seguimiento de entregas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleFiltersVisibility() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtros",
                            tint = if (uiState.showFilters || uiState.hasActiveFilters) 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { viewModel.loadOrders() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filters Section
            AnimatedVisibility(visible = uiState.showFilters) {
                FiltersSection(
                    uiState = uiState,
                    onStatusChange = viewModel::updateStatusFilter,
                    onDeliveryDateFromChange = viewModel::updateDeliveryDateFrom,
                    onDeliveryDateToChange = viewModel::updateDeliveryDateTo,
                    onOrderDateFromChange = viewModel::updateOrderDateFrom,
                    onOrderDateToChange = viewModel::updateOrderDateTo,
                    onClearFilters = viewModel::clearFilters
                )
            }
            
            // Orders List
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                uiState.orders.isEmpty() -> {
                    EmptyOrdersState()
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.orders) { order ->
                            OrderCard(
                                order = order,
                                onOrderClick = { 
                                    Log.d("CustomerOrdersScreen", "Click en tarjeta: ${order.orderNumber}")
                                    order.id?.let { orderId ->
                                        Log.d("CustomerOrdersScreen", "Navegando a detalle con ID: $orderId")
                                        onNavigateToOrderDetail(orderId)
                                    } ?: Log.e("CustomerOrdersScreen", "Order ID es null para pedido: ${order.orderNumber}")
                                },
                                onDetailClick = {
                                    Log.d("CustomerOrdersScreen", "Click en botón detalle: ${order.orderNumber}")
                                    order.id?.let { orderId ->
                                        Log.d("CustomerOrdersScreen", "Navegando a detalle con ID: $orderId")
                                        onNavigateToOrderDetail(orderId)
                                    } ?: Log.e("CustomerOrdersScreen", "Order ID es null para pedido: ${order.orderNumber}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Filters section component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltersSection(
    uiState: OrderTrackingUiState,
    onStatusChange: (String) -> Unit,
    onDeliveryDateFromChange: (Date?) -> Unit,
    onDeliveryDateToChange: (Date?) -> Unit,
    onOrderDateFromChange: (Date?) -> Unit,
    onOrderDateToChange: (Date?) -> Unit,
    onClearFilters: () -> Unit
) {
    // Date Picker States - Declared at function level so dialogs can access them
    var expandedStatus by remember { mutableStateOf(false) }
    var showDateFromPicker by remember { mutableStateOf(false) }
    var showDateToPicker by remember { mutableStateOf(false) }
    val dateFromPickerState = rememberDatePickerState()
    val dateToPickerState = rememberDatePickerState()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Filtros",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Status Filter
            ExposedDropdownMenuBox(
                expanded = expandedStatus,
                onExpandedChange = { expandedStatus = !expandedStatus }
            ) {
                OutlinedTextField(
                    value = uiState.statusOptions.find { it.first == uiState.selectedStatus }?.second 
                        ?: "Todos los estados",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expandedStatus,
                    onDismissRequest = { expandedStatus = false }
                ) {
                    uiState.statusOptions.forEach { (value, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                onStatusChange(if (value == "todos") "" else value)
                                expandedStatus = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Date Filters
            Text(
                text = "Fecha de entrega",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.deliveryDateFrom?.let { 
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) 
                    } ?: "",
                    onValueChange = { },
                    label = { Text("Desde") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDateFromPicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Fecha desde")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                
                OutlinedTextField(
                    value = uiState.deliveryDateTo?.let { 
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) 
                    } ?: "",
                    onValueChange = { },
                    label = { Text("Hasta") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDateToPicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Fecha hasta")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Clear Filters Button
            if (uiState.hasActiveFilters) {
                OutlinedButton(
                    onClick = onClearFilters,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Limpiar filtros")
                }
            }
        }
    }
    
    // Date Picker Dialogs - Outside the Card so they show as overlays
    if (showDateFromPicker) {
        DatePickerDialog(
            onDismissRequest = { showDateFromPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateFromPickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Date(millis)
                            onDeliveryDateFromChange(selectedDate)
                        }
                        showDateFromPicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateFromPicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = dateFromPickerState,
                title = {
                    Text(
                        text = "Fecha desde",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        }
    }
    
    if (showDateToPicker) {
        DatePickerDialog(
            onDismissRequest = { showDateToPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateToPickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Date(millis)
                            onDeliveryDateToChange(selectedDate)
                        }
                        showDateToPicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateToPicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = dateToPickerState,
                title = {
                    Text(
                        text = "Fecha hasta",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        }
    }
}

/**
 * Order card component
 */
@Composable
private fun OrderCard(
    order: Order,
    onOrderClick: () -> Unit = {},
    onDetailClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOrderClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Order Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderNumber ?: "Sin número",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = order.orderDate?.let { 
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) 
                        } ?: "Sin fecha",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Button(
                    onClick = {
                        Log.d("CustomerOrdersScreen", "Click en botón Detalle para pedido: ${order.orderNumber}")
                        onDetailClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Detalle")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Order Status
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = getStatusColor(order.status),
                            shape = RoundedCornerShape(6.dp)
                        )
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = getStatusDisplayName(order.status),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Order Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale("es", "CO")).format(order.totalAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Order Date
            order.orderDate?.let { orderDate ->
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Fecha pedido:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(orderDate),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            // Delivery Date - Now using proper deliveryDate field from backend
            order.deliveryDate?.let { deliveryDate ->
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Fecha entrega:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(deliveryDate),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Custom Date Picker Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
/**
 * Empty orders state component
 */
@Composable
private fun EmptyOrdersState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📦",
            style = MaterialTheme.typography.displayLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No hay pedidos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "No se encontraron pedidos con los filtros seleccionados",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Get color for order status
 */
private fun getStatusColor(status: OrderStatus): Color {
    return when (status) {
        OrderStatus.PENDING -> Color(0xFFFFA726) // Orange
        OrderStatus.CONFIRMED -> Color(0xFF42A5F5) // Blue  
        OrderStatus.PROCESSING -> Color(0xFF9C27B0) // Purple
        OrderStatus.SHIPPED -> Color(0xFF26C6DA) // Cyan
        OrderStatus.DELIVERED -> Color(0xFF66BB6A) // Green
        OrderStatus.CANCELLED -> Color(0xFFEF5350) // Red
    }
}

/**
 * Get display name for order status
 */
private fun getStatusDisplayName(status: OrderStatus): String {
    return status.displayName
}
