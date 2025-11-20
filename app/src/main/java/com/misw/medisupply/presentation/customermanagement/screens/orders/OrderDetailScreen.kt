package com.misw.medisupply.presentation.customermanagement.screens.orders

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.R
import com.misw.medisupply.core.i18n.LocaleManager
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderItem
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.presentation.common.components.ErrorView
import com.misw.medisupply.presentation.customermanagement.screens.orders.viewmodel.OrderDetailViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Reactive localized string resource function
 */
@Composable
fun localizedStringResource(@StringRes id: Int, localeManager: LocaleManager): String {
    val currentLanguage by localeManager.currentLanguage.collectAsState()
    val context = LocalContext.current
    return remember(currentLanguage) {
        context.getString(id)
    }
}

/**
 * Order Detail Screen
 * Pantalla de detalle del pedido - muestra información completa del pedido como en la imagen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Int,
    onNavigateBack: () -> Unit = {},
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val localeManager = viewModel.localeManager
    
    // Load order detail when screen is opened
    LaunchedEffect(orderId) {
        android.util.Log.d("OrderDetailScreen", "Iniciando carga de detalle para Order ID: $orderId")
        viewModel.loadOrderDetail(orderId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = uiState.order?.orderNumber ?: localizedStringResource(R.string.order_detail_title, localeManager),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = localizedStringResource(R.string.back_button_description, localeManager)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    ErrorView(
                        message = uiState.error ?: localizedStringResource(R.string.unknown_error, localeManager),
                        onRetry = { viewModel.retryLoad(orderId) }
                    )
                }
                uiState.order != null -> {
                    uiState.order?.let { order ->
                        OrderDetailContent(order = order, localeManager = localeManager)
                    }
                }
            }
        }
    }
}

/**
 * Main content for order detail
 */
@Composable
private fun OrderDetailContent(order: Order, localeManager: LocaleManager) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Order Header Card
        item {
            OrderHeaderCard(order = order, localeManager = localeManager)
        }
        
        // Customer Information Card
        item {
            CustomerInfoCard(order = order, localeManager = localeManager)
        }
        
        // Delivery Information Card
        item {
            DeliveryInfoCard(order = order, localeManager = localeManager)
        }
        
        // Order Items Header
        item {
            Text(
                text = localizedStringResource(R.string.items_count_label, localeManager).format(order.items.size),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Order Items List
        items(order.items) { item ->
            OrderItemCard(item = item, localeManager = localeManager)
        }
        
        // Order Total Card
        item {
            OrderTotalCard(order = order, localeManager = localeManager)
        }
    }
}

/**
 * Order header card with order number, dates and status
 */
@Composable
private fun OrderHeaderCard(order: Order, localeManager: LocaleManager) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Order Number
            Text(
                text = order.orderNumber ?: "N/A",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Order Date
            InfoRow(
                icon = Icons.Default.CalendarToday,
                label = localizedStringResource(R.string.order_header_date_label, localeManager),
                value = formatDate(order.orderDate)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Status
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = getStatusColor(order.status),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = localizedStringResource(R.string.order_status_label, localeManager),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = order.status.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = getStatusColor(order.status)
                )
            }
        }
    }
}

/**
 * Customer information card
 */
@Composable
private fun CustomerInfoCard(order: Order, localeManager: LocaleManager) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = localizedStringResource(R.string.customer_data_title, localeManager),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val customerName = order.customer?.getDisplayName() ?: localizedStringResource(R.string.customer_id_label, localeManager).format(order.customerId)
            InfoRow(
                icon = Icons.Default.Person,
                label = localizedStringResource(R.string.customer_label, localeManager),
                value = customerName
            )
            
            order.customer?.contactEmail?.let { customerEmail ->
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    icon = Icons.Default.Business,
                    label = localizedStringResource(R.string.email_label, localeManager),
                    value = customerEmail
                )
            }
        }
    }
}

/**
 * Delivery information card
 */
@Composable
private fun DeliveryInfoCard(order: Order, localeManager: LocaleManager) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = localizedStringResource(R.string.medisupply_data_title, localeManager),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            InfoRow(
                icon = Icons.Default.CalendarToday,
                label = localizedStringResource(R.string.delivery_date_detail_label, localeManager),
                value = order.deliveryDate?.let { 
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) 
                } ?: localizedStringResource(R.string.date_undefined, localeManager)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InfoRow(
                icon = Icons.Default.LocalShipping,
                label = localizedStringResource(R.string.delivery_time_label, localeManager),
                value = localizedStringResource(R.string.delivery_time_default, localeManager)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InfoRow(
                icon = Icons.Default.LocalShipping,
                label = localizedStringResource(R.string.partial_delivery_label, localeManager),
                value = localizedStringResource(R.string.partial_delivery_none, localeManager)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InfoRow(
                icon = Icons.Default.LocalShipping,
                label = localizedStringResource(R.string.carrier_label, localeManager),
                value = localizedStringResource(R.string.carrier_default, localeManager)
            )
        }
    }
}

/**
 * Order item card
 */
@Composable
private fun OrderItemCard(item: OrderItem, localeManager: LocaleManager) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.productName ?: item.productSku,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = localizedStringResource(R.string.quantities_label, localeManager).format(item.quantity),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = formatCurrency(item.total),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Order total card
 */
@Composable
private fun OrderTotalCard(order: Order, localeManager: LocaleManager) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = localizedStringResource(R.string.total_label, localeManager),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = formatCurrency(order.totalAmount),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Reusable info row component
 */
@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Get status color based on order status
 */
@Composable
private fun getStatusColor(status: OrderStatus): Color {
    return when (status) {
        OrderStatus.PENDING -> MaterialTheme.colorScheme.outline
        OrderStatus.CONFIRMED -> MaterialTheme.colorScheme.primary
        OrderStatus.PROCESSING -> MaterialTheme.colorScheme.tertiary
        OrderStatus.SHIPPED -> MaterialTheme.colorScheme.secondary
        OrderStatus.DELIVERED -> Color(0xFF4CAF50) // Green
        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error
    }
}

/**
 * Format date utility
 */
private fun formatDate(date: Date?): String {
    return if (date != null) {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    } else {
        "N/A"
    }
}

/**
 * Format currency utility
 */
private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    return format.format(amount)
}