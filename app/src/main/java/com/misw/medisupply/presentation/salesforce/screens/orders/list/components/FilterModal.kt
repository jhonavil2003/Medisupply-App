package com.misw.medisupply.presentation.salesforce.screens.orders.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import com.misw.medisupply.R
import com.misw.medisupply.core.i18n.LocaleManager
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.presentation.components.localizedStringResource
import com.misw.medisupply.presentation.salesforce.screens.orders.list.DateRange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Modal for filtering orders by status, customer, and date range
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterModal(
    selectedStatus: OrderStatus?,
    selectedCustomerId: Int?,
    selectedDateRange: DateRange?,
    customers: List<Customer>,
    localeManager: LocaleManager,
    onStatusSelected: (OrderStatus?) -> Unit,
    onCustomerSelected: (Int?) -> Unit,
    onDateRangeSelected: (DateRange?) -> Unit,
    onApplyFilters: () -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = "Filtros de Pedidos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            ) 
        },
        text = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                
                    // Status Filter
                    Column {
                        Text(
                            text = "Estado del pedido",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        StatusDropdown(
                            selectedStatus = selectedStatus,
                            onStatusSelected = onStatusSelected,
                            localeManager = localeManager
                        )
                    }
                    
                    HorizontalDivider(color = Color(0xFF1565C0).copy(alpha = 0.3f))
                    
                    // Customer Filter
                    Column {
                        Text(
                            text = "Cliente",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        CustomerDropdown(
                            selectedCustomerId = selectedCustomerId,
                            customers = customers,
                            onCustomerSelected = onCustomerSelected,
                            localeManager = localeManager
                        )
                    }
                    
                    HorizontalDivider(color = Color(0xFF1565C0).copy(alpha = 0.3f))
                    
                    // Date Range Filter
                    Column {
                        Text(
                            text = "Rango de fechas",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        DateRangeSelector(
                            selectedDateRange = selectedDateRange,
                            onDateRangeSelected = onDateRangeSelected,
                            localeManager = localeManager
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApplyFilters()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Aplicar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    onClearFilters()
                    onDismiss()
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Limpiar")
            }
        }
    )
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF424242)
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun StatusDropdown(
    selectedStatus: OrderStatus?,
    onStatusSelected: (OrderStatus?) -> Unit,
    localeManager: LocaleManager
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clickable { expanded = true },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedStatus?.let { getStatusDisplayName(it, localeManager) }
                        ?: localizedStringResource(R.string.orders_all_statuses, localeManager),
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color(0xFF757575)
                )
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            DropdownMenuItem(
                text = { Text(localizedStringResource(R.string.orders_all_statuses, localeManager)) },
                onClick = {
                    onStatusSelected(null)
                    expanded = false
                }
            )
            OrderStatus.entries.forEach { status ->
                DropdownMenuItem(
                    text = { Text(getStatusDisplayName(status, localeManager)) },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CustomerDropdown(
    selectedCustomerId: Int?,
    customers: List<Customer>,
    onCustomerSelected: (Int?) -> Unit,
    localeManager: LocaleManager
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCustomer = customers.find { it.id == selectedCustomerId }
    
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clickable { expanded = true },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedCustomer?.getDisplayName()
                        ?: "Todos los clientes",
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color(0xFF757575)
                )
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            DropdownMenuItem(
                text = { Text("Todos los clientes") },
                onClick = {
                    onCustomerSelected(null)
                    expanded = false
                }
            )
            customers.forEach { customer ->
                DropdownMenuItem(
                    text = { Text(customer.getDisplayName()) },
                    onClick = {
                        onCustomerSelected(customer.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRangeSelector(
    selectedDateRange: DateRange?,
    onDateRangeSelected: (DateRange?) -> Unit,
    localeManager: LocaleManager
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(selectedDateRange?.startDate) }
    var endDate by remember { mutableStateOf(selectedDateRange?.endDate) }
    
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Start Date
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable { showStartDatePicker = true },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color(0xFF757575),
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Fecha desde",
                        fontSize = 12.sp,
                        color = Color(0xFF757575),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = startDate?.let { dateFormatter.format(it) }
                            ?: "Seleccionar fecha de inicio",
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }
            }
        }
        
        // End Date
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable { showEndDatePicker = true },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color(0xFF757575),
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Fecha hasta",
                        fontSize = 12.sp,
                        color = Color(0xFF757575),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = endDate?.let { dateFormatter.format(it) }
                            ?: "Seleccionar fecha final",
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }
            }
        }
        
        // Clear button
        if (selectedDateRange != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = {
                        startDate = null
                        endDate = null
                        onDateRangeSelected(null)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar fechas",
                        tint = Color(0xFF757575),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("Limpiar fechas")
                }
            }
        }
    }
    
    // Update date range when both dates are selected
    if (startDate != null && endDate != null) {
        onDateRangeSelected(DateRange(startDate!!, endDate!!))
    }
    
    // Start Date Picker
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            startDate = Date(it)
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // End Date Picker
    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            endDate = Date(it)
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun getStatusDisplayName(status: OrderStatus, localeManager: LocaleManager): String {
    return when (status) {
        OrderStatus.PENDING -> localizedStringResource(R.string.order_status_pending, localeManager)
        OrderStatus.CONFIRMED -> localizedStringResource(R.string.order_status_confirmed, localeManager)
        OrderStatus.PROCESSING -> localizedStringResource(R.string.order_status_processing, localeManager)
        OrderStatus.SHIPPED -> localizedStringResource(R.string.order_status_shipped, localeManager)
        OrderStatus.DELIVERED -> localizedStringResource(R.string.order_status_delivered, localeManager)
        OrderStatus.CANCELLED -> localizedStringResource(R.string.order_status_cancelled, localeManager)
    }
}