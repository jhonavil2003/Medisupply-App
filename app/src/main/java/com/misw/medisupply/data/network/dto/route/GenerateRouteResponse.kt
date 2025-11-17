package com.misw.medisupply.data.network.dto.route

import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta de generación de ruta
 */
data class GenerateRouteResponse(
    @SerializedName("status")
    val status: String,
    
    @SerializedName("route")
    val route: RouteDto,
    
    @SerializedName("warnings")
    val warnings: List<String> = emptyList(),
    
    @SerializedName("computation_time_seconds")
    val computationTimeSeconds: Double? = null
)

/**
 * DTO para respuesta de detalle de ruta
 */
data class RouteDetailResponse(
    @SerializedName("route")
    val route: RouteDto
)

/**
 * DTO para respuesta de lista de rutas
 */
data class RoutesListResponse(
    @SerializedName("routes")
    val routes: List<RouteSummaryDto>,
    
    @SerializedName("total")
    val total: Int,
    
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("per_page")
    val perPage: Int
)

/**
 * DTO resumido de ruta para listas
 */
data class RouteSummaryDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("route_code")
    val routeCode: String,
    
    @SerializedName("planned_date")
    val plannedDate: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("metrics")
    val metrics: RouteMetricsDto,
    
    @SerializedName("progress")
    val progress: RouteProgressDto? = null
)

data class RouteProgressDto(
    @SerializedName("completed_stops")
    val completedStops: Int,
    
    @SerializedName("pending_stops")
    val pendingStops: Int,
    
    @SerializedName("percentage")
    val percentage: Int
)
