package com.misw.medisupply.presentation.salesforce.screens.routes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.domain.model.route.OptimizationStrategy
import com.misw.medisupply.presentation.salesforce.screens.routes.components.CustomerCheckboxItem
import com.misw.medisupply.presentation.salesforce.screens.routes.components.LocationPickerDialog
import com.misw.medisupply.presentation.salesforce.screens.routes.viewmodel.GenerateRouteViewModel
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateRouteScreen(
    onNavigateBack: () -> Unit,
    onRouteGenerated: (Int) -> Unit,
    viewModel: GenerateRouteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showStrategyDialog by remember { mutableStateOf(false) }
    var showLocationPicker by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Generar Ruta Optimizada") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (uiState.selectedCustomerIds.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearSelection() }) {
                            Text("Limpiar (${uiState.selectedCustomerIds.size})")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.canGenerate) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.generateRoute(onRouteGenerated) },
                    icon = { Icon(Icons.Default.Route, contentDescription = null) },
                    text = { Text("Generar Ruta") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sección de configuración
                item {
                    ConfigurationSection(
                        selectedDate = uiState.selectedDate,
                        optimizationStrategy = uiState.optimizationStrategy,
                        workHoursStart = uiState.workHoursStart,
                        workHoursEnd = uiState.workHoursEnd,
                        serviceTimeMinutes = uiState.serviceTimeMinutes,
                        useCustomStartLocation = uiState.useCustomStartLocation,
                        onDateClick = { showDatePicker = true },
                        onStrategyClick = { showStrategyDialog = true },
                        onStartTimeClick = { showStartTimePicker = true },
                        onEndTimeClick = { showEndTimePicker = true },
                        onServiceTimeChange = { viewModel.updateServiceTime(it) },
                        onToggleCustomStart = { enabled ->
                            viewModel.toggleCustomStartLocation(enabled)
                            // Abrir selector de mapa automáticamente cuando se habilita
                            if (enabled) {
                                showLocationPicker = true
                            }
                        }
                    )
                }
                
                // Ubicación de inicio personalizada
                if (uiState.useCustomStartLocation) {
                    item {
                        StartLocationSection(
                            name = uiState.startLocationName,
                            latitude = uiState.startLocationLatitude,
                            longitude = uiState.startLocationLongitude,
                            onOpenMapPicker = { showLocationPicker = true }
                        )
                    }
                }
                
                // Sección de selección de clientes
                item {
                    CustomerSelectionHeader(
                        selectedCount = uiState.selectedCustomerIds.size,
                        totalCount = uiState.filteredCustomers.size,
                        searchQuery = uiState.searchQuery,
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        onSelectAll = { viewModel.selectAllFiltered() }
                    )
                }
                
                // Lista de clientes
                when {
                    uiState.isLoadingCustomers -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    
                    uiState.customerError != null -> {
                        item {
                            ErrorCard(
                                message = uiState.customerError ?: "Error al cargar clientes",
                                onRetry = { viewModel.loadCustomers() }
                            )
                        }
                    }
                    
                    uiState.filteredCustomers.isEmpty() -> {
                        item {
                            EmptyStateCard(
                                message = if (uiState.searchQuery.isNotEmpty()) {
                                    "No se encontraron clientes con '${uiState.searchQuery}'"
                                } else {
                                    "No hay clientes con ubicación GPS configurada"
                                }
                            )
                        }
                    }
                    
                    else -> {
                        items(
                            items = uiState.filteredCustomers,
                            key = { it.id }
                        ) { customer ->
                            CustomerCheckboxItem(
                                customer = customer,
                                isSelected = customer.id in uiState.selectedCustomerIds,
                                onSelectionChanged = { _ ->
                                    viewModel.toggleCustomerSelection(customer.id)
                                }
                            )
                        }
                    }
                }
                
                // Espaciado para FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
            
            // Loading overlay
            if (uiState.isGenerating) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Generando ruta optimizada...",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Esto puede tomar unos segundos",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
    
    // Error snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Mostrar snackbar
            viewModel.clearError()
        }
    }
    
    uiState.validationError?.let { error ->
        LaunchedEffect(error) {
            // Mostrar snackbar
            viewModel.clearValidationError()
        }
    }
    
    // Date picker
    if (showDatePicker) {
        DatePickerDialog(
            selectedDate = uiState.selectedDate,
            onDateSelected = { date ->
                viewModel.updateSelectedDate(date)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
    
    // Strategy dialog
    if (showStrategyDialog) {
        OptimizationStrategyDialog(
            selectedStrategy = uiState.optimizationStrategy,
            onStrategySelected = { strategy ->
                viewModel.updateOptimizationStrategy(strategy)
                showStrategyDialog = false
            },
            onDismiss = { showStrategyDialog = false }
        )
    }
    
    // Location picker dialog
    if (showLocationPicker) {
        LocationPickerDialog(
            onDismiss = { showLocationPicker = false },
            onLocationSelected = { location ->
                viewModel.updateStartLocation(
                    location.name,
                    location.latitude.toString(),
                    location.longitude.toString()
                )
            },
            initialLocation = if (uiState.startLocationLatitude.isNotEmpty() && 
                                  uiState.startLocationLongitude.isNotEmpty()) {
                try {
                    LatLng(
                        uiState.startLocationLatitude.toDouble(),
                        uiState.startLocationLongitude.toDouble()
                    )
                } catch (e: Exception) {
                    LatLng(4.6097, -74.0817) // Bogotá por defecto
                }
            } else {
                LatLng(4.6097, -74.0817) // Bogotá por defecto
            }
        )
    }
    
    // Time pickers
    if (showStartTimePicker) {
        TimePickerDialog(
            selectedTime = uiState.workHoursStart,
            title = "Hora de inicio",
            onTimeSelected = { time ->
                viewModel.updateWorkHoursStart(time)
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false }
        )
    }
    
    if (showEndTimePicker) {
        TimePickerDialog(
            selectedTime = uiState.workHoursEnd,
            title = "Hora de fin",
            onTimeSelected = { time ->
                viewModel.updateWorkHoursEnd(time)
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfigurationSection(
    selectedDate: LocalDate,
    optimizationStrategy: OptimizationStrategy,
    workHoursStart: LocalTime,
    workHoursEnd: LocalTime,
    serviceTimeMinutes: Int,
    useCustomStartLocation: Boolean,
    onDateClick: () -> Unit,
    onStrategyClick: () -> Unit,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit,
    onServiceTimeChange: (Int) -> Unit,
    onToggleCustomStart: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Configuración de Ruta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Fecha
            OutlinedCard(
                onClick = onDateClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Fecha de ruta",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Estrategia
            OutlinedCard(
                onClick = onStrategyClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Estrategia de optimización",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = when (optimizationStrategy) {
                                OptimizationStrategy.MINIMIZE_DISTANCE -> "Distancia más corta"
                                OptimizationStrategy.MINIMIZE_TIME -> "Tiempo más corto"
                                OptimizationStrategy.BALANCED -> "Balanceado"
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Icon(Icons.Default.Tune, contentDescription = null)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Horario de trabajo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedCard(
                    onClick = onStartTimeClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Inicio",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = workHoursStart.format(DateTimeFormatter.ofPattern("HH:mm")),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                OutlinedCard(
                    onClick = onEndTimeClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Fin",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = workHoursEnd.format(DateTimeFormatter.ofPattern("HH:mm")),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Tiempo de servicio
            Column {
                Text(
                    text = "Tiempo de servicio por visita",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$serviceTimeMinutes minutos",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = serviceTimeMinutes.toFloat(),
                    onValueChange = { onServiceTimeChange(it.toInt()) },
                    valueRange = 15f..120f,
                    steps = 20
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Ubicación personalizada
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ubicación de inicio personalizada",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = useCustomStartLocation,
                    onCheckedChange = onToggleCustomStart,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        uncheckedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        }
    }
}

@Composable
private fun StartLocationSection(
    name: String,
    latitude: String,
    longitude: String,
    onOpenMapPicker: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ubicación de Inicio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onOpenMapPicker) {
                    Icon(
                        Icons.Default.Map,
                        contentDescription = "Abrir mapa",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            if (latitude.isNotEmpty() && longitude.isNotEmpty()) {
                // Mostrar información de ubicación seleccionada
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = name.ifEmpty { "Ubicación personalizada" },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        
                        Text(
                            text = "Lat: $latitude",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        
                        Text(
                            text = "Lng: $longitude",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
            
            // Botón para seleccionar en mapa
            Button(
                onClick = onOpenMapPicker,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(
                    if (latitude.isEmpty() || longitude.isEmpty()) {
                        Icons.Default.Add
                    } else {
                        Icons.Default.Edit
                    },
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (latitude.isEmpty() || longitude.isEmpty()) {
                        "Seleccionar en el mapa"
                    } else {
                        "Cambiar ubicación"
                    }
                )
            }
        }
    }
}

@Composable
private fun CustomerSelectionHeader(
    selectedCount: Int,
    totalCount: Int,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSelectAll: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Seleccionar Clientes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (totalCount > 0) {
                TextButton(onClick = onSelectAll) {
                    Text("Seleccionar todos")
                }
            }
        }
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Buscar clientes...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        if (selectedCount > 0) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "$selectedCount clientes seleccionados de $totalCount",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = java.time.Instant.ofEpochMilli(millis)
                        val date = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        onDateSelected(date)
                    }
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    text = "Seleccionar fecha de ruta",
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp)
                )
            },
            headline = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val instant = java.time.Instant.ofEpochMilli(millis)
                    val date = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                        modifier = Modifier.padding(start = 24.dp)
                    )
                }
            },
            showModeToggle = true
        )
    }
}

@Composable
private fun OptimizationStrategyDialog(
    selectedStrategy: OptimizationStrategy,
    onStrategySelected: (OptimizationStrategy) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Estrategia de Optimización") },
        text = {
            Column {
                OptimizationStrategy.values().forEach { strategy ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = strategy == selectedStrategy,
                            onClick = { onStrategySelected(strategy) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = when (strategy) {
                                    OptimizationStrategy.MINIMIZE_DISTANCE -> "Distancia más corta"
                                    OptimizationStrategy.MINIMIZE_TIME -> "Tiempo más corto"
                                    OptimizationStrategy.BALANCED -> "Balanceado"
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = when (strategy) {
                                    OptimizationStrategy.MINIMIZE_DISTANCE -> "Minimiza la distancia total"
                                    OptimizationStrategy.MINIMIZE_TIME -> "Minimiza el tiempo de viaje"
                                    OptimizationStrategy.BALANCED -> "Equilibrio entre distancia y tiempo"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    selectedTime: LocalTime,
    title: String,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime.hour,
        initialMinute = selectedTime.minute,
        is24Hour = true
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(title) 
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.padding(16.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val time = LocalTime.of(
                        timePickerState.hour,
                        timePickerState.minute
                    )
                    onTimeSelected(time)
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
