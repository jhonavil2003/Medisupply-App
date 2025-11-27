package com.misw.medisupply.data.remote.dto.customer

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for customer registration request
 * Used when registering a new customer in the system
 */
data class CreateCustomerRequest(
    @SerializedName("business_name")
    val businessName: String,
    
    @SerializedName("trade_name")
    val tradeName: String? = null,
    
    @SerializedName("document_number")
    val documentNumber: String,
    
    @SerializedName("document_type")
    val documentType: String = "NIT",
    
    @SerializedName("contact_name")
    val contactName: String? = null,
    
    @SerializedName("contact_email")
    val contactEmail: String,
    
    @SerializedName("contact_phone")
    val contactPhone: String,
    
    @SerializedName("address")
    val address: String,
    
    @SerializedName("neighborhood")
    val neighborhood: String? = null,
    
    @SerializedName("city")
    val city: String? = null,
    
    @SerializedName("department")
    val department: String? = null,
    
    @SerializedName("country")
    val country: String = "Colombia",
    
    @SerializedName("latitude")
    val latitude: Double? = null,
    
    @SerializedName("longitude")
    val longitude: Double? = null,
    
    @SerializedName("credit_limit")
    val creditLimit: Double = 60000000.0,
    
    @SerializedName("credit_days")
    val creditDays: Int = 90,
       
    @SerializedName("customer_type")
    val customerType: String = "hospital",

    @SerializedName("salesperson_id")
    val salespersonId: Int? = null,

    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String
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