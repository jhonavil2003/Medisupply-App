package com.misw.medisupply.data.network.dto.visit

import com.google.gson.annotations.SerializedName

data class UpdateVisitRequest(
    @SerializedName("customer_id")
    val customerId: Int? = null,
    
    @SerializedName("salesperson_id")
    val salespersonId: Int? = null,
    
    @SerializedName("visit_date")
    val visitDate: String? = null, // YYYY-MM-DD
    
    @SerializedName("visit_time")
    val visitTime: String? = null, // HH:MM:SS
    
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
    val longitude: Double? = null
)