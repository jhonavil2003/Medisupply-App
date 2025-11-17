package com.misw.medisupply.data.network.dto.route

import com.google.gson.annotations.SerializedName

/**
 * DTO para la solicitud de generación de ruta
 */
data class GenerateRouteRequest(
    @SerializedName("salesperson_id")
    val salespersonId: Int,
    
    @SerializedName("salesperson_name")
    val salespersonName: String,
    
    @SerializedName("salesperson_employee_id")
    val salespersonEmployeeId: String,
    
    @SerializedName("customer_ids")
    val customerIds: List<Int>,
    
    @SerializedName("planned_date")
    val plannedDate: String, // YYYY-MM-DD
    
    @SerializedName("optimization_strategy")
    val optimizationStrategy: String = "minimize_distance",
    
    @SerializedName("start_location")
    val startLocation: LocationDto? = null,
    
    @SerializedName("end_location")
    val endLocation: LocationDto? = null,
    
    @SerializedName("work_hours")
    val workHours: WorkHoursDto? = null,
    
    @SerializedName("service_time_per_visit_minutes")
    val serviceTimePerVisitMinutes: Int = 30
)

data class LocationDto(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("latitude")
    val latitude: Double,
    
    @SerializedName("longitude")
    val longitude: Double
)

data class WorkHoursDto(
    @SerializedName("start")
    val start: String, // HH:mm
    
    @SerializedName("end")
    val end: String // HH:mm
)
