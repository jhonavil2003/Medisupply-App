package com.misw.medisupply.domain.model.product

/**
 * Regulatory information for medical products
 * Includes sanitary registration and prescription requirements
 */
data class RegulatoryInfo(
    val sanitaryRegistration: String?,
    val requiresPrescription: Boolean?,
    val regulatoryClass: String?
)
