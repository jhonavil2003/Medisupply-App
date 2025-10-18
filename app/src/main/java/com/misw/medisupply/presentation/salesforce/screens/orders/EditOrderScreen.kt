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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.presentation.common.components.LoadingIndicator
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
import com.misw.medisupply.presentation.salesforce.components.ProductItemCard
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Edit Order Screen (Frame 11)
 * Allows editing an existing order with full details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOrderScreen(
    orderId: String,
    viewModel: EditOrderViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show success message and navigate back
    LaunchedEffect(state.successMessage) {
        state.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(EditOrderEvent.ClearSuccess)
            onNavigateBack()
        }
    }
    
    // Show error in snackbar
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(EditOrderEvent.ClearError)
        }
    }
    
    // Delete confirmation dialog
    if (state.showDeleteConfirmation) {
        DeleteConfirmationDialog(
            onConfirm = { viewModel.onEvent(EditOrderEvent.DeleteOrder) },
            onDismiss = { viewModel.onEvent(EditOrderEvent.HideDeleteConfirmation) }
        )
    }
    
    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = "Editar pedido",
                subtitle = "Pedidos - Medisupply",
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.isLoading) {
                LoadingIndicator()
            } else if (state.order != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                // Customer information card
                item {
                    CustomerInfoCard(order = state.order!!)
                }
                
                // Order information card
                item {
                    OrderInfoCard(order = state.order!!)
                }
                
                // Search products
                item {
                    ProductSearchBar(
                        query = state.searchQuery,
                        onQueryChange = { viewModel.onEvent(EditOrderEvent.SearchProduct(it)) }
                    )
                }
                
                // Product items
                items(state.orderItems, key = { it.productId }) { item ->
                    ProductItemCard(
                        item = item,
                        onIncrementQuantity = {
                            viewModel.onEvent(EditOrderEvent.IncrementQuantity(item.productId))
                        },
                        onDecrementQuantity = {
                            viewModel.onEvent(EditOrderEvent.DecrementQuantity(item.productId))
                        },
                        onRemove = {
                            viewModel.onEvent(EditOrderEvent.RemoveProduct(item.productId))
                        }
                    )
                }
                
                // Footer with summary and actions
                item {
                    OrderFooter(
                        productLinesCount = state.getProductLinesCount(),
                        totalAmount = state.getFormattedTotalAmount(),
                        canConfirm = state.canConfirmOrder(),
                        isSaving = state.isSaving,
                        isDeleting = state.isDeleting,
                        onConfirm = { viewModel.onEvent(EditOrderEvent.ConfirmOrder) },
                        onDelete = { viewModel.onEvent(EditOrderEvent.ShowDeleteConfirmation) }
                    )
                }
                }
            }
            
            // Custom Snackbar in top-right corner
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 80.dp, end = 16.dp)
            ) { data ->
                Card(
                    modifier = Modifier.padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = data.visuals.message,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Customer information card
 */
@Composable
fun CustomerInfoCard(
    order: com.misw.medisupply.domain.model.order.Order,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Cliente ID: ${order.customerId}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121)
            )
            Text(
                text = order.deliveryAddress,
                fontSize = 12.sp,
                color = Color(0xFF757575)
            )
            Text(
                text = "${order.deliveryCity}, ${order.deliveryDepartment}",
                fontSize = 12.sp,
                color = Color(0xFF757575)
            )
        }
    }
}

/**
 * Order information card
 */
@Composable
fun OrderInfoCard(
    order: com.misw.medisupply.domain.model.order.Order,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Order number - centered and bold
            Text(
                text = order.orderNumber,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            // Order date - left aligned with bold label
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Fecha: ",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(order.orderDate),
                    fontSize = 13.sp,
                    color = Color(0xFF757575)
                )
            }
            
            // Order status - left aligned with bold label and value
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Estado: ",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    text = order.getStatusDisplayName(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }
        }
    }
}

/**
 * Product search bar
 */
@Composable
fun ProductSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Buscar por nombre o código de producto") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(8.dp)
    )
}

/**
 * Order footer with summary and action buttons
 */
@Composable
fun OrderFooter(
    productLinesCount: Int,
    totalAmount: String,
    canConfirm: Boolean,
    isSaving: Boolean,
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Product lines count
            Text(
                text = "$productLinesCount líneas de productos agregadas",
                fontSize = 14.sp,
                color = Color(0xFF757575)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Total amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    text = totalAmount,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Confirm button
            Button(
                onClick = onConfirm,
                enabled = canConfirm && !isSaving && !isDeleting,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isSaving) "Guardando..." else "Confirmar pedido",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Delete button
            OutlinedButton(
                onClick = onDelete,
                enabled = !isSaving && !isDeleting,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFF44336)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isDeleting) "Eliminando..." else "Eliminar pedido",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

/**
 * Info row helper component
 */
@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            fontSize = 13.sp,
            color = Color(0xFF757575),
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF212121),
            modifier = Modifier.weight(0.6f)
        )
    }
}

/**
 * Delete confirmation dialog
 */
@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Eliminar pedido",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("¿Estás seguro de que deseas eliminar este pedido? Esta acción no se puede deshacer.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFF44336)
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
