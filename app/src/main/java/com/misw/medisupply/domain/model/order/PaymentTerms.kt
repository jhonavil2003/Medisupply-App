package com.misw.medisupply.domain.model.order

/**
 * Enum representing payment terms for an order
 * Matches the backend payment_terms values
 */
enum class PaymentTerms(val value: String, val displayName: String, val days: Int) {
    CASH("contado", "Contado", 0),
    CREDIT_30("credito_30", "Crédito 30 días", 30),
    CREDIT_45("credito_45", "Crédito 45 días", 45),
    CREDIT_60("credito_60", "Crédito 60 días", 60),
    CREDIT_90("credito_90", "Crédito 90 días", 90);
    
    companion object {
        fun fromValue(value: String): PaymentTerms {
            return values().find { it.value == value } ?: CASH
        }
    }
}
