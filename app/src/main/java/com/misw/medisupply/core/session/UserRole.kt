package com.misw.medisupply.core.session

/**
 * User roles in the application
 * Determines which navigation flow and screens to show
 */
enum class UserRole {
    /**
     * Sales Force - Internal staff
     * Access to: Home, Visits, Orders, Performance
     */
    SALES_FORCE,
    
    /**
     * Customer Management - Self-service for clients
     * Access to: Home, Shop, Orders, Account
     */
    CUSTOMER_MANAGEMENT;
    
    companion object {
        /**
         * Convert string to UserRole
         * @param value String representation of the role
         * @return UserRole enum value
         */
        fun fromString(value: String): UserRole {
            return when (value.lowercase()) {
                "salesforce", "sales_force", "fuerza_ventas" -> SALES_FORCE
                "customer", "customer_management", "cliente" -> CUSTOMER_MANAGEMENT
                else -> CUSTOMER_MANAGEMENT // Default to customer
            }
        }
    }
    
    /**
     * Get display name for the role
     */
    fun getDisplayName(): String {
        return when (this) {
            SALES_FORCE -> "Fuerza de Ventas"
            CUSTOMER_MANAGEMENT -> "Cliente"
        }
    }
    
    /**
     * Get route string for navigation
     */
    fun toRouteString(): String {
        return when (this) {
            SALES_FORCE -> "salesforce"
            CUSTOMER_MANAGEMENT -> "customer"
        }
    }
}
