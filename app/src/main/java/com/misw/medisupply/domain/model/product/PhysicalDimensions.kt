package com.misw.medisupply.domain.model.product

/**
 * Physical dimensions for logistics and storage
 */
data class PhysicalDimensions(
    val weightKg: Float?,
    val lengthCm: Float?,
    val widthCm: Float?,
    val heightCm: Float?
)
