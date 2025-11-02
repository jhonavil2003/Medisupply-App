package com.misw.medisupply.presentation.salesforce.screens.visits.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.util.Calendar
import java.time.Instant
import java.time.ZoneId
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
import com.misw.medisupply.presentation.salesforce.screens.visits.state.CreateVisitUiState
import com.misw.medisupply.presentation.salesforce.screens.visits.viewmodel.CreateVisitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVisitScreen(
    onNavigateBack: (() -> Unit)? = null,
    viewModel: com.misw.medisupply.presentation.salesforce.screens.visits.viewmodel.CreateVisitViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Datos", "Ubicación", "Archivos")
    val uiState by viewModel.uiState.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = "Crear visita",
                subtitle = "Visitas - Medisupply",
                onNavigateBack = onNavigateBack
            )
        },
        snackbarHost = { 
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = if (data.visuals.message.startsWith("✅")) {
                            Color(0xFF4CAF50) // Verde para éxito
                        } else {
                            MaterialTheme.colorScheme.errorContainer // Rojo para error
                        },
                        contentColor = if (data.visuals.message.startsWith("✅")) {
                            Color.White
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        }
                    )
                }
            )
        },
        containerColor = Color(0xFFF5F5F5),
        floatingActionButton = {
            if (!uiState.isVisitSaved) { // Solo mostrar el FAB si no se ha guardado aún
                ExtendedFloatingActionButton(
                    text = { 
                        Text(
                            if (uiState.isSaving) "Guardando..." else "Guardar visita",
                            color = Color.White
                        )
                    },
                    icon = { 
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Save, 
                                contentDescription = "Guardar visita",
                                tint = Color.White
                            )
                        }
                    },
                    onClick = { viewModel.saveVisit() },
                    containerColor = if (uiState.isFormValid && !uiState.isSaving) 
                        Color(0xFF1565C0) else Color(0xFFBDBDBD)
                )
            }
        }
    ) { paddingValues ->
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        var showDatePicker by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }
        
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.visitDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        )
        val timePickerState = rememberTimePickerState(
            initialHour = uiState.visitTime.hour,
            initialMinute = uiState.visitTime.minute,
            is24Hour = true
        )

        // Sincronizar datePickerState cuando cambie la fecha del ViewModel
        LaunchedEffect(uiState.visitDate) {
            datePickerState.selectedDateMillis = uiState.visitDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        }

        // Manejar éxito del guardado
        LaunchedEffect(uiState.saveSuccess) {
            if (uiState.saveSuccess) {
                // Mostrar mensaje de éxito
                snackbarHostState.showSnackbar(
                    message = "✅ ¡Visita guardada exitosamente! Ahora puedes completar ubicación y archivos.",
                    duration = SnackbarDuration.Long
                )
                // Cambiar al tab de ubicación automáticamente después de guardar
                selectedTabIndex = 1
                viewModel.clearSuccess()
            }
        }
        
        // Manejar mensajes de éxito personalizados
        uiState.successMessage?.let { successMessage ->
            LaunchedEffect(successMessage) {
                snackbarHostState.showSnackbar(
                    message = "✅ $successMessage",
                    duration = SnackbarDuration.Long
                )
                viewModel.clearSuccess()
            }
        }

        // Mostrar SnackBar para errores
        uiState.error?.let { error ->
            LaunchedEffect(error) {
                snackbarHostState.showSnackbar(
                    message = "❌ $error",
                    duration = SnackbarDuration.Long
                )
                viewModel.clearError()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color(0xFF1565C0)
            ) {
                tabTitles.forEachIndexed { index, title ->
                    val isTabEnabled = when (index) {
                        0 -> true // "Datos" siempre habilitado
                        1 -> uiState.isVisitSaved // "Ubicación" solo después de guardar
                        2 -> uiState.isVisitSaved // "Archivos" solo después de guardar
                        else -> false
                    }
                    
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { 
                            if (isTabEnabled) {
                                selectedTabIndex = index 
                            }
                        },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (isTabEnabled) {
                                    if (selectedTabIndex == index) Color(0xFF1565C0) else Color(0xFF757575)
                                } else {
                                    Color(0xFFBDBDBD)
                                }
                            )
                        },
                        enabled = isTabEnabled
                    )
                }
            }
            
            // Tab Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> DatosTabContent(
                        uiState = uiState,
                        dateFormatter = dateFormatter,
                        timeFormatter = timeFormatter,
                        showDatePicker = showDatePicker,
                        showTimePicker = showTimePicker,
                        datePickerState = datePickerState,
                        timePickerState = timePickerState,
                        onShowDatePicker = { showDatePicker = it },
                        onShowTimePicker = { showTimePicker = it },
                        onDateChange = viewModel::updateVisitDate,
                        onTimeChange = viewModel::updateVisitTime,
                        onCustomerSearchQueryChange = viewModel::searchCustomers,
                        onCustomerSelected = viewModel::selectCustomer,
                        onClearCustomerSelection = viewModel::clearCustomerSelection,
                        onContactedPersonsChange = viewModel::updateContactedPersons,
                        onClinicalFindingsChange = viewModel::updateClinicalFindings,
                        onAdditionalNotesChange = viewModel::updateAdditionalNotes
                    )
                    1 -> UbicacionTabContent(
                        uiState = uiState,
                        onAddressChange = viewModel::updateAddress
                    )
                    2 -> com.misw.medisupply.presentation.salesforce.screens.visits.components.ArchivosTabContent(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
                Spacer(Modifier.height(80.dp)) // Espacio para el FAB
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatosTabContent(
    uiState: com.misw.medisupply.presentation.salesforce.screens.visits.state.CreateVisitUiState,
    dateFormatter: DateTimeFormatter,
    timeFormatter: DateTimeFormatter,
    showDatePicker: Boolean,
    showTimePicker: Boolean,
    datePickerState: androidx.compose.material3.DatePickerState,
    timePickerState: TimePickerState,
    onShowDatePicker: (Boolean) -> Unit,
    onShowTimePicker: (Boolean) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    onCustomerSearchQueryChange: (String) -> Unit,
    onCustomerSelected: (com.misw.medisupply.domain.model.customer.Customer) -> Unit,
    onClearCustomerSelection: () -> Unit,
    onContactedPersonsChange: (String) -> Unit,
    onClinicalFindingsChange: (String) -> Unit,
    onAdditionalNotesChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Información de la visita", 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )
            
            // Indicador de progreso o mensaje de éxito
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isVisitSaved) {
                        Color(0xFFE8F5E8) // Verde para éxito
                    } else if (uiState.isCustomerSelected) {
                        Color(0xFFE3F2FD) // Azul para en progreso
                    } else {
                        Color(0xFFFFF3E0) // Naranja para comenzar
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (uiState.isVisitSaved) {
                                "✅ Visita guardada exitosamente. Ve a las pestañas 'Ubicación' y 'Archivos' para completar información adicional."
                            } else if (!uiState.isCustomerSelected) {
                                "1️⃣ Selecciona un cliente para comenzar"
                            } else if (uiState.isFormValid) {
                                "3️⃣ ¡Listo para guardar! Cliente, fecha y hora confirmados."
                            } else if (uiState.isCustomerSelected && !uiState.hasModifiedDate) {
                                "2️⃣ Confirma la fecha de la visita (toca el campo de fecha)"
                            } else if (uiState.isCustomerSelected && !uiState.hasModifiedTime) {
                                "2️⃣ Confirma la hora de la visita (toca el campo de hora)"
                            } else {
                                "2️⃣ Confirma la fecha y hora de la visita"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (uiState.isVisitSaved) Color(0xFF2E7D32) else Color(0xFF1565C0)
                        )
                        
                        // Mostrar indicador de auto-guardado cuando está editando
                        if (uiState.isVisitSaved && uiState.isSaving) {
                            Spacer(Modifier.height(4.dp))
                            Row(
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    color = Color(0xFF1565C0),
                                    strokeWidth = 1.5.dp
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Guardando cambios...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF1565C0)
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // 1. PRIMERO: Selección de cliente
            if (!uiState.isVisitSaved) {
                // Campo de búsqueda y selección (antes de guardar)
                com.misw.medisupply.presentation.salesforce.screens.visits.components.CustomerSearchField(
                    selectedCustomer = uiState.selectedCustomer,
                    searchQuery = uiState.customerSearchQuery,
                    searchResults = uiState.customerSearchResults,
                    isSearching = uiState.isSearchingCustomers,
                    showDropdown = uiState.showCustomerDropdown,
                    onQueryChange = onCustomerSearchQueryChange,
                    onCustomerSelected = onCustomerSelected,
                    onClearSelection = onClearCustomerSelection,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Información del cliente guardado (después de guardar)
                uiState.selectedCustomer?.let { customer ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF0F7FF)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Cliente",
                                    tint = Color(0xFF1565C0),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Cliente de la visita:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF1565C0)
                                )
                            }
                            
                            Spacer(Modifier.height(8.dp))
                            
                            Text(
                                text = customer.businessName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1565C0)
                            )
                            
                            if (!customer.contactName.isNullOrEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Contacto: ${customer.contactName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF757575)
                                )
                            }
                            
                            if (!customer.contactPhone.isNullOrEmpty()) {
                                Text(
                                    text = "Teléfono: ${customer.contactPhone}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF757575)
                                )
                            }
                            
                            if (!customer.address.isNullOrEmpty()) {
                                Text(
                                    text = "Dirección: ${customer.address}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF757575)
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Campos habilitados solo si hay cliente seleccionado
            val fieldsEnabled = uiState.isCustomerSelected // Solo habilitar campos cuando se selecciona cliente
            val dateTimeEnabled = fieldsEnabled && !uiState.isVisitSaved // Fecha/hora solo editable antes de guardar
            val detailsEnabled = fieldsEnabled // Detalles siempre editables si hay cliente
            
            if (!fieldsEnabled) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                ) {
                    Text(
                        text = "Selecciona un cliente para continuar con los datos de la visita",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF757575)
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
            
            // Mostrar información adicional si la visita ya está guardada
            if (uiState.isVisitSaved) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                ) {
                    Text(
                        text = "💾 Los cambios en personas contactadas, hallazgos y notas se guardan automáticamente.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2E7D32)
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
            
            // Date Picker - Envuelto en Box clickeable
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = dateTimeEnabled) { 
                        if (dateTimeEnabled) onShowDatePicker(true) 
                    }
            ) {
                OutlinedTextField(
                    value = uiState.visitDate.format(dateFormatter),
                    onValueChange = {},
                    label = { Text("Fecha de la visita") },
                    leadingIcon = { 
                        Icon(
                            Icons.Default.CalendarToday, 
                            contentDescription = "Seleccionar fecha", 
                            tint = if (dateTimeEnabled) Color(0xFF1565C0) else Color(0xFFBDBDBD)
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false, // Deshabilitado para evitar focus, el click lo maneja el Box
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1565C0),
                        unfocusedBorderColor = if (dateTimeEnabled) Color(0xFFB6C6E3) else Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFF1565C0),
                        unfocusedLabelColor = if (dateTimeEnabled) Color(0xFF1565C0) else Color(0xFFBDBDBD),
                        disabledBorderColor = if (dateTimeEnabled) Color(0xFFB6C6E3) else Color(0xFFE0E0E0),
                        disabledLabelColor = if (dateTimeEnabled) Color(0xFF1565C0) else Color(0xFFBDBDBD),
                        disabledTextColor = if (dateTimeEnabled) Color(0xFF000000) else Color(0xFFBDBDBD)
                    )
                )
            }
            
            // Date Picker Dialog
            if (showDatePicker) {
                CustomDatePickerDialog(
                    onDateSelected = { dateMillis ->
                        dateMillis?.let {
                            // Usar UTC para evitar problemas de zona horaria
                            val selectedDate = Instant.ofEpochMilli(it)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            onDateChange(selectedDate)
                        }
                        onShowDatePicker(false)
                    },
                    onDismiss = { onShowDatePicker(false) },
                    datePickerState = datePickerState
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Time Picker - Envuelto en Box clickeable
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = dateTimeEnabled) { 
                        if (dateTimeEnabled) onShowTimePicker(true) 
                    }
            ) {
                OutlinedTextField(
                    value = uiState.visitTime.format(timeFormatter),
                    onValueChange = {},
                    label = { Text("Hora de la visita") },
                    leadingIcon = { 
                        Icon(
                            Icons.Default.AccessTime, 
                            contentDescription = "Seleccionar hora", 
                            tint = if (dateTimeEnabled) Color(0xFF1565C0) else Color(0xFFBDBDBD)
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false, // Deshabilitado para evitar focus, el click lo maneja el Box
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1565C0),
                        unfocusedBorderColor = if (dateTimeEnabled) Color(0xFFB6C6E3) else Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFF1565C0),
                        unfocusedLabelColor = if (dateTimeEnabled) Color(0xFF1565C0) else Color(0xFFBDBDBD),
                        disabledBorderColor = if (dateTimeEnabled) Color(0xFFB6C6E3) else Color(0xFFE0E0E0),
                        disabledLabelColor = if (dateTimeEnabled) Color(0xFF1565C0) else Color(0xFFBDBDBD),
                        disabledTextColor = if (dateTimeEnabled) Color(0xFF000000) else Color(0xFFBDBDBD)
                    )
                )
            }
            
            // Time Picker Dialog
            if (showTimePicker) {
                CustomTimePickerDialog(
                    onTimeSelected = { hour, minute ->
                        val selectedTime = LocalTime.of(hour, minute)
                        onTimeChange(selectedTime)
                        onShowTimePicker(false)
                    },
                    onDismiss = { onShowTimePicker(false) },
                    timePickerState = timePickerState
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            OutlinedTextField(
                value = uiState.contactedPersons,
                onValueChange = onContactedPersonsChange,
                label = { Text("Personas contactadas") },
                placeholder = { Text("Ej: Dr. García, Enf. López") },
                modifier = Modifier.fillMaxWidth(),
                enabled = detailsEnabled,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = if (detailsEnabled) Color(0xFFB6C6E3) else Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = if (detailsEnabled) Color(0xFF1565C0) else Color(0xFFBDBDBD),
                    disabledBorderColor = Color(0xFFE0E0E0),
                    disabledLabelColor = Color(0xFFBDBDBD),
                    disabledTextColor = Color(0xFFBDBDBD)
                )
            )
            
            Spacer(Modifier.height(12.dp))
            
            OutlinedTextField(
                value = uiState.clinicalFindings,
                onValueChange = onClinicalFindingsChange,
                label = { Text("Hallazgos clínicos") },
                placeholder = { Text("Describe los hallazgos relevantes...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                enabled = detailsEnabled,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = if (detailsEnabled) Color(0xFFB6C6E3) else Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = if (detailsEnabled) Color(0xFF1565C0) else Color(0xFFBDBDBD),
                    disabledBorderColor = Color(0xFFE0E0E0),
                    disabledLabelColor = Color(0xFFBDBDBD),
                    disabledTextColor = Color(0xFFBDBDBD)
                )
            )
            
            Spacer(Modifier.height(12.dp))
            
            OutlinedTextField(
                value = uiState.additionalNotes,
                onValueChange = onAdditionalNotesChange,
                label = { Text("Notas adicionales") },
                placeholder = { Text("Observaciones, comentarios...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                enabled = detailsEnabled,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = if (detailsEnabled) Color(0xFFB6C6E3) else Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = if (detailsEnabled) Color(0xFF1565C0) else Color(0xFFBDBDBD),
                    disabledBorderColor = Color(0xFFE0E0E0),
                    disabledLabelColor = Color(0xFFBDBDBD),
                    disabledTextColor = Color(0xFFBDBDBD)
                )
            )
        }
    }
}

@Composable
private fun UbicacionTabContent(
    uiState: com.misw.medisupply.presentation.salesforce.screens.visits.state.CreateVisitUiState,
    onAddressChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Ubicación de la visita", 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )
            
            // Mensaje informativo
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isVisitSaved) Color(0xFFE8F5E8) else Color(0xFFE3F2FD)
                )
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        text = if (uiState.isVisitSaved) {
                            "💾 La dirección se guarda automáticamente en tu visita."
                        } else {
                            "📍 Primero debes guardar la visita en el tab 'Datos' para poder agregar ubicación."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (uiState.isVisitSaved) Color(0xFF2E7D32) else Color(0xFF1565C0)
                    )
                    
                    // Indicador de guardado
                    if (uiState.isSaving && uiState.isVisitSaved) {
                        Spacer(Modifier.height(4.dp))
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = Color(0xFF2E7D32),
                                strokeWidth = 1.5.dp
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Guardando ubicación...",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            OutlinedTextField(
                value = uiState.address,
                onValueChange = onAddressChange,
                label = { Text("Dirección de la visita") },
                placeholder = { Text("Ej: Cra 7 #123-45, Bogotá") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF1565C0)) },
                enabled = uiState.isVisitSaved, // Solo habilitado después de guardar la visita
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = if (uiState.isVisitSaved) Color(0xFFB6C6E3) else Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = if (uiState.isVisitSaved) Color(0xFF1565C0) else Color(0xFFBDBDBD),
                    disabledBorderColor = Color(0xFFE0E0E0),
                    disabledLabelColor = Color(0xFFBDBDBD),
                    disabledTextColor = Color(0xFFBDBDBD)
                )
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Mapa placeholder con mejor diseño
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Mapa",
                            tint = Color(0xFF1565C0),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Vista del mapa",
                            color = Color(0xFF1565C0),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Integración próximamente",
                            color = Color(0xFF757575),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = { /* Seleccionar ubicación */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF1565C0)
                )
            ) {
                Icon(
                    Icons.Default.LocationOn, 
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Seleccionar ubicación")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomDatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    datePickerState: androidx.compose.material3.DatePickerState
) {
    androidx.compose.material3.DatePickerDialog(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomTimePickerDialog(
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
    timePickerState: TimePickerState
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}