package com.misw.medisupply.presentation.salesforce.screens.routes.components

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Modelo de datos para la ubicación seleccionada
 */
data class SelectedLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

/**
 * Dialog para seleccionar una ubicación en Google Maps
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPickerDialog(
    onDismiss: () -> Unit,
    onLocationSelected: (SelectedLocation) -> Unit,
    initialLocation: LatLng = LatLng(4.6097, -74.0817) // Bogotá por defecto
) {
    val context = LocalContext.current
    var selectedPosition by remember { mutableStateOf(initialLocation) }
    var locationName by remember { mutableStateOf("") }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 12f)
    }
    
    // Permisos de ubicación
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    // Obtener ubicación actual del dispositivo
    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        if (locationPermissions.allPermissionsGranted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    selectedPosition = currentLatLng
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
                    )
                }
            }
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Text("Seleccionar Ubicación de Inicio")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) {
                // Campo para nombre de ubicación
                OutlinedTextField(
                    value = locationName,
                    onValueChange = { locationName = it },
                    label = { Text("Nombre del lugar (opcional)") },
                    placeholder = { Text("Ej: Oficina Central, Mi Casa, etc.") },
                    leadingIcon = {
                        Icon(Icons.Default.Place, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true
                )
                
                // Botón para usar ubicación actual
                if (locationPermissions.allPermissionsGranted) {
                    Button(
                        onClick = { getCurrentLocation() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.MyLocation,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Usar mi ubicación actual")
                    }
                } else {
                    Button(
                        onClick = { locationPermissions.launchMultiplePermissionRequest() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Habilitar permisos de ubicación")
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Información de coordenadas seleccionadas
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            "Coordenadas seleccionadas:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Lat: ${String.format("%.6f", selectedPosition.latitude)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "Lng: ${String.format("%.6f", selectedPosition.longitude)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Mapa
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(
                                isMyLocationEnabled = locationPermissions.allPermissionsGranted
                            ),
                            uiSettings = MapUiSettings(
                                zoomControlsEnabled = true,
                                myLocationButtonEnabled = locationPermissions.allPermissionsGranted,
                                compassEnabled = true
                            ),
                            onMapClick = { latLng ->
                                selectedPosition = latLng
                            }
                        ) {
                            // Marcador en la posición seleccionada
                            Marker(
                                state = MarkerState(position = selectedPosition),
                                title = if (locationName.isNotEmpty()) locationName else "Ubicación de inicio",
                                snippet = "Lat: ${String.format("%.6f", selectedPosition.latitude)}, " +
                                        "Lng: ${String.format("%.6f", selectedPosition.longitude)}"
                            )
                        }
                        
                        // Instrucción flotante
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(16.dp),
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    "Toca en el mapa para seleccionar",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = locationName.ifEmpty { "Ubicación personalizada" }
                    onLocationSelected(
                        SelectedLocation(
                            name = finalName,
                            latitude = selectedPosition.latitude,
                            longitude = selectedPosition.longitude
                        )
                    )
                    onDismiss()
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirmar ubicación")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
