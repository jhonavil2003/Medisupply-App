package com.misw.medisupply.domain.model.product

/**
 * Regulatory condition for product handling
 * Defines mandatory requirements for storage, distribution, and usage
 */
data class ProductRegulatoryCondition(
    val id: Int,
    val conditionType: String,
    val description: String,
    val isMandatory: Boolean,
    val createdAt: String
)
