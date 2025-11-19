package com.misw.medisupply.presentation.salesforce.screens.routes.components

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.misw.medisupply.BuildConfig
import com.misw.medisupply.domain.model.route.Route
import com.misw.medisupply.domain.model.route.RouteStop
import com.misw.medisupply.domain.model.route.StopStatus
import com.misw.medisupply.presentation.salesforce.screens.routes.utils.DirectionsApiHelper
import kotlinx.coroutines.launch

/**
 * Componente de mapa para visualizar ruta con todas las paradas
 * Muestra marcadores, números de secuencia y polyline siguiendo calles reales
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RouteMapView(
    route: Route,
    modifier: Modifier = Modifier,
    showPolyline: Boolean = true,
    onStopClick: ((RouteStop) -> Unit)? = null
) {
    // Permisos de ubicación
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    // Calcular centro del mapa basado en todas las paradas
    val mapCenter = remember(route.stops) {
        calculateMapCenter(route.stops)
    }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapCenter, 12f)
    }
    
    // Estado para almacenar los puntos de la ruta real
    var routePoints by remember { mutableStateOf<List<LatLng>?>(null) }
    val scope = rememberCoroutineScope()
    
    // Obtener ruta real usando Directions API
    LaunchedEffect(route.stops, showPolyline) {
        if (showPolyline && route.stops.size > 1 && BuildConfig.GOOGLE_MAPS_API_KEY.isNotEmpty()) {
            scope.launch {
                android.util.Log.d("RouteMapView", "Obteniendo ruta para ${route.stops.size} paradas")
                android.util.Log.d("RouteMapView", "API Key presente: ${BuildConfig.GOOGLE_MAPS_API_KEY.take(10)}...")
                
                val points = route.stops
                    .sortedBy { it.sequenceOrder }
                    .map { LatLng(it.latitude, it.longitude) }
                
                android.util.Log.d("RouteMapView", "Puntos a enviar: ${points.size}")
                points.forEachIndexed { index, point -> 
                    android.util.Log.d("RouteMapView", "Punto $index: ${point.latitude}, ${point.longitude}")
                }
                
                val directions = DirectionsApiHelper.getDirectionsWithWaypoints(
                    points = points,
                    apiKey = BuildConfig.GOOGLE_MAPS_API_KEY
                )
                
                if (directions != null) {
                    android.util.Log.d("RouteMapView", "✅ Ruta obtenida con ${directions.size} puntos")
                    routePoints = directions
                } else {
                    android.util.Log.e("RouteMapView", "❌ No se pudo obtener la ruta")
                }
            }
        } else {
            android.util.Log.w("RouteMapView", "No se puede obtener ruta: showPolyline=$showPolyline, stops=${route.stops.size}, apiKey=${BuildConfig.GOOGLE_MAPS_API_KEY.isNotEmpty()}")
        }
    }
    
    // Ajustar cámara cuando cambian las paradas
    LaunchedEffect(route.stops) {
        val bounds = calculateBounds(route.stops)
        bounds?.let {
            cameraPositionState.animate(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds(it, 100)
            )
        }
    }
    
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = locationPermissions.allPermissionsGranted,
            mapType = MapType.NORMAL
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = locationPermissions.allPermissionsGranted,
            compassEnabled = true
        )
    ) {
        // Marcadores de paradas
        route.stops.sortedBy { it.sequenceOrder }.forEach { stop ->
            val position = LatLng(stop.latitude, stop.longitude)
            val markerColor = when (stop.status) {
                StopStatus.COMPLETED -> BitmapDescriptorFactory.HUE_GREEN
                StopStatus.SKIPPED -> BitmapDescriptorFactory.HUE_ORANGE
                StopStatus.PENDING -> BitmapDescriptorFactory.HUE_RED
            }
            
            Marker(
                state = MarkerState(position = position),
                title = "${stop.sequenceOrder}. ${stop.customerName}",
                snippet = stop.address,
                icon = BitmapDescriptorFactory.defaultMarker(markerColor),
                onClick = {
                    onStopClick?.invoke(stop)
                    true
                }
            )
        }
        
        // Polyline con ruta real siguiendo calles
        if (showPolyline) {
            routePoints?.let { points ->
                Polyline(
                    points = points,
                    color = Color(0xFF1E63A8),
                    width = 10f
                )
            }
        }
    }
}

/**
 * Componente de mapa para ejecución de ruta en tiempo real
 * Muestra ubicación actual, próximas paradas y ruta restante siguiendo calles reales
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RouteExecutionMapView(
    route: Route,
    currentLocation: com.misw.medisupply.domain.model.route.Location?,
    modifier: Modifier = Modifier,
    onStopClick: ((RouteStop) -> Unit)? = null
) {
    // Permisos de ubicación
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    // Solicitar permisos automáticamente al cargar
    LaunchedEffect(Unit) {
        if (!locationPermissions.allPermissionsGranted) {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }
    
    // Determinar centro del mapa (ubicación actual o siguiente parada)
    val mapCenter = remember(currentLocation, route.stops) {
        currentLocation?.let {
            LatLng(it.latitude, it.longitude)
        } ?: route.stops.firstOrNull { it.status == StopStatus.PENDING }?.let {
            LatLng(it.latitude, it.longitude)
        } ?: calculateMapCenter(route.stops)
    }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapCenter, 14f)
    }
    
    // Estados para rutas reales
    var completedRoutePoints by remember { mutableStateOf<List<LatLng>?>(null) }
    var pendingRoutePoints by remember { mutableStateOf<List<LatLng>?>(null) }
    val scope = rememberCoroutineScope()
    
    // Obtener ruta real para paradas completadas
    LaunchedEffect(route.stops) {
        if (BuildConfig.GOOGLE_MAPS_API_KEY.isNotEmpty()) {
            scope.launch {
                android.util.Log.d("RouteExecutionMapView", "Obteniendo rutas para ejecución")
                
                val completedStops = route.stops
                    .filter { it.status == StopStatus.COMPLETED || it.status == StopStatus.SKIPPED }
                    .sortedBy { it.sequenceOrder }
                
                if (completedStops.size > 1) {
                    android.util.Log.d("RouteExecutionMapView", "Obteniendo ruta completada: ${completedStops.size} paradas")
                    val points = completedStops.map { LatLng(it.latitude, it.longitude) }
                    val result = DirectionsApiHelper.getDirectionsWithWaypoints(
                        points = points,
                        apiKey = BuildConfig.GOOGLE_MAPS_API_KEY
                    )
                    if (result != null) {
                        android.util.Log.d("RouteExecutionMapView", "✅ Ruta completada obtenida: ${result.size} puntos")
                        completedRoutePoints = result
                    } else {
                        android.util.Log.e("RouteExecutionMapView", "❌ No se pudo obtener ruta completada")
                    }
                }
                
                val pendingStops = route.stops
                    .filter { it.status == StopStatus.PENDING }
                    .sortedBy { it.sequenceOrder }
                
                if (pendingStops.size > 1) {
                    android.util.Log.d("RouteExecutionMapView", "Obteniendo ruta pendiente: ${pendingStops.size} paradas")
                    val points = pendingStops.map { LatLng(it.latitude, it.longitude) }
                    val result = DirectionsApiHelper.getDirectionsWithWaypoints(
                        points = points,
                        apiKey = BuildConfig.GOOGLE_MAPS_API_KEY
                    )
                    if (result != null) {
                        android.util.Log.d("RouteExecutionMapView", "✅ Ruta pendiente obtenida: ${result.size} puntos")
                        pendingRoutePoints = result
                    } else {
                        android.util.Log.e("RouteExecutionMapView", "❌ No se pudo obtener ruta pendiente")
                    }
                }
            }
        } else {
            android.util.Log.w("RouteExecutionMapView", "API Key no configurada")
        }
    }
    
    // Seguir ubicación actual
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.animate(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLng(
                    LatLng(it.latitude, it.longitude)
                )
            )
        }
    }
    
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = locationPermissions.allPermissionsGranted,
            mapType = MapType.NORMAL
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = locationPermissions.allPermissionsGranted,
            compassEnabled = true
        )
    ) {
        // Marcador de ubicación actual
        currentLocation?.let { location ->
            Marker(
                state = MarkerState(position = LatLng(location.latitude, location.longitude)),
                title = "Mi ubicación",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )
        }
        
        // Marcadores de paradas
        route.stops.sortedBy { it.sequenceOrder }.forEach { stop ->
            val position = LatLng(stop.latitude, stop.longitude)
            val markerColor = when (stop.status) {
                StopStatus.COMPLETED -> BitmapDescriptorFactory.HUE_GREEN
                StopStatus.SKIPPED -> BitmapDescriptorFactory.HUE_ORANGE
                StopStatus.PENDING -> BitmapDescriptorFactory.HUE_RED
            }
            
            Marker(
                state = MarkerState(position = position),
                title = "${stop.sequenceOrder}. ${stop.customerName}",
                snippet = "${stop.status.name} - ${stop.address}",
                icon = BitmapDescriptorFactory.defaultMarker(markerColor),
                alpha = if (stop.status == StopStatus.COMPLETED || stop.status == StopStatus.SKIPPED) 0.6f else 1f,
                onClick = {
                    onStopClick?.invoke(stop)
                    true
                }
            )
        }
        
        // Polyline de ruta completada (siguiendo calles reales)
        completedRoutePoints?.let { points ->
            Polyline(
                points = points,
                color = Color(0xFF16A34A), // Verde para ruta completada
                width = 8f
            )
        }
        
        // Polyline de ruta pendiente (siguiendo calles reales)
        pendingRoutePoints?.let { points ->
            Polyline(
                points = points,
                color = Color(0xFF1E63A8), // Azul para ruta pendiente
                width = 8f,
                pattern = listOf(
                    com.google.android.gms.maps.model.Dot(),
                    com.google.android.gms.maps.model.Gap(10f)
                )
            )
        }
    }
}

/**
 * Calcular centro geográfico de las paradas
 */
private fun calculateMapCenter(stops: List<RouteStop>): LatLng {
    if (stops.isEmpty()) return LatLng(4.6097, -74.0817) // Bogotá default
    
    val avgLat = stops.map { it.latitude }.average()
    val avgLng = stops.map { it.longitude }.average()
    return LatLng(avgLat, avgLng)
}

/**
 * Calcular bounds para ajustar cámara a todas las paradas
 */
private fun calculateBounds(stops: List<RouteStop>): com.google.android.gms.maps.model.LatLngBounds? {
    if (stops.isEmpty()) return null
    
    val builder = com.google.android.gms.maps.model.LatLngBounds.Builder()
    stops.forEach { stop ->
        builder.include(LatLng(stop.latitude, stop.longitude))
    }
    return builder.build()
}

/**
 * Card con mapa de ruta (para RouteDetailScreen)
 */
@Composable
fun RouteMapCard(
    route: Route,
    modifier: Modifier = Modifier,
    onStopClick: ((RouteStop) -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        RouteMapView(
            route = route,
            modifier = Modifier.fillMaxSize(),
            showPolyline = true,
            onStopClick = onStopClick
        )
    }
}

/**
 * Card con mapa de ejecución (para RouteExecutionScreen)
 */
@Composable
fun RouteExecutionMapCard(
    route: Route,
    currentLocation: com.misw.medisupply.domain.model.route.Location?,
    modifier: Modifier = Modifier,
    onStopClick: ((RouteStop) -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        RouteExecutionMapView(
            route = route,
            currentLocation = currentLocation,
            modifier = Modifier.fillMaxSize(),
            onStopClick = onStopClick
        )
    }
}
