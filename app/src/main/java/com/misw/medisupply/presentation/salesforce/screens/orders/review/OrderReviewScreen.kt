package com.misw.medisupply.presentation.salesforce.screens.orders.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.order.CartItem
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
import com.misw.medisupply.presentation.salesforce.screens.orders.Mode
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.CartItemCard
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.CustomerSummaryCard
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.OrderSummaryCard
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.SectionTitle
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.dialogs.ConfirmOrderDialog
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.dialogs.DeleteOrderDialog
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.dialogs.ErrorDialog
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.dialogs.SuccessDialog
import com.misw.medisupply.presentation.salesforce.viewmodel.orders.OrderViewModel
import com.misw.medisupply.presentation.salesforce.viewmodel.orders.OrdersViewModel

/**
 * Order Review Screen
 * Third step - Review order details before final confirmation
 */
@Composable
fun OrderReviewScreen(
    customer: Customer,
    cartItems: Map<String, CartItem>,
    onNavigateBack: () -> Unit,
    onOrderSuccess: (String) -> Unit, // orderNumber
    mode: Mode = Mode.CREATE,
    orderId: String? = null,
    orderStatus: OrderStatus? = null,
    createViewModel: OrderViewModel = hiltViewModel(),
    editViewModel: OrdersViewModel = hiltViewModel()
) {
    // Use different ViewModel depending on mode
    val createState by createViewModel.state.collectAsState()
    val editState by editViewModel.state.collectAsState()
    
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // Determine which state and flags to use based on mode
    val isLoading = if (mode == Mode.EDIT) editState.isSaving || editState.isDeleting else createState.isLoading
    val error = if (mode == Mode.EDIT) editState.error else createState.error
    val successOrder = if (mode == Mode.EDIT) {
        // Use the updatedOrder directly from state
        editState.updatedOrder
    } else {
        createState.createdOrder
    }
    
    // Get order number for delete confirmation
    val orderNumber = if (mode == Mode.EDIT) orderId else null

    // Calculate totals
    val subtotal = cartItems.values.sumOf { it.calculateSubtotal().toDouble() }.toFloat()
    val itemCount = cartItems.values.sumOf { it.quantity }
    
    // Handle delete success
    LaunchedEffect(editState.deleteSuccessMessage) {
        if (editState.deleteSuccessMessage != null) {
            // Clear the state first to prevent re-triggering
            editViewModel.onEvent(com.misw.medisupply.presentation.salesforce.viewmodel.orders.OrdersEvent.ClearDeleteSuccess)
            // Navigate using onOrderSuccess which clears the backstack properly
            onOrderSuccess("DELETED")
        }
    }

    // Handle order success result
    if (successOrder != null && !showSuccessDialog) {
        showSuccessDialog = true
    }

    if (error != null && !showErrorDialog) {
        showErrorDialog = true
    }

    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = if (mode == Mode.EDIT) "Editar Pedido" else "Revisar Pedido",
                subtitle = "Fuerza de ventas - Medisupply",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Customer Information Section
            item {
                SectionTitle(text = "Información del Cliente")
                CustomerSummaryCard(customer = customer)
            }

            // Order Items Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionTitle(text = "Productos ($itemCount ${if (itemCount == 1) "item" else "items"})")
            }

            items(cartItems.values.toList()) { cartItem ->
                CartItemCard(cartItem = cartItem)
            }

            // Order Summary Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionTitle(text = "Resumen del Pedido")
                OrderSummaryCard(
                    subtotal = subtotal,
                    tax = 0f, // TODO: Calculate tax
                    total = subtotal
                )
            }

            // Action Buttons
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Confirm/Update button
                    Button(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Procesando...")
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (mode == Mode.EDIT) "Confirmar y Actualizar Pedido" else "Confirmar y Crear Pedido",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Cancel button
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        Text("Cancelar")
                    }
                    
                    // Delete button (only in EDIT mode for PENDING orders)
                    if (mode == Mode.EDIT && orderStatus == OrderStatus.PENDING) {
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Eliminar Pedido",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Confirmation Dialog
    if (showConfirmDialog) {
        ConfirmOrderDialog(
            isEditMode = mode == Mode.EDIT,
            onConfirm = {
                showConfirmDialog = false
                if (mode == Mode.EDIT && orderId != null) {
                    // Call updateOrder on OrdersViewModel
                    editViewModel.updateOrder()
                } else {
                    // Call createOrder on OrderViewModel
                    createViewModel.createOrder(
                        customer = customer,
                        cartItems = cartItems
                    )
                }
            },
            onDismiss = { showConfirmDialog = false }
        )
    }

    // Success Dialog
    if (showSuccessDialog && successOrder != null) {
        val orderNumber = successOrder.orderNumber ?: "N/A"
        SuccessDialog(
            orderNumber = orderNumber,
            message = if (mode == Mode.EDIT) "Pedido actualizado exitosamente" else "Pedido creado exitosamente",
            onDismiss = {
                showSuccessDialog = false
                if (mode == Mode.EDIT) {
                    editViewModel.clearSuccessMessage()
                } else {
                    createViewModel.resetState()
                }
                onOrderSuccess(orderNumber)
            }
        )
    }

    // Error Dialog
    if (showErrorDialog && error != null) {
        ErrorDialog(
            errorMessage = error,
            onDismiss = {
                showErrorDialog = false
                if (mode == Mode.EDIT) {
                    editViewModel.clearError()
                } else {
                    createViewModel.clearError()
                }
            }
        )
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        DeleteOrderDialog(
            orderNumber = orderNumber,
            onConfirm = {
                showDeleteDialog = false
                // Use the numeric ID from the state, not the string orderId parameter
                val numericOrderId = editState.orderIdEditingNumeric
                if (numericOrderId != null) {
                    android.util.Log.d("OrderReviewScreen", "Deleting order with ID: $numericOrderId")
                    editViewModel.onEvent(com.misw.medisupply.presentation.salesforce.viewmodel.orders.OrdersEvent.DeleteOrder(numericOrderId))
                } else {
                    android.util.Log.e("OrderReviewScreen", "Cannot delete: orderIdEditingNumeric is null")
                }
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}