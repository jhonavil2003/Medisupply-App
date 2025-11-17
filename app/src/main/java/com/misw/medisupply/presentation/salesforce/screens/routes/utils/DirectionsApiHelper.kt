package com.misw.medisupply.presentation.salesforce.screens.routes.utils

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

/**
 * Helper para obtener rutas de Google Directions API
 * Convierte coordenadas en rutas reales siguiendo calles
 */
object DirectionsApiHelper {
    
    private const val TAG = "DirectionsAPI"
    
    /**
     * Obtener ruta real entre dos puntos usando Directions API
     * 
     * @param origin Punto de inicio (LatLng)
     * @param destination Punto de destino (LatLng)
     * @param apiKey API Key de Google Maps
     * @return Lista de LatLng representando la ruta, o null si hay error
     */
    suspend fun getDirections(
        origin: LatLng,
        destination: LatLng,
        apiKey: String
    ): List<LatLng>? = withContext(Dispatchers.IO) {
        try {
            val originStr = "${origin.latitude},${origin.longitude}"
            val destStr = "${destination.latitude},${destination.longitude}"
            
            val url = buildDirectionsUrl(originStr, destStr, apiKey)
            
            val response = URL(url).readText()
            val json = JSONObject(response)
            
            // Verificar status de la respuesta
            val status = json.getString("status")
            if (status != "OK") {
                Log.e(TAG, "Directions API error: $status")
                return@withContext null
            }
            
            // Extraer la polyline de la ruta
            val routes = json.getJSONArray("routes")
            if (routes.length() == 0) {
                Log.e(TAG, "No routes found")
                return@withContext null
            }
            
            val route = routes.getJSONObject(0)
            val overviewPolyline = route.getJSONObject("overview_polyline")
            val encodedPolyline = overviewPolyline.getString("points")
            
            // Decodificar polyline
            decodePolyline(encodedPolyline)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting directions", e)
            null
        }
    }
    
    /**
     * Obtener ruta completa para múltiples paradas (waypoints)
     * 
     * @param points Lista de puntos a visitar en orden
     * @param apiKey API Key de Google Maps
     * @return Lista de LatLng representando toda la ruta, o null si hay error
     */
    suspend fun getDirectionsWithWaypoints(
        points: List<LatLng>,
        apiKey: String
    ): List<LatLng>? = withContext(Dispatchers.IO) {
        if (points.size < 2) {
            Log.w(TAG, "No hay suficientes puntos (${points.size})")
            return@withContext null
        }
        
        try {
            val origin = points.first()
            val destination = points.last()
            val waypoints = points.drop(1).dropLast(1) // Puntos intermedios
            
            val originStr = "${origin.latitude},${origin.longitude}"
            val destStr = "${destination.latitude},${destination.longitude}"
            
            val waypointsStr = if (waypoints.isNotEmpty()) {
                waypoints.joinToString("|") { "${it.latitude},${it.longitude}" }
            } else null
            
            val url = buildDirectionsUrl(originStr, destStr, apiKey, waypointsStr)
            Log.d(TAG, "URL generada: ${url.take(150)}...")
            
            val response = URL(url).readText()
            Log.d(TAG, "Respuesta recibida: ${response.take(200)}...")
            
            val json = JSONObject(response)
            
            val status = json.getString("status")
            Log.d(TAG, "Status de la respuesta: $status")
            
            if (status != "OK") {
                Log.e(TAG, "Directions API error: $status")
                if (json.has("error_message")) {
                    Log.e(TAG, "Error message: ${json.getString("error_message")}")
                }
                return@withContext null
            }
            
            val routes = json.getJSONArray("routes")
            if (routes.length() == 0) {
                Log.e(TAG, "No routes found")
                return@withContext null
            }
            
            val route = routes.getJSONObject(0)
            val overviewPolyline = route.getJSONObject("overview_polyline")
            val encodedPolyline = overviewPolyline.getString("points")
            
            Log.d(TAG, "Polyline codificada obtenida, largo: ${encodedPolyline.length}")
            
            val decodedPoints = decodePolyline(encodedPolyline)
            Log.d(TAG, "✅ Polyline decodificada: ${decodedPoints.size} puntos")
            
            decodedPoints
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting directions with waypoints", e)
            null
        }
    }
    
    /**
     * Construir URL para Directions API
     */
    private fun buildDirectionsUrl(
        origin: String,
        destination: String,
        apiKey: String,
        waypoints: String? = null
    ): String {
        val baseUrl = "https://maps.googleapis.com/maps/api/directions/json"
        val originParam = "origin=${URLEncoder.encode(origin, "UTF-8")}"
        val destParam = "destination=${URLEncoder.encode(destination, "UTF-8")}"
        val waypointsParam = waypoints?.let { "waypoints=optimize:true|${URLEncoder.encode(it, "UTF-8")}" } ?: ""
        val modeParam = "mode=driving" // Modo de transporte
        val keyParam = "key=$apiKey"
        
        return if (waypoints != null) {
            "$baseUrl?$originParam&$destParam&$waypointsParam&$modeParam&$keyParam"
        } else {
            "$baseUrl?$originParam&$destParam&$modeParam&$keyParam"
        }
    }
    
    /**
     * Decodificar polyline codificada de Google Maps
     * Algoritmo: https://developers.google.com/maps/documentation/utilities/polylinealgorithm
     */
    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            
            // Decodificar latitud
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            
            shift = 0
            result = 0
            
            // Decodificar longitud
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            
            val latLng = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(latLng)
        }
        
        return poly
    }
}
