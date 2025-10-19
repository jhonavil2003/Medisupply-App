package com.misw.medisupply.domain.model.product

/**
 * Storage conditions for product preservation
 * Defines temperature and humidity requirements
 */
data class StorageConditions(
    val temperatureMin: Float?,
    val temperatureMax: Float?,
    val humidityMax: Float?
)
