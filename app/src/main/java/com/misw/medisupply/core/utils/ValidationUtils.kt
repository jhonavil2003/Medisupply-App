package com.misw.medisupply.core.utils

import android.util.Patterns

/**
 * Validation utilities for common fields
 */
object ValidationUtils {
    
    /**
     * Validate Colombian NIT format
     * Format: XXXXXXXXX-X
     */
    fun isValidNit(nit: String): Boolean {
        val nitPattern = Regex("^\\d{9}-\\d\$")
        return nitPattern.matches(nit)
    }
    
    /**
     * Validate Colombian phone number
     * Accepts formats: +57XXXXXXXXXX, 3XXXXXXXXX, etc.
     */
    fun isValidColombianPhone(phone: String): Boolean {
        val cleanPhone = phone.replace(Regex("[\\s\\-()]"), "")
        return cleanPhone.matches(Regex("^(\\+57)?[0-9]{10}\$"))
    }
    
    /**
     * Validate email format
     */
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Validate that field is not empty
     */
    fun isNotEmpty(value: String?): Boolean {
        return !value.isNullOrBlank()
    }
    
    /**
     * Validate minimum length
     */
    fun hasMinLength(value: String, minLength: Int): Boolean {
        return value.length >= minLength
    }
    
    /**
     * Validate maximum length
     */
    fun hasMaxLength(value: String, maxLength: Int): Boolean {
        return value.length <= maxLength
    }
    
    /**
     * Validate that value is a positive number
     */
    fun isPositiveNumber(value: Double): Boolean {
        return value > 0
    }
    
    /**
     * Validate that value is within range
     */
    fun isInRange(value: Double, min: Double, max: Double): Boolean {
        return value in min..max
    }
    
    /**
     * Validate credit limit (must be positive or zero)
     */
    fun isValidCreditLimit(limit: Double): Boolean {
        return limit >= 0
    }
    
    /**
     * Validate credit days (must be between 0 and 365)
     */
    fun isValidCreditDays(days: Int): Boolean {
        return days in 0..365
    }
    
    /**
     * Validate address (minimum 10 characters)
     */
    fun isValidAddress(address: String): Boolean {
        return address.length >= 10
    }
    
    /**
     * Get validation error message
     */
    fun getErrorMessage(field: String, validationType: ValidationType): String {
        return when (validationType) {
            ValidationType.REQUIRED -> "$field es requerido"
            ValidationType.INVALID_FORMAT -> "$field tiene un formato inválido"
            ValidationType.TOO_SHORT -> "$field es muy corto"
            ValidationType.TOO_LONG -> "$field es muy largo"
            ValidationType.INVALID_RANGE -> "$field está fuera del rango permitido"
            ValidationType.INVALID_EMAIL -> "Email inválido"
            ValidationType.INVALID_PHONE -> "Teléfono inválido"
            ValidationType.INVALID_NIT -> "NIT inválido"
        }
    }
    
    enum class ValidationType {
        REQUIRED,
        INVALID_FORMAT,
        TOO_SHORT,
        TOO_LONG,
        INVALID_RANGE,
        INVALID_EMAIL,
        INVALID_PHONE,
        INVALID_NIT
    }
}
