package com.misw.medisupply.domain.model.route

/**
 * Representa una ubicación geográfica con nombre
 */
data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double
)
