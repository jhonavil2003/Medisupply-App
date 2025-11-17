package com.misw.medisupply.data.network.dto.route

import com.google.gson.annotations.SerializedName

/**
 * DTO de una parada en la ruta
 */
data class RouteStopDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("sequence_order")
    val sequenceOrder: Int,
    
    @SerializedName("customer")
    val customer: CustomerDto,
    
    @SerializedName("location")
    val location: CustomerLocationDto,
    
    @SerializedName("estimated_times")
    val estimatedTimes: EstimatedTimesDto,
    
    @SerializedName("actual_times")
    val actualTimes: ActualTimesDto? = null,
    
    @SerializedName("distance_metrics")
    val distanceMetrics: DistanceMetricsDto,
    
    @SerializedName("status")
    val status: StopStatusDto,
    
    @SerializedName("timestamps")
    val timestamps: StopTimestampsDto? = null,
    
    @SerializedName("notes")
    val notes: String? = null,
    
    @SerializedName("visit_id")
    val visitId: Int? = null,
    
    @SerializedName("visit_notes")
    val visitNotes: String? = null,
    
    @SerializedName("route_id")
    val routeId: Int
)

data class CustomerDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("document")
    val document: String,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("contact")
    val contact: ContactDto
)

data class CustomerLocationDto(
    @SerializedName("address")
    val address: String,
    
    @SerializedName("neighborhood")
    val neighborhood: String,
    
    @SerializedName("city")
    val city: String,
    
    @SerializedName("department")
    val department: String? = null,
    
    @SerializedName("latitude")
    val latitude: Double,
    
    @SerializedName("longitude")
    val longitude: Double
)

data class ContactDto(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("phone")
    val phone: String,
    
    @SerializedName("email")
    val email: String
)

data class EstimatedTimesDto(
    @SerializedName("arrival")
    val arrival: String,
    
    @SerializedName("departure")
    val departure: String,
    
    @SerializedName("service_minutes")
    val serviceMinutes: Int
)

data class ActualTimesDto(
    @SerializedName("arrival")
    val arrival: String,
    
    @SerializedName("departure")
    val departure: String,
    
    @SerializedName("service_minutes")
    val serviceMinutes: Int
)

data class DistanceMetricsDto(
    @SerializedName("from_previous_km")
    val fromPreviousKm: Double,
    
    @SerializedName("travel_time_minutes")
    val travelTimeMinutes: Int
)

data class StopStatusDto(
    @SerializedName("is_completed")
    val isCompleted: Boolean,
    
    @SerializedName("is_skipped")
    val isSkipped: Boolean,
    
    @SerializedName("skip_reason")
    val skipReason: String? = null
)

data class StopTimestampsDto(
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("updated_at")
    val updatedAt: String?,
    
    @SerializedName("completed_at")
    val completedAt: String?
)
