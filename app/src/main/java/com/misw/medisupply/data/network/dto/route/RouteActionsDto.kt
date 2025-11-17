package com.misw.medisupply.data.network.dto.route

import com.google.gson.annotations.SerializedName

/**
 * DTO para completar una parada
 */
data class CompleteStopRequest(
    @SerializedName("actual_arrival")
    val actualArrival: String, // ISO 8601: YYYY-MM-DDTHH:mm:ss
    
    @SerializedName("actual_departure")
    val actualDeparture: String, // ISO 8601: YYYY-MM-DDTHH:mm:ss
    
    @SerializedName("notes")
    val notes: String? = null
)

/**
 * DTO para omitir una parada
 */
data class SkipStopRequest(
    @SerializedName("reason")
    val reason: String
)

/**
 * DTO para respuesta de acciones sobre paradas
 */
data class StopActionResponse(
    @SerializedName("stop")
    val stop: RouteStopDto,
    
    @SerializedName("message")
    val message: String
)

/**
 * DTO para respuesta de acciones sobre rutas
 */
data class RouteActionResponse(
    @SerializedName("route")
    val route: RouteDto,
    
    @SerializedName("message")
    val message: String
)

/**
 * DTO genérico para mensajes
 */
data class MessageResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("route_id")
    val routeId: Int? = null
)
