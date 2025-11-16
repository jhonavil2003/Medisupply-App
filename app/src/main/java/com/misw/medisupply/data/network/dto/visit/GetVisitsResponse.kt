package com.misw.medisupply.data.network.dto.visit

import com.google.gson.annotations.SerializedName

data class GetVisitsResponse(
    @SerializedName("visits")
    val visits: List<VisitDto>,
    
    @SerializedName("total")
    val total: Int
)

data class VisitDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("customer_id")
    val customerId: Int,
    
    @SerializedName("salesperson_id")
    val salespersonId: Int,
    
    @SerializedName("visit_date")
    val visitDate: String, // formato: YYYY-MM-DD
    
    @SerializedName("visit_time")
    val visitTime: String?, // formato: HH:MM:SS
    
    @SerializedName("contacted_persons")
    val contactedPersons: String?,
    
    @SerializedName("clinical_findings")
    val clinicalFindings: String?,
    
    @SerializedName("additional_notes")
    val additionalNotes: String?,
    
    @SerializedName("address")
    val address: String?,
    
    @SerializedName("latitude")
    val latitude: Double?,
    
    @SerializedName("longitude")
    val longitude: Double?,
    
    @SerializedName("status")
    val status: String, // PROGRAMADA, REALIZADA, CANCELADA
    
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("updated_at")
    val updatedAt: String?
)
