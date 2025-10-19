package com.misw.medisupply.data.remote.dto.product

import com.google.gson.annotations.SerializedName
import com.misw.medisupply.domain.model.product.Pagination

/**
 * Products list response from catalog service
 */
data class ProductsResponse(
    @SerializedName("products")
    val products: List<ProductDto>,
    @SerializedName("pagination")
    val pagination: PaginationDto
) {
    /**
     * Convert pagination DTO to domain model
     */
    fun getPaginationDomain(): Pagination {
        return Pagination(
            page = pagination.page,
            perPage = pagination.perPage,
            totalPages = pagination.totalPages,
            totalItems = pagination.totalItems,
            hasNext = pagination.hasNext,
            hasPrev = pagination.hasPrev
        )
    }
}
