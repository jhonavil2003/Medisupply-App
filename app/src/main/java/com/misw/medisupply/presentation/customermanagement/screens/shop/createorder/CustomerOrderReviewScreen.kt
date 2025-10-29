package com.misw.medisupply.presentation.customermanagement.screens.shop.createorder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.model.order.CartItem
import com.misw.medisupply.presentation.salesforce.screens.orders.review.OrderReviewScreen
import com.misw.medisupply.presentation.salesforce.viewmodel.orders.OrderViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Customer Order Review Screen
 * Reutiliza OrderReviewScreen pero agrega selección de fecha de entrega para clientes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOrderReviewScreen(
    cartItems: Map<String, CartItem>,
    onNavigateBack: () -> Unit,
    onOrderSuccess: (String) -> Unit, // orderNumber
    viewModel: OrderViewModel = hiltViewModel()
) {
    // Estado para la fecha de entrega seleccionada
    var selectedDeliveryDate by remember { 
        mutableStateOf(getDefaultDeliveryDate()) 
    }
    
    // Customer estático por ahora (customer_id = 1)
    val staticCustomer = Customer(
        id = 1,
        documentType = com.misw.medisupply.domain.model.customer.DocumentType.NIT,
        documentNumber = "900123456-1",
        businessName = "Hospital General San José",
        tradeName = "Hospital San José",
        customerType = CustomerType.HOSPITAL,
        contactName = "Dr. Juan Pérez",
        contactEmail = "admin@hospitalsanjose.com",
        contactPhone = "+57 1 234 5678",
        address = "Calle 10 # 5-25",
        city = "Bogotá",
        department = "Cundinamarca",
        country = "Colombia",
        creditLimit = 10000000.0,
        creditDays = 30,
        isActive = true,
        createdAt = null,
        updatedAt = null
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Sección específica para clientes: Selección de fecha de entrega
        CustomerDeliveryDateSection(
            selectedDate = selectedDeliveryDate,
            onDateSelected = { selectedDeliveryDate = it }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Por ahora usar el ViewModel original y loggear la fecha
        // En futuras versiones se podría crear un ViewModel específico para clientes
        OrderReviewScreen(
            customer = staticCustomer,
            cartItems = cartItems,
            onNavigateBack = onNavigateBack,
            onOrderSuccess = { orderNumber ->
                println("DEBUG: Orden $orderNumber creada con fecha de entrega preferida: $selectedDeliveryDate")
                // TODO: En versiones futuras, enviar la fecha al backend
                onOrderSuccess(orderNumber)
            },
            createViewModel = viewModel
        )
    }
}



/**
 * Sección específica para clientes para seleccionar fecha de entrega
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomerDeliveryDateSection(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Initialize DatePicker with current selected date
    val initialDateMillis = remember(selectedDate) {
        try {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatter.parse(selectedDate)?.time
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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
                text = "Fecha de Entrega Preferida",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        println("DEBUG: DatePicker clicked!")
                        showDatePicker = true 
                    }
            ) {
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = { },
                    label = { Text("Selecciona fecha de entrega") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Calendario"
                        )
                    },
                    readOnly = true,
                    enabled = false, // Disable to prevent internal interactions
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledIndicatorColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "La fecha de entrega está sujeta a disponibilidad y políticas de distribución",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    
    // DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            onDateSelected(formatter.format(Date(millis)))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Seleccionar fecha de entrega",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        }
    }
}

/**
 * Genera fecha de entrega por defecto (3 días hábiles desde hoy)
 */
private fun getDefaultDeliveryDate(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_MONTH, 3) // 3 días por defecto
    
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(calendar.time)
}