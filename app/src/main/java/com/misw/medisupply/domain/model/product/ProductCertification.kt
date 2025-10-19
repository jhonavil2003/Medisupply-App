package com.misw.medisupply.domain.model.product

/**
 * Product certification (ISO, CE, FDA, etc.)
 */
data class ProductCertification(
    val id: Int,
    val certificationType: String,
    val certificationNumber: String,
    val issuingAuthority: String,
    val issueDate: String,
    val expirationDate: String,
    val isValid: Boolean,
    val documentUrl: String?,
    val createdAt: String
)
