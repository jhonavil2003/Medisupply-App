package com.misw.medisupply.data.network.dto.visit

import com.google.gson.annotations.SerializedName

data class CreateVisitRequest(
    @SerializedName("customer_id")
    val customerId: Int,
    
    @SerializedName("salesperson_id")
    val salespersonId: Int,
    
    @SerializedName("visit_date")
    val visitDate: String, // YYYY-MM-DD
    
    @SerializedName("visit_time")
    val visitTime: String, // HH:MM:SS
    
    @SerializedName("contacted_persons")
    val contactedPersons: String? = null,
    
    @SerializedName("clinical_findings")
    val clinicalFindings: String? = null,
    
    @SerializedName("additional_notes")
    val additionalNotes: String? = null,
    
    @SerializedName("address")
    val address: String? = null,
    
    @SerializedName("latitude")
    val latitude: Double? = null,
    
    @SerializedName("longitude")
    val longitude: Double? = null,
    
    @SerializedName("status")
    val status: String = "PROGRAMADA"
)