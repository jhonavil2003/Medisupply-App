package com.misw.medisupply.data.remote.dto.salesperson

import com.google.gson.annotations.SerializedName
import com.misw.medisupply.domain.model.salesperson.Salesperson

/**
 * Data Transfer Object for Salesperson from API
 * Used when salesperson data is embedded in customer responses
 */
data class SalespersonDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("employee_id")
    val employeeId: String?,
    
    @SerializedName("full_name")
    val fullName: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("phone")
    val phone: String?,
    
    @SerializedName("territory")
    val territory: String?
)

/**
 * Extension function to convert SalespersonDto to Domain Salesperson model
 */
fun SalespersonDto.toDomain(): Salesperson {
    // Split full_name into firstName and lastName
    val nameParts = fullName.trim().split(" ", limit = 2)
    val firstName = nameParts.getOrNull(0) ?: ""
    val lastName = nameParts.getOrNull(1) ?: ""
    
    return Salesperson(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
        territory = territory,
        isActive = true // Assuming if returned by API, they are active
    )
}
