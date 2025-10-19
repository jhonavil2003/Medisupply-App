package com.misw.medisupply.domain.model.order

/**
 * Enum representing the status of an order
 * Matches the backend order status values
 */
enum class OrderStatus(val value: String, val displayName: String) {
    PENDING("pending", "Pendiente"),
    CONFIRMED("confirmed", "Confirmada"),
    PROCESSING("processing", "En Procesamiento"),
    SHIPPED("shipped", "Despachada"),
    DELIVERED("delivered", "Entregada"),
    CANCELLED("cancelled", "Cancelada");
    
    companion object {
        fun fromValue(value: String): OrderStatus {
            return values().find { it.value == value } ?: PENDING
        }
    }
}
