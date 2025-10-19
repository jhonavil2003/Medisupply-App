package com.misw.medisupply.domain.model.product

/**
 * Pagination metadata for product lists
 */
data class Pagination(
    val page: Int,
    val perPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val hasNext: Boolean,
    val hasPrev: Boolean
)
