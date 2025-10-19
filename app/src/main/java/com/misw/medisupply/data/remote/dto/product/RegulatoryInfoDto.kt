package com.misw.medisupply.data.remote.dto.product

import com.google.gson.annotations.SerializedName

/**
 * Regulatory information DTO
 */
data class RegulatoryInfoDto(
    @SerializedName("sanitary_registration")
    val sanitaryRegistration: String?,
    @SerializedName("requires_prescription")
    val requiresPrescription: Boolean?,
    @SerializedName("regulatory_class")
    val regulatoryClass: String?
)
