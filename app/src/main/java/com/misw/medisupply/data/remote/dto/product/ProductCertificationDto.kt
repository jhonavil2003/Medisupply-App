package com.misw.medisupply.data.remote.dto.product

import com.google.gson.annotations.SerializedName

/**
 * Product certification DTO
 */
data class ProductCertificationDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("certification_type")
    val certificationType: String,
    @SerializedName("certification_number")
    val certificationNumber: String,
    @SerializedName("issuing_authority")
    val issuingAuthority: String,
    @SerializedName("issue_date")
    val issueDate: String,
    @SerializedName("expiration_date")
    val expirationDate: String,
    @SerializedName("is_valid")
    val isValid: Boolean,
    @SerializedName("document_url")
    val documentUrl: String?,
    @SerializedName("created_at")
    val createdAt: String
)
