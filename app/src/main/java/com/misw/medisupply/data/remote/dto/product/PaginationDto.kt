package com.misw.medisupply.data.remote.dto.product

import com.google.gson.annotations.SerializedName

/**
 * Pagination response DTO
 */
data class PaginationDto(
    @SerializedName("page")
    val page: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_items")
    val totalItems: Int,
    @SerializedName("has_next")
    val hasNext: Boolean,
    @SerializedName("has_prev")
    val hasPrev: Boolean
)
