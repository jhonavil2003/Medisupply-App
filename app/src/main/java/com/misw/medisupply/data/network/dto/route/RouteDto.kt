package com.misw.medisupply.data.network.dto.route

import com.google.gson.annotations.SerializedName

/**
 * DTO completo de una ruta
 */
data class RouteDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("route_code")
    val routeCode: String,
    
    @SerializedName("salesperson")
    val salesperson: SalespersonDto,
    
    @SerializedName("planned_date")
    val plannedDate: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("metrics")
    val metrics: RouteMetricsDto,
    
    @SerializedName("stops")
    val stops: List<RouteStopDto> = emptyList(),
    
    @SerializedName("start_location")
    val startLocation: LocationDto? = null,
    
    @SerializedName("end_location")
    val endLocation: LocationDto? = null,
    
    @SerializedName("work_hours")
    val workHours: WorkHoursDto,
    
    @SerializedName("optimization_strategy")
    val optimizationStrategy: String? = null,
    
    @SerializedName("map_url")
    val mapUrl: String? = null,
    
    @SerializedName("computation_time_seconds")
    val computationTimeSeconds: Double? = null,
    
    @SerializedName("created_at")
    val createdAt: String? = null,
    
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    
    @SerializedName("timestamps")
    val timestamps: RouteTimestampsDto? = null
)

data class SalespersonDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("employee_id")
    val employeeId: String? = null
)

data class RouteTimestampsDto(
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("confirmed_at")
    val confirmedAt: String?,
    
    @SerializedName("started_at")
    val startedAt: String?,
    
    @SerializedName("completed_at")
    val completedAt: String?
)

data class RouteMetricsDto(
    @SerializedName("total_stops")
    val totalStops: Int,
    
    @SerializedName("total_distance_km")
    val totalDistanceKm: Double,
    
    @SerializedName("estimated_duration_minutes")
    val estimatedDurationMinutes: Int,
    
    @SerializedName("optimization_score")
    val optimizationScore: Double,
    
    @SerializedName("completed_stops")
    val completedStops: Int = 0,
    
    @SerializedName("skipped_stops")
    val skippedStops: Int = 0
)
