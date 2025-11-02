package com.misw.medisupply.domain.model.salesperson

/**
 * Domain model representing a salesperson
 */
data class Salesperson(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val territory: String?,
    val isActive: Boolean = true
) {
    val fullName: String
        get() = "$firstName $lastName"
}