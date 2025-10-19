package com.misw.medisupply.data.remote.dto.product

import com.google.gson.annotations.SerializedName

/**
 * Product regulatory condition DTO
 */
data class ProductRegulatoryConditionDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("condition_type")
    val conditionType: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("is_mandatory")
    val isMandatory: Boolean,
    @SerializedName("created_at")
    val createdAt: String
)
