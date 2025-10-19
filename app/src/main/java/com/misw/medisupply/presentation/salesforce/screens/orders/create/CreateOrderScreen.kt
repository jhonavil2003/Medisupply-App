package com.misw.medisupply.presentation.salesforce.screens.orders.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
import com.misw.medisupply.presentation.salesforce.viewmodel.orders.OrdersViewModel

// UI Constants
private val DROPDOWN_TOP_OFFSET = 140.dp
private val DROPDOWN_MAX_HEIGHT = 300.dp

/**
 * Create Order Screen
 * Screen for creating a new order by selecting a customer
 */
@Composable
fun CreateOrderScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProductSelection: (Customer) -> Unit,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    
    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = "Crear Pedido",
                subtitle = "Fuerza de ventas - Medisupply",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top
            ) {
                CustomerSelectionCard(
                    customers = state.customers,
                    selectedCustomer = selectedCustomer,
                    onCustomerSelected = { selectedCustomer = it },
                    onCreateOrder = {
                        selectedCustomer?.let { customer ->
                            onNavigateToProductSelection(customer)
                        }
                    },
                    modifier = Modifier.zIndex(1f)
                )
            }
        }
    }
}

/**
 * Customer selection component with dropdown
 * Features:
 * - Displays customer title card
 * - Custom dropdown with overlay to prevent layout shifts
 * - Enables order creation only when customer is selected
 */
@Composable
private fun CustomerSelectionCard(
    customers: List<Customer>,
    selectedCustomer: Customer?,
    onCustomerSelected: (Customer) -> Unit,
    onCreateOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Customer title section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = "Cliente",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Customer selection field
            OutlinedTextField(
                value = selectedCustomer?.getDisplayName() ?: "Seleccionar cliente",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                label = { Text("Cliente") },
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Seleccionar cliente",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = Color.Transparent
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Create order button
            Button(
                onClick = onCreateOrder,
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedCustomer != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Crear",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
        
        // Floating dropdown - overlays content to prevent layout shifts
        if (expanded) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = DROPDOWN_TOP_OFFSET)
                    .heightIn(max = DROPDOWN_MAX_HEIGHT)
                    .zIndex(10f),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                if (customers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "No hay clientes disponibles",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(customers) { customer ->
                            CustomerDropdownItem(
                                customer = customer,
                                onSelect = {
                                    onCustomerSelected(customer)
                                    expanded = false
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
 * Individual customer item in dropdown list
 */
@Composable
private fun CustomerDropdownItem(
    customer: Customer,
    onSelect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(16.dp)
    ) {
        Text(
            text = customer.getDisplayName(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = customer.customerType.displayName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
