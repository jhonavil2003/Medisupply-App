package com.misw.medisupply.data.remote.dto.product

import com.google.gson.annotations.SerializedName

/**
 * Storage conditions DTO
 */
data class StorageConditionsDto(
    @SerializedName("temperature_min")
    val temperatureMin: Float?,
    @SerializedName("temperature_max")
    val temperatureMax: Float?,
    @SerializedName("humidity_max")
    val humidityMax: Float?
)
