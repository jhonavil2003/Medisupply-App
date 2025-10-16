package com.misw.medisupply.data.remote.dto.customer

import com.google.gson.annotations.SerializedName
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.model.customer.DocumentType
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Data Transfer Object for Customer from API
 * Matches the API response structure exactly
 */
data class CustomerDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("document_type")
    val documentType: String,
    
    @SerializedName("document_number")
    val documentNumber: String,
    
    @SerializedName("business_name")
    val businessName: String,
    
    @SerializedName("trade_name")
    val tradeName: String?,
    
    @SerializedName("customer_type")
    val customerType: String,
    
    @SerializedName("contact_name")
    val contactName: String?,
    
    @SerializedName("contact_email")
    val contactEmail: String?,
    
    @SerializedName("contact_phone")
    val contactPhone: String?,
    
    @SerializedName("address")
    val address: String?,
    
    @SerializedName("city")
    val city: String?,
    
    @SerializedName("department")
    val department: String?,
    
    @SerializedName("country")
    val country: String,
    
    @SerializedName("credit_limit")
    val creditLimit: Double,
    
    @SerializedName("credit_days")
    val creditDays: Int,
    
    @SerializedName("is_active")
    val isActive: Boolean,
    
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("updated_at")
    val updatedAt: String?
)

/**
 * Parse ISO 8601 date string
 */
private fun String.parseIso8601() = try {
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(this)
} catch (e: Exception) {
    null
}

/**
 * Extension function to convert CustomerDto to Domain Customer model
 */
fun CustomerDto.toDomain(): Customer {
    return Customer(
        id = id,
        documentType = DocumentType.fromString(documentType),
        documentNumber = documentNumber,
        businessName = businessName,
        tradeName = tradeName,
        customerType = CustomerType.fromString(customerType),
        contactName = contactName,
        contactEmail = contactEmail,
        contactPhone = contactPhone,
        address = address,
        city = city,
        department = department,
        country = country,
        creditLimit = creditLimit,
        creditDays = creditDays,
        isActive = isActive,
        createdAt = createdAt?.parseIso8601(),
        updatedAt = updatedAt?.parseIso8601()
    )
}

/**
 * Extension function to convert list of CustomerDto to list of Customer
 */
fun List<CustomerDto>.toDomain(): List<Customer> {
    return this.map { it.toDomain() }
}
