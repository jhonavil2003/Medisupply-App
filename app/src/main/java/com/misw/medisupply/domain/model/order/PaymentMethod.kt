package com.misw.medisupply.domain.model.order

/**
 * Enum representing payment methods for an order
 * Matches the backend payment_method values
 */
enum class PaymentMethod(val value: String, val displayName: String) {
    TRANSFER("transferencia", "Transferencia Bancaria"),
    CHECK("cheque", "Cheque"),
    CASH("efectivo", "Efectivo"),
    CARD("tarjeta", "Tarjeta");
    
    companion object {
        fun fromValue(value: String?): PaymentMethod? {
            return values().find { it.value == value }
        }
    }
}
