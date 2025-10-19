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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.order.CartItem
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.CartItemCard
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.CustomerSummaryCard
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.OrderSummaryCard
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.SectionTitle
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.dialogs.ConfirmOrderDialog
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.dialogs.ErrorDialog
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.dialogs.SuccessDialog
import com.misw.medisupply.presentation.salesforce.viewmodel.orders.OrderViewModel

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
    viewModel: OrderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // Calculate totals
    val subtotal = cartItems.values.sumOf { it.calculateSubtotal().toDouble() }.toFloat()
    val itemCount = cartItems.values.sumOf { it.quantity }

    // Handle order creation result
    if (state.createdOrder != null && !showSuccessDialog) {
        showSuccessDialog = true
    }

    if (state.error != null && !showErrorDialog) {
        showErrorDialog = true
    }

    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = "Revisar Pedido",
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
                    // Confirm button
                    Button(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
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
                                text = "Confirmar y Crear Pedido",
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
                        enabled = !state.isLoading
                    ) {
                        Text("Cancelar")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Confirmation Dialog
    if (showConfirmDialog) {
        ConfirmOrderDialog(
            onConfirm = {
                showConfirmDialog = false
                viewModel.createOrder(
                    customer = customer,
                    cartItems = cartItems
                )
            },
            onDismiss = { showConfirmDialog = false }
        )
    }

    // Success Dialog
    if (showSuccessDialog && state.createdOrder != null) {
        val orderNumber = state.createdOrder!!.orderNumber ?: "N/A"
        SuccessDialog(
            orderNumber = orderNumber,
            onDismiss = {
                showSuccessDialog = false
                viewModel.resetState()
                onOrderSuccess(orderNumber)
            }
        )
    }

    // Error Dialog
    if (showErrorDialog && state.error != null) {
        ErrorDialog(
            errorMessage = state.error!!,
            onDismiss = {
                showErrorDialog = false
                viewModel.clearError()
            }
        )
    }
}
