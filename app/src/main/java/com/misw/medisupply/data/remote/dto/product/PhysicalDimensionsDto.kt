package com.misw.medisupply.data.remote.dto.product

import com.google.gson.annotations.SerializedName

/**
 * Physical dimensions DTO
 */
data class PhysicalDimensionsDto(
    @SerializedName("weight_kg")
    val weightKg: Float?,
    @SerializedName("length_cm")
    val lengthCm: Float?,
    @SerializedName("width_cm")
    val widthCm: Float?,
    @SerializedName("height_cm")
    val heightCm: Float?
)
