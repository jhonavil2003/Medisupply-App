package com.misw.medisupply.data.remote.dto.customer

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for customer registration request
 * Used when registering a new customer in the system
 */
data class CreateCustomerRequest(
    @SerializedName("business_name")
    val businessName: String,
    
    @SerializedName("document_number")
    val documentNumber: String,
    
    @SerializedName("document_type")
    val documentType: String = "NIT",
    
    @SerializedName("contact_email")
    val contactEmail: String,
    
    @SerializedName("contact_phone")
    val contactPhone: String,
    
    @SerializedName("address")
    val address: String,
    
    @SerializedName("city")
    val city: String? = null,
    
    @SerializedName("department")
    val department: String? = null,
    
    @SerializedName("country")
    val country: String = "Colombia",
    
    @SerializedName("customer_type")
    val customerType: String = "hospital"
)

/**
 * Response DTO for customer registration
 */
data class CreateCustomerResponse(
    @SerializedName("id")
    val id: Int = 0,
    
    @SerializedName("business_name")
    val businessName: String? = null,
    
    @SerializedName("document_number")  
    val documentNumber: String? = null,
    
    @SerializedName("message")
    val message: String? = null
)

/**
 * Response DTO for document validation
 */
data class ValidateDocumentResponse(
    @SerializedName("exists")
    val exists: Boolean,
    
    @SerializedName("customer_id")
    val customerId: Int? = null,
    
    @SerializedName("message")
    val message: String? = null
)