package com.misw.medisupply.presentation.customermanagement.screens.shop.createorder

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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.model.order.CartItem
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
import com.misw.medisupply.presentation.salesforce.screens.orders.review.OrderReviewScreen
import com.misw.medisupply.presentation.salesforce.viewmodel.orders.OrderViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.CartItemCard
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.CustomerSummaryCard
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.OrderSummaryCard
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.SectionTitle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
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

    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = "Revisar pedido",
                subtitle = "Compras - Medisupply",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sección específica para clientes: Selección de fecha de entrega
            CustomerDeliveryDateSection(
                selectedDate = selectedDeliveryDate,
                onDateSelected = { selectedDeliveryDate = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // SOLUCIÓN TEMPORAL: Extraer el contenido de OrderReviewScreen sin su Scaffold
            // Para evitar el AppBar duplicado, creamos el contenido directamente aquí
            OrderReviewContent(
                customer = staticCustomer,
                cartItems = cartItems,
                onOrderSuccess = { orderNumber ->
                    println("DEBUG: Orden $orderNumber creada con fecha de entrega preferida: $selectedDeliveryDate")
                    // TODO: En versiones futuras, enviar la fecha al backend
                    onOrderSuccess(orderNumber)
                },
                viewModel = viewModel
            )
        }
    }
}



/**
 * Sección específica para clientes para seleccionar fecha de entrega
 * Usa el mismo estilo visual que CreateVisitScreen
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
            val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            formatter.timeZone = java.util.TimeZone.getDefault()
            val date = formatter.parse(selectedDate)
            // Add timezone offset to ensure correct date display
            date?.time?.plus(java.util.TimeZone.getDefault().getOffset(date.time))
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Fecha de Entrega Preferida",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color(0xFF1565C0)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Mensaje informativo con el mismo estilo que CreateVisitScreen
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFFE3F2FD)
                )
            ) {
                Text(
                    text = "📦 Selecciona tu fecha de entrega preferida. La confirmación está sujeta a disponibilidad y políticas de distribución.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color(0xFF1565C0)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Date Picker con el mismo estilo que CreateVisitScreen
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
                    label = { Text("Fecha de entrega") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Seleccionar fecha",
                            tint = androidx.compose.ui.graphics.Color(0xFF1565C0)
                        )
                    },
                    readOnly = true,
                    enabled = false, // Disable to prevent internal interactions
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = androidx.compose.ui.graphics.Color(0xFF1565C0),
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFFB6C6E3),
                        focusedLabelColor = androidx.compose.ui.graphics.Color(0xFF1565C0),
                        unfocusedLabelColor = androidx.compose.ui.graphics.Color(0xFF1565C0),
                        disabledBorderColor = androidx.compose.ui.graphics.Color(0xFFB6C6E3),
                        disabledLabelColor = androidx.compose.ui.graphics.Color(0xFF1565C0),
                        disabledTextColor = androidx.compose.ui.graphics.Color(0xFF000000)
                    )
                )
            }
        }
    }
    
    // DatePicker Dialog con el mismo estilo que CreateVisitScreen
    if (showDatePicker) {
        CustomDatePickerDialog(
            onDateSelected = { dateMillis ->
                dateMillis?.let { millis ->
                    // Fix timezone issue: Create date using Calendar to avoid UTC conversion
                    val calendar = java.util.Calendar.getInstance()
                    calendar.timeInMillis = millis
                    val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    formatter.timeZone = java.util.TimeZone.getDefault()
                    onDateSelected(formatter.format(calendar.time))
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
            datePickerState = datePickerState
        )
    }
}

/**
 * Custom Date Picker Dialog - Copiado de CreateVisitScreen para mantener consistencia
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomDatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    datePickerState: androidx.compose.material3.DatePickerState
) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onDateSelected(datePickerState.selectedDateMillis) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

/**
 * Contenido de OrderReview sin el Scaffold para evitar AppBar duplicado
 */
@Composable
private fun OrderReviewContent(
    customer: Customer,
    cartItems: Map<String, CartItem>,
    onOrderSuccess: (String) -> Unit,
    viewModel: OrderViewModel
) {
    val state by viewModel.state.collectAsState()

    // Calculate totals
    val subtotal = cartItems.values.sumOf { it.calculateSubtotal().toDouble() }.toFloat()
    val itemCount = cartItems.values.sumOf { it.quantity }

    // Handle order success result
    LaunchedEffect(state.createdOrder) {
        state.createdOrder?.let { order ->
            order.orderNumber?.let { orderNumber ->
                onOrderSuccess(orderNumber)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
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
                tax = 0f,
                total = subtotal
            )
        }

        // Create Order Button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    viewModel.createOrder(
                        customer = customer,
                        cartItems = cartItems
                    )
                },
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
                    Text("Creando pedido...")
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
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * Genera fecha de entrega por defecto (3 días hábiles desde hoy)
 */
private fun getDefaultDeliveryDate(): String {
    val calendar = java.util.Calendar.getInstance()
    calendar.add(java.util.Calendar.DAY_OF_MONTH, 3) // 3 días por defecto
    
    val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
    return formatter.format(calendar.time)
}