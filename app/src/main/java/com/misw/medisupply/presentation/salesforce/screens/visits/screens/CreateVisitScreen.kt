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
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
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
            ExtendedFloatingActionButton(
                text = { Text("Guardar visita") },
                icon = { Icon(Icons.Default.Save, contentDescription = "Guardar visita") },
                onClick = { viewModel.saveVisit() },
                containerColor = if (uiState.isFormValid) Color(0xFF1565C0) else Color(0xFFBDBDBD)
            )
        }
    ) { paddingValues ->
        var visitDate by remember { mutableStateOf(LocalDate.now()) }
        var visitTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val dateDialogState = rememberMaterialDialogState()
        val timeDialogState = rememberMaterialDialogState()

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
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = Color(0xFF1565C0),
                        unselectedContentColor = Color(0xFF757575)
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
                        dateDialogState = dateDialogState,
                        timeDialogState = timeDialogState,
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

@Composable
private fun DatosTabContent(
    uiState: com.misw.medisupply.presentation.salesforce.screens.visits.state.CreateVisitUiState,
    visitDate: LocalDate,
    visitTime: LocalTime,
    dateFormatter: DateTimeFormatter,
    timeFormatter: DateTimeFormatter,
    dateDialogState: com.vanpra.composematerialdialogs.MaterialDialogState,
    timeDialogState: com.vanpra.composematerialdialogs.MaterialDialogState,
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
            val fieldsEnabled = uiState.selectedCustomer != null
            
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
                    .clickable { if (fieldsEnabled) dateDialogState.show() },
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
            
            MaterialDialog(
                dialogState = dateDialogState,
                buttons = {
                    positiveButton("OK")
                    negativeButton("Cancelar")
                }
            ) {
                datepicker(
                    initialDate = visitDate,
                    title = "Selecciona la fecha"
                ) { date ->
                    onDateChange(date)
                }
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
                    .clickable { if (fieldsEnabled) timeDialogState.show() },
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
            
            MaterialDialog(
                dialogState = timeDialogState,
                buttons = {
                    positiveButton("OK")
                    negativeButton("Cancelar")
                }
            ) {
                timepicker(
                    initialTime = visitTime,
                    title = "Selecciona la hora",
                    is24HourClock = true
                ) { time ->
                    onTimeChange(time)
                }
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