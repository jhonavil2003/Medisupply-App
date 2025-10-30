package com.misw.medisupply.presentation.customermanagement.screens.shop

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.misw.medisupply.core.utils.FormatUtils
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.presentation.common.components.ErrorView
import com.misw.medisupply.presentation.customermanagement.viewmodel.CustomerShopViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Shop Screen
 * Pantalla de compras - muestra historial y permite crear pedidos
 */
@Composable
fun ShopScreen(
    onNavigateToCreateOrder: () -> Unit = {},
    viewModel: CustomerShopViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Botón Crear
        CreateOrderButton(onNavigateToCreateOrder)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Historial de compras
        HistorySection(
            orders = uiState.orders,
            isLoading = uiState.isLoading,
            error = uiState.error,
            onRetry = { viewModel.retryLoadOrders() },
            ordersToShow = uiState.ordersToShow,
            availablePageSizes = uiState.availablePageSizes,
            onChangePageSize = { newSize -> viewModel.changeOrdersToShow(newSize) },
            getPageSizeDisplayText = { size -> viewModel.getPageSizeDisplayText(size) }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Productos rechazados (mock data como en la imagen)
        RejectedProductsSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Condiciones pactadas
        AgreementConditionsSection()
    }
}

/**
 * Botón para crear nuevos pedidos
 */
@Composable
private fun CreateOrderButton(onNavigateToCreateOrder: () -> Unit) {
    Button(
        onClick = onNavigateToCreateOrder,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Crear",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Sección de historial de compras
 */
@Composable
private fun HistorySection(
    orders: List<Order>,
    isLoading: Boolean,
    error: String?,
    ordersToShow: Int,
    availablePageSizes: List<Int>,
    onRetry: () -> Unit,
    onChangePageSize: (Int) -> Unit,
    getPageSizeDisplayText: (Int) -> String
) {
    Column {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Historial de compras",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                var showDropdown by remember { mutableStateOf(false) }
                
                Text(
                    text = "Mostrar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showDropdown = true }
                    ) {
                        Text(
                            text = getPageSizeDisplayText(ordersToShow),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dropdown",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { showDropdown = false }
                    ) {
                        availablePageSizes.forEach { size ->
                            DropdownMenuItem(
                                text = { 
                                    Text(getPageSizeDisplayText(size))
                                },
                                onClick = {
                                    onChangePageSize(size)
                                    showDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Content
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                ErrorView(
                    message = error,
                    onRetry = onRetry
                )
            }
            orders.isEmpty() -> {
                EmptyOrdersView()
            }
            else -> {
                val ordersToDisplay = if (ordersToShow == -1) orders else orders.take(ordersToShow)
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ordersToDisplay) { order ->
                        OrderCard(order = order)
                    }
                }
            }
        }
    }
}

/**
 * Card de orden individual
 */
@Composable
private fun OrderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Assignment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${order.orderNumber ?: "N/A"} - ${formatDate(order.orderDate)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Total: ${formatCurrency(order.totalAmount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Vista cuando no hay órdenes
 */
@Composable
private fun EmptyOrdersView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No tienes compras aún",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Crea tu primera orden tocando el botón Crear",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Sección de productos rechazados (mock data como en la imagen)
 */
@Composable
private fun RejectedProductsSection() {
    Column {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Productos rechazados",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mostrar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "3",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Mock data como en la imagen
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RejectedProductCard(
                productName = "Guantes de nitrilo M",
                quantity = 5
            )
            RejectedProductCard(
                productName = "Termómetro digital T-200",
                quantity = 20
            )
        }
    }
}

/**
 * Card de producto rechazado
 */
@Composable
private fun RejectedProductCard(
    productName: String,
    quantity: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Cantidad: $quantity",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Sección de condiciones pactadas
 */
@Composable
private fun AgreementConditionsSection() {
    Column {
        Text(
            text = "Condiciones pactadas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ConditionCard(
                title = "Descuento:",
                value = "8% hasta 15/dic/25"
            )
            ConditionCard(
                title = "Plazo pago:",
                value = "30 días"
            )
            ConditionCard(
                title = "Impuestos:",
                value = "19% IVA incluido"
            )
        }
    }
}

/**
 * Card de condición
 */
@Composable
private fun ConditionCard(
    title: String,
    value: String
) {
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
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Formatear fecha
 */
private fun formatDate(date: java.util.Date?): String {
    return if (date != null) {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    } else {
        "N/A"
    }
}

/**
 * Formatear moneda
 */
private fun formatCurrency(amount: Double): String {
    return FormatUtils.formatCurrency(amount, decimals = 0)
}

