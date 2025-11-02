package com.misw.medisupply.data.network.dto.visit

import com.google.gson.annotations.SerializedName

data class CreateVisitResponse(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("customer_id")
    val customerId: Int,
    
    @SerializedName("salesperson_id")
    val salespersonId: Int,
    
    @SerializedName("visit_date")
    val visitDate: String,
    
    @SerializedName("visit_time")
    val visitTime: String,
    
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
    
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("updated_at")
    val updatedAt: String?,
    
    @SerializedName("status")
    val status: String?
)