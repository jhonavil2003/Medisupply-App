package com.misw.medisupply.domain.model.customer

import com.misw.medisupply.core.utils.FormatUtils
import java.util.Date

/**
 * Domain model representing a Customer
 * This model is used across the application and is independent of data sources
 */
data class Customer(
    val id: Int,
    val documentType: DocumentType,
    val documentNumber: String,
    val businessName: String,
    val tradeName: String?,
    val customerType: CustomerType,
    val contactName: String?,
    val contactEmail: String?,
    val contactPhone: String?,
    val address: String?,
    val city: String?,
    val department: String?,
    val country: String,
    val creditLimit: Double,
    val creditDays: Int,
    val isActive: Boolean,
    val createdAt: Date?,
    val updatedAt: Date?
) {
    /**
     * Get formatted credit limit as currency
     */
    fun getFormattedCreditLimit(): String {
        return FormatUtils.formatCurrency(creditLimit)
    }
    
    /**
     * Get display name (trade name or business name)
     */
    fun getDisplayName(): String {
        return tradeName?.takeIf { it.isNotBlank() } ?: businessName
    }
    
    /**
     * Get full contact info
     */
    fun getContactInfo(): String {
        val parts = mutableListOf<String>()
        contactName?.let { parts.add(it) }
        contactEmail?.let { parts.add(it) }
        contactPhone?.let { parts.add(it) }
        return parts.joinToString(" • ")
    }
    
    /**
     * Get full address
     */
    fun getFullAddress(): String {
        val parts = mutableListOf<String>()
        address?.let { parts.add(it) }
        city?.let { parts.add(it) }
        department?.let { parts.add(it) }
        return parts.joinToString(", ")
    }
}

/**
 * Enum representing customer types
 */
enum class CustomerType(val displayName: String) {
    HOSPITAL("Hospital"),
    CLINICA("Clínica"),
    FARMACIA("Farmacia"),
    DISTRIBUIDOR("Distribuidor"),
    IPS("IPS"),
    EPS("EPS");
    
    companion object {
        /**
         * Get CustomerType from string value
         */
        fun fromString(value: String): CustomerType {
            return entries.find { 
                it.name.equals(value, ignoreCase = true) 
            } ?: HOSPITAL
        }
    }
}

/**
 * Enum representing document types
 */
enum class DocumentType(val displayName: String) {
    NIT("NIT"),
    CC("Cédula de Ciudadanía"),
    CE("Cédula de Extranjería"),
    RUT("RUT"),
    DNI("DNI");
    
    companion object {
        /**
         * Get DocumentType from string value
         */
        fun fromString(value: String): DocumentType {
            return entries.find { 
                it.name.equals(value, ignoreCase = true) 
            } ?: NIT
        }
    }
}
