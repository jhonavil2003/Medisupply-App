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
import androidx.compose.foundation.layout.PaddingValues
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
import com.misw.medisupply.presentation.components.localizedStringResource
import com.misw.medisupply.R
import com.misw.medisupply.presentation.salesforce.screens.orders.review.OrderReviewScreen
import com.misw.medisupply.presentation.salesforce.viewmodel.orders.OrderViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.CartItemCard
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.CustomerSummaryCard
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.OrderSummaryCard
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.SectionTitle
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.dialogs.ConfirmOrderDialog
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.dialogs.ErrorDialog
import com.misw.medisupply.presentation.salesforce.screens.orders.review.components.dialogs.SuccessDialog
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
        latitude = null,
        longitude = null,
        creditLimit = 10000000.0,
        creditDays = 30,
        isActive = true,
        createdAt = null,
        updatedAt = null,
        salespersonId = null,
        salesperson = null
    )

    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = localizedStringResource(R.string.order_review_title, viewModel.localeManager),
                subtitle = localizedStringResource(R.string.order_review_subtitle, viewModel.localeManager),
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        CustomerOrderReviewContent(
            customer = staticCustomer,
            cartItems = cartItems,
            selectedDeliveryDate = selectedDeliveryDate,
            onDateSelected = { selectedDeliveryDate = it },
            onOrderSuccess = { orderNumber ->
                println("DEBUG: Orden $orderNumber creada con fecha de entrega preferida: $selectedDeliveryDate")
                onOrderSuccess(orderNumber)
            },
            viewModel = viewModel,
            paddingValues = paddingValues
        )
    }
}

/**
 * Contenido combinado del review del pedido con selección de fecha de entrega
 */
@Composable
private fun CustomerOrderReviewContent(
    customer: Customer,
    cartItems: Map<String, CartItem>,
    selectedDeliveryDate: String,
    onDateSelected: (String) -> Unit,
    onOrderSuccess: (String) -> Unit,
    viewModel: OrderViewModel,
    paddingValues: PaddingValues
) {
    val state by viewModel.state.collectAsState()
    
    // Pre-calculate localized strings for dialogs
    val localeManager = viewModel.localeManager
    val notAvailableText = localizedStringResource(R.string.label_not_available, localeManager)
    val orderSuccessText = localizedStringResource(R.string.order_created_success, localeManager)
    val unknownErrorText = localizedStringResource(R.string.error_unknown, localeManager)
    
    // Dialog states
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // Calculate totals
    val subtotal = cartItems.values.sumOf { it.calculateSubtotal().toDouble() }.toFloat()
    val itemCount = cartItems.values.sumOf { it.quantity }

    // Handle order success result
    LaunchedEffect(state.createdOrder) {
        if (state.createdOrder != null && !showSuccessDialog) {
            showSuccessDialog = true
        }
    }

    // Handle error result
    LaunchedEffect(state.error) {
        if (state.error != null && !showErrorDialog) {
            showErrorDialog = true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Customer Delivery Date Section
        item {
            SectionTitle(text = localizedStringResource(R.string.delivery_date_title, viewModel.localeManager))
            Spacer(modifier = Modifier.height(8.dp))
            CustomerDeliveryDateSection(
                selectedDate = selectedDeliveryDate,
                onDateSelected = onDateSelected,
                viewModel = viewModel
            )
        }
        
        // Customer Information Section
        item {
            SectionTitle(text = localizedStringResource(R.string.customer_info_section, viewModel.localeManager))
            Spacer(modifier = Modifier.height(8.dp))
            CustomerSummaryCard(customer = customer)
        }

        // Order Items Section
        item {
            val itemText = if (itemCount == 1) {
                localizedStringResource(R.string.product_item, viewModel.localeManager)
            } else {
                localizedStringResource(R.string.product_items, viewModel.localeManager)
            }
            SectionTitle(
                text = String.format(
                    localizedStringResource(R.string.products_section, viewModel.localeManager),
                    itemCount,
                    itemText
                )
            )
        }

        items(cartItems.values.toList()) { cartItem ->
            CartItemCard(cartItem = cartItem)
        }

        // Order Summary Section
        item {
            SectionTitle(text = localizedStringResource(R.string.order_summary_section, viewModel.localeManager))
            Spacer(modifier = Modifier.height(8.dp))
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
                    showConfirmDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(localizedStringResource(R.string.creating_order, viewModel.localeManager))
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = localizedStringResource(R.string.button_confirm_order, viewModel.localeManager),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
    
    // Dialogs
    if (showConfirmDialog) {
        ConfirmOrderDialog(
            isEditMode = false,
            localeManager = localeManager,
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
    
    if (showSuccessDialog) {
        state.createdOrder?.let { order ->
            SuccessDialog(
                orderNumber = order.orderNumber ?: notAvailableText,
                message = orderSuccessText,
                localeManager = localeManager,
                onDismiss = { 
                    showSuccessDialog = false
                    order.orderNumber?.let { orderNumber ->
                        onOrderSuccess(orderNumber)
                    }
                }
            )
        }
    }
    
    if (showErrorDialog) {
        ErrorDialog(
            errorMessage = state.error ?: unknownErrorText,
            onDismiss = { 
                showErrorDialog = false 
                viewModel.clearError()
            }
        )
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
    onDateSelected: (String) -> Unit,
    viewModel: OrderViewModel
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Initialize DatePicker with current selected date
    val initialDateMillis = remember(selectedDate) {
        try {
            val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val date = formatter.parse(selectedDate)
            
            // Convert to UTC milliseconds for DatePicker
            date?.let { parsedDate ->
                val calendar = java.util.Calendar.getInstance()
                calendar.time = parsedDate
                
                val utcCalendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
                utcCalendar.set(
                    calendar.get(java.util.Calendar.YEAR),
                    calendar.get(java.util.Calendar.MONTH),
                    calendar.get(java.util.Calendar.DAY_OF_MONTH),
                    12, 0, 0 // Set to noon to avoid timezone edge cases
                )
                utcCalendar.set(java.util.Calendar.MILLISECOND, 0)
                utcCalendar.timeInMillis
            }
        } catch (e: Exception) {
            // Default to today at noon UTC
            val utcCalendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
            utcCalendar.set(java.util.Calendar.HOUR_OF_DAY, 12)
            utcCalendar.set(java.util.Calendar.MINUTE, 0)
            utcCalendar.set(java.util.Calendar.SECOND, 0)
            utcCalendar.set(java.util.Calendar.MILLISECOND, 0)
            utcCalendar.timeInMillis
        }
    }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Mensaje informativo con el mismo estilo que CreateVisitScreen
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFFE3F2FD)
                )
            ) {
                Text(
                    text = localizedStringResource(R.string.delivery_date_info, viewModel.localeManager),
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
                    label = { Text(localizedStringResource(R.string.delivery_date_label, viewModel.localeManager)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = localizedStringResource(R.string.delivery_date_select, viewModel.localeManager),
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
                    // Use UTC to avoid timezone conversion issues
                    val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
                    calendar.timeInMillis = millis
                    
                    // Format using local timezone but with UTC input
                    val localCalendar = java.util.Calendar.getInstance()
                    localCalendar.set(
                        calendar.get(java.util.Calendar.YEAR),
                        calendar.get(java.util.Calendar.MONTH),
                        calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    )
                    
                    val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    onDateSelected(formatter.format(localCalendar.time))
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
            datePickerState = datePickerState,
            viewModel = viewModel
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
    datePickerState: androidx.compose.material3.DatePickerState,
    viewModel: OrderViewModel
) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onDateSelected(datePickerState.selectedDateMillis) }) {
                Text(localizedStringResource(R.string.button_ok, viewModel.localeManager))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(localizedStringResource(R.string.button_cancel, viewModel.localeManager))
            }
        }
    ) {
        DatePicker(state = datePickerState)
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