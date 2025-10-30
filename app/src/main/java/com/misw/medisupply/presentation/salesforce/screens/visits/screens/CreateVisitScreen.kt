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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVisitScreen(
    onNavigateBack: (() -> Unit)? = null,
    viewModel: com.misw.medisupply.presentation.salesforce.screens.visits.viewmodel.CreateVisitViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Datos", "Ubicación", "Archivos")
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = "Crear visita",
                subtitle = "Visitas - Medisupply",
                onNavigateBack = onNavigateBack
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
        var visitDate by remember { mutableStateOf(LocalDate.now()) }
        var visitTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        var showDatePicker by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }
        
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = visitDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        val timePickerState = rememberTimePickerState(
            initialHour = visitTime.hour,
            initialMinute = visitTime.minute,
            is24Hour = true
        )

        // Manejar éxito del guardado
        LaunchedEffect(uiState.saveSuccess) {
            if (uiState.saveSuccess) {
                // Cambiar al tab de ubicación automáticamente después de guardar
                selectedTabIndex = 1
                viewModel.clearSuccess()
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
                        visitDate = visitDate,
                        visitTime = visitTime,
                        dateFormatter = dateFormatter,
                        timeFormatter = timeFormatter,
                        showDatePicker = showDatePicker,
                        showTimePicker = showTimePicker,
                        datePickerState = datePickerState,
                        timePickerState = timePickerState,
                        onShowDatePicker = { showDatePicker = it },
                        onShowTimePicker = { showTimePicker = it },
                        onDateChange = { visitDate = it },
                        onTimeChange = { visitTime = it },
                        onCustomerSearchQueryChange = viewModel::searchCustomers,
                        onCustomerSelected = viewModel::selectCustomer,
                        onClearCustomerSelection = viewModel::clearCustomerSelection,
                        onContactedPersonsChange = viewModel::updateContactedPersons,
                        onClinicalFindingsChange = viewModel::updateClinicalFindings,
                        onAdditionalNotesChange = viewModel::updateAdditionalNotes
                    )
                    1 -> UbicacionTabContent()
                    2 -> ArchivosTabContent()
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
    visitDate: LocalDate,
    visitTime: LocalTime,
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
                    Text(
                        text = if (uiState.isVisitSaved) {
                            "✅ Visita guardada exitosamente. Ahora puedes agregar ubicación y archivos."
                        } else if (!uiState.isCustomerSelected) {
                            "1️⃣ Selecciona un cliente para comenzar"
                        } else if (!uiState.areVisitFieldsComplete) {
                            "2️⃣ Completa los datos de la visita"
                        } else {
                            "3️⃣ Haz clic en 'Guardar visita' para continuar"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (uiState.isVisitSaved) Color(0xFF2E7D32) else Color(0xFF1565C0)
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // 1. PRIMERO: Selección de cliente
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
            
            Spacer(Modifier.height(16.dp))
            
            // Campos habilitados solo si hay cliente seleccionado
            val fieldsEnabled = uiState.isCustomerSelected // Solo habilitar campos cuando se selecciona cliente
            
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
            
            // Date Picker
            OutlinedTextField(
                value = visitDate.format(dateFormatter),
                onValueChange = {},
                label = { Text("Fecha de la visita") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha", tint = if (fieldsEnabled) Color(0xFF1565C0) else Color(0xFFBDBDBD)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (fieldsEnabled) onShowDatePicker(true) },
                readOnly = true,
                enabled = fieldsEnabled,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = if (fieldsEnabled) Color(0xFFB6C6E3) else Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = if (fieldsEnabled) Color(0xFF1565C0) else Color(0xFFBDBDBD),
                    disabledBorderColor = Color(0xFFE0E0E0),
                    disabledLabelColor = Color(0xFFBDBDBD),
                    disabledTextColor = Color(0xFFBDBDBD)
                )
            )
            
            // Date Picker Dialog
            if (showDatePicker) {
                CustomDatePickerDialog(
                    onDateSelected = { dateMillis ->
                        dateMillis?.let {
                            val selectedDate = Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
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
            
            // Time Picker
            OutlinedTextField(
                value = visitTime.format(timeFormatter),
                onValueChange = {},
                label = { Text("Hora de la visita") },
                leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = "Seleccionar hora", tint = if (fieldsEnabled) Color(0xFF1565C0) else Color(0xFFBDBDBD)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (fieldsEnabled) onShowTimePicker(true) },
                readOnly = true,
                enabled = fieldsEnabled,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = if (fieldsEnabled) Color(0xFFB6C6E3) else Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = if (fieldsEnabled) Color(0xFF1565C0) else Color(0xFFBDBDBD),
                    disabledBorderColor = Color(0xFFE0E0E0),
                    disabledLabelColor = Color(0xFFBDBDBD),
                    disabledTextColor = Color(0xFFBDBDBD)
                )
            )
            
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
                enabled = fieldsEnabled,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = if (fieldsEnabled) Color(0xFFB6C6E3) else Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = if (fieldsEnabled) Color(0xFF1565C0) else Color(0xFFBDBDBD),
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
                enabled = fieldsEnabled,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = if (fieldsEnabled) Color(0xFFB6C6E3) else Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = if (fieldsEnabled) Color(0xFF1565C0) else Color(0xFFBDBDBD),
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
                enabled = fieldsEnabled,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = if (fieldsEnabled) Color(0xFFB6C6E3) else Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = if (fieldsEnabled) Color(0xFF1565C0) else Color(0xFFBDBDBD),
                    disabledBorderColor = Color(0xFFE0E0E0),
                    disabledLabelColor = Color(0xFFBDBDBD),
                    disabledTextColor = Color(0xFFBDBDBD)
                )
            )
        }
    }
}

@Composable
private fun UbicacionTabContent() {
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
            Spacer(Modifier.height(16.dp))
            
            OutlinedTextField(
                value = "Cra 7 #123-45, Bogotá",
                onValueChange = {},
                label = { Text("Buscar dirección") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF1565C0)) },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color(0xFFB6C6E3),
                    disabledLabelColor = Color(0xFF1565C0)
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

@Composable
private fun ArchivosTabContent() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Archivos adjuntos", 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )
            Spacer(Modifier.height(16.dp))
            
            // Área de archivos con mejor diseño
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
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
                            painterResource(id = android.R.drawable.ic_menu_upload),
                            contentDescription = "Subir archivos",
                            tint = Color(0xFF757575),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Adjuntar archivos",
                            color = Color(0xFF757575),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Fotos, documentos, etc.",
                            color = Color(0xFF9E9E9E),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = { /* Adjuntar archivos */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = false, // Deshabilitado por ahora
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF757575)
                )
            ) {
                Icon(
                    painterResource(id = android.R.drawable.ic_menu_upload),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Adjuntar archivos")
            }
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                text = "Funcionalidad próximamente disponible",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
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