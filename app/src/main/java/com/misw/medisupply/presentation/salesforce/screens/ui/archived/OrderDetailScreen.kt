package com.misw.medisupply.presentation.salesforce.screens.orders.detail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderItem
import com.misw.medisupply.presentation.common.components.ErrorView
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
import com.misw.medisupply.presentation.salesforce.components.OrderStatusBadge
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Order Detail Screen
 * Displays full order information including all products
 */
@Composable
fun OrderDetailScreen(
    orderId: String,
    viewModel: OrderDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(orderId) {
        viewModel.loadOrderDetail(orderId)
    }
    
    // Handle success states
    LaunchedEffect(state.updateSuccess) {
        if (state.updateSuccess) {
            onNavigateBack()
        }
    }
    
    LaunchedEffect(state.deleteSuccess) {
        if (state.deleteSuccess) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = "Detalle del pedido",
                subtitle = "Pedidos - Medisupply",
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        when {
            state.isLoading -> {
                LoadingIndicator()
            }
            state.error != null -> {
                ErrorView(
                    message = state.error!!,
                    onRetry = { viewModel.loadOrderDetail(orderId) }
                )
            }
            state.order != null -> {
                OrderDetailContent(
                    order = state.order!!,
                    state = state,
                    onUpdateQuantity = { itemId, quantity ->
                        viewModel.onEvent(OrderDetailEvent.UpdateItemQuantity(itemId, quantity))
                    },
                    onRemoveItem = { itemId ->
                        viewModel.onEvent(OrderDetailEvent.RemoveItem(itemId))
                    },
                    onConfirmOrder = {
                        viewModel.onEvent(OrderDetailEvent.ConfirmOrder)
                    },
                    onDeleteOrder = {
                        showDeleteDialog = true
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            order = state.order,
            onConfirm = {
                viewModel.onEvent(OrderDetailEvent.DeleteOrder)
                showDeleteDialog = false
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
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
 * Order detail content
 */
@Composable
private fun OrderDetailContent(
    order: Order,
    state: OrderDetailState,
    onUpdateQuantity: (Int, Int) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onConfirmOrder: () -> Unit,
    onDeleteOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentItems = state.getCurrentItems().filter { it.quantity > 0 }
    val newTotal = state.calculateNewTotal()
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Order header card
        item {
            OrderHeaderCard(order = order)
        }
        
        // Customer info card
        item {
            CustomerInfoCard(order = order)
        }
        
        // Delivery info card
        item {
            DeliveryInfoCard(order = order)
        }
        
        // Products header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Productos del pedido",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }
        }
        
        // Products list
        items(
            items = currentItems,
            key = { item -> item.id ?: 0 }
        ) { item ->
            EditableProductItemCard(
                item = item,
                onQuantityChange = { newQuantity ->
                    item.id?.let { onUpdateQuantity(it, newQuantity) }
                },
                onRemove = {
                    item.id?.let { onRemoveItem(it) }
                }
            )
        }
        
        // Order summary card
        item {
            OrderSummaryCard(
                order = order,
                currentTotal = if (state.isModified()) newTotal else order.totalAmount,
                totalItems = currentItems.size,
                totalQuantity = currentItems.sumOf { it.quantity }
            )
        }
        
        // Action buttons
        item {
            ActionButtons(
                isModified = state.isModified(),
                isUpdating = state.isUpdating,
                isDeleting = state.isDeleting,
                onConfirm = onConfirmOrder,
                onDelete = onDeleteOrder
            )
        }
    }
}

/**
 * Order header card with order number, date, and status
 */
@Composable
private fun OrderHeaderCard(
    order: Order,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
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
                        text = "Pedido",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = order.orderNumber ?: "N/A",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                }
                
                OrderStatusBadge(status = order.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            HorizontalDivider(color = Color(0xFFE0E0E0))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Date
            order.orderDate?.let { date ->
                InfoRow(
                    label = "Fecha de creación",
                    value = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date)
                )
            }
        }
    }
}

/**
 * Customer information card
 */
@Composable
private fun CustomerInfoCard(
    order: Order,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SectionHeader(
                icon = Icons.Default.Info,
                title = "Información del cliente"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            order.customer?.let { customer ->
                InfoRow(
                    label = "Cliente",
                    value = customer.getDisplayName()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            InfoRow(
                label = "Cliente ID",
                value = order.customerId.toString()
            )
            
            order.customer?.contactPhone?.let { phone ->
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    label = "Teléfono",
                    value = phone
                )
            }
            
            order.customer?.contactEmail?.let { email ->
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    label = "Email",
                    value = email
                )
            }
        }
    }
}

/**
 * Delivery information card
 */
@Composable
private fun DeliveryInfoCard(
    order: Order,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SectionHeader(
                icon = Icons.Default.CheckCircle,
                title = "Información de entrega"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            order.deliveryAddress?.let { address ->
                InfoRow(
                    label = "Dirección",
                    value = address
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            order.deliveryCity?.let { city ->
                InfoRow(
                    label = "Ciudad",
                    value = city
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            order.deliveryDepartment?.let { department ->
                InfoRow(
                    label = "Departamento",
                    value = department
                )
            }
            
            order.notes?.let { notes ->
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    label = "Notas",
                    value = notes
                )
            }
        }
    }
}

/**
 * Editable product item card with quantity input and delete button
 */
@Composable
private fun EditableProductItemCard(
    item: OrderItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    var quantityText by remember(item.quantity) { mutableStateOf(item.quantity.toString()) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with product name and delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.productName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF212121)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "SKU: ${item.productSku}",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }
                
                // Delete button
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar producto",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity input
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cantidad:",
                        fontSize = 12.sp,
                        color = Color(0xFF757575),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { newValue: String ->
                            quantityText = newValue
                            newValue.toIntOrNull()?.let { qty ->
                                if (qty >= 0) {
                                    onQuantityChange(qty)
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .width(100.dp)
                            .height(56.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Price info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Precio unitario:",
                        fontSize = 12.sp,
                        color = Color(0xFF757575),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.getFormattedUnitPrice(),
                        fontSize = 13.sp,
                        color = Color(0xFF212121)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Subtotal
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Subtotal",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.getFormattedSubtotal(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Order summary card with totals
 */
@Composable
private fun OrderSummaryCard(
    order: Order,
    currentTotal: Double,
    totalItems: Int,
    totalQuantity: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F8F0)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Resumen del pedido",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total de productos:",
                    fontSize = 14.sp,
                    color = Color(0xFF424242)
                )
                Text(
                    text = "$totalItems items",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212121)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total de unidades:",
                    fontSize = 14.sp,
                    color = Color(0xFF424242)
                )
                Text(
                    text = "$totalQuantity unidades",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212121)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            HorizontalDivider(color = Color(0xFF9E9E9E))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total a pagar:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$ ${String.format("%,.2f", currentTotal)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Section header with icon and title
 */
@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
    }
}

/**
 * Info row component
 */
@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontSize = 13.sp,
            color = Color(0xFF757575),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.4f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            color = Color(0xFF212121),
            modifier = Modifier.weight(0.6f)
        )
    }
}

/**
 * Action buttons for confirm and delete
 */
@Composable
private fun ActionButtons(
    isModified: Boolean,
    isUpdating: Boolean,
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Confirm button
        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = isModified && !isUpdating && !isDeleting,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = Color(0xFFE0E0E0)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isUpdating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = if (isModified) "Confirmar pedido" else "Sin cambios",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Delete button
        Button(
            onClick = onDelete,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isUpdating && !isDeleting,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD32F2F),
                disabledContainerColor = Color(0xFFE0E0E0)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isDeleting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Eliminar pedido",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Delete confirmation dialog
 */
@Composable
private fun DeleteConfirmationDialog(
    order: Order?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "¿Eliminar pedido?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Esta acción no se puede deshacer.",
                    fontWeight = FontWeight.Medium
                )
                
                order?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Order info
                    Text(
                        text = "Pedido: ${it.orderNumber ?: "N/A"}",
                        fontSize = 14.sp,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Estado: ${it.status.displayName}",
                        fontSize = 14.sp,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Total: ${it.getFormattedTotal()}",
                        fontSize = 14.sp,
                        color = Color(0xFF424242)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "¿Está seguro de que desea continuar?",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
