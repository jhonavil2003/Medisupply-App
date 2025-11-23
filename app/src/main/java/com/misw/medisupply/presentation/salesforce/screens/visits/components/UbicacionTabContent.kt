package com.misw.medisupply.presentation.salesforce.screens.visits.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.misw.medisupply.R
import com.misw.medisupply.presentation.components.localizedStringResource
import com.misw.medisupply.presentation.salesforce.screens.visits.state.CreateVisitUiState
import com.misw.medisupply.presentation.salesforce.screens.visits.viewmodel.CreateVisitViewModel

@Composable
fun UbicacionTabContent(
    uiState: CreateVisitUiState,
    viewModel: CreateVisitViewModel,
    localeManager: com.misw.medisupply.core.i18n.LocaleManager
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    var hasLocationPermission by remember { mutableStateOf(false) }
    var isLoadingLocation by remember { mutableStateOf(false) }
    
    // Launcher para solicitar permisos de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        if (hasLocationPermission) {
            // Obtener ubicación actual automáticamente
            isLoadingLocation = true
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        viewModel.updateLocation(it.latitude, it.longitude)
                    }
                    isLoadingLocation = false
                }
            } catch (e: SecurityException) {
                isLoadingLocation = false
            }
        }
    }
    
    // Verificar permisos al iniciar
    LaunchedEffect(Unit) {
        val hasFineLocation = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == 
                android.content.pm.PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == 
                android.content.pm.PackageManager.PERMISSION_GRANTED
        
        hasLocationPermission = hasFineLocation || hasCoarseLocation
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    localizedStringResource(R.string.visit_location_title, localeManager),
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
                                localizedStringResource(R.string.address_auto_save, localeManager)
                            } else {
                                localizedStringResource(R.string.save_visit_first, localeManager)
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (uiState.isVisitSaved) Color(0xFF2E7D32) else Color(0xFF1565C0)
                        )
                        
                        // Indicador de guardado
                        if (uiState.isSaving && uiState.isVisitSaved) {
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    color = Color(0xFF2E7D32),
                                    strokeWidth = 1.5.dp
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = localizedStringResource(R.string.saving_location, localeManager),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Campo de dirección
                OutlinedTextField(
                    value = uiState.address,
                    onValueChange = { viewModel.updateAddress(it) },
                    label = { Text(localizedStringResource(R.string.visit_address_label, localeManager)) },
                    placeholder = { Text(localizedStringResource(R.string.address_placeholder, localeManager)) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = localizedStringResource(R.string.address_label, localeManager),
                            tint = Color(0xFF1565C0)
                        )
                    },
                    enabled = uiState.isVisitSaved,
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
                
                // Mapa de Google
                if (uiState.isVisitSaved) {
                    MapSection(
                        latitude = uiState.latitude,
                        longitude = uiState.longitude,
                        hasLocationPermission = hasLocationPermission,
                        isLoadingLocation = isLoadingLocation,
                        onLocationSelected = { lat, lng ->
                            viewModel.updateLocation(lat, lng)
                        },
                        localeManager = localeManager
                    )
                } else {
                    // Placeholder cuando no se ha guardado la visita
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
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
                                    text = localizedStringResource(R.string.save_visit_first, localeManager),
                                    color = Color(0xFF1565C0),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Botones de acción
                if (uiState.isVisitSaved) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Botón para obtener ubicación actual
                        OutlinedButton(
                            onClick = {
                                if (hasLocationPermission) {
                                    isLoadingLocation = true
                                    try {
                                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                            location?.let {
                                                viewModel.updateLocation(it.latitude, it.longitude)
                                            }
                                            isLoadingLocation = false
                                        }
                                    } catch (e: SecurityException) {
                                        isLoadingLocation = false
                                    }
                                } else {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF1565C0)
                            ),
                            enabled = !isLoadingLocation
                        ) {
                            if (isLoadingLocation) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.MyLocation,
                                    contentDescription = "Mi ubicación",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text("Mi Ubicación")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MapSection(
    latitude: Double?,
    longitude: Double?,
    hasLocationPermission: Boolean,
    isLoadingLocation: Boolean,
    onLocationSelected: (Double, Double) -> Unit,
    localeManager: com.misw.medisupply.core.i18n.LocaleManager
) {
    val defaultLocation = LatLng(4.60971, -74.08175) // Bogotá por defecto
    val currentLocation = if (latitude != null && longitude != null) {
        LatLng(latitude, longitude)
    } else {
        defaultLocation
    }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
    }
    
    // Actualizar cámara cuando cambie la ubicación
    LaunchedEffect(latitude, longitude) {
        if (latitude != null && longitude != null) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(latitude, longitude),
                15f
            )
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = hasLocationPermission,
                    zoomControlsEnabled = true
                ),
                onMapClick = { latLng ->
                    onLocationSelected(latLng.latitude, latLng.longitude)
                }
            ) {
                // Marcador en la ubicación seleccionada
                if (latitude != null && longitude != null) {
                    Marker(
                        state = MarkerState(position = LatLng(latitude, longitude)),
                        title = "Ubicación de la visita",
                        snippet = "Lat: ${"%.6f".format(latitude)}, Lng: ${"%.6f".format(longitude)}"
                    )
                }
            }
            
            // Indicador de carga
            if (isLoadingLocation) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.9f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(12.dp))
                            Text("Obteniendo ubicación...")
                        }
                    }
                }
            }
        }
    }
}
