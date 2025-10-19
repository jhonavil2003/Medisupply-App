package com.misw.medisupply.data.remote.dto.stock

import com.google.gson.annotations.SerializedName
import com.misw.medisupply.domain.model.stock.MultipleStockLevels

/**
 * Multiple Stock Levels Response DTO
 * Maps JSON response for multiple products from logistics service API
 */
data class MultipleStockLevelsDto(
    @SerializedName("products")
    val products: List<StockLevelDto>,
    
    @SerializedName("total_products")
    val totalProducts: Int
) {
    /**
     * Convert DTO to domain model
     */
    fun toDomain(): MultipleStockLevels {
        return MultipleStockLevels(
            products = products.map { it.toDomain() },
            totalProducts = totalProducts
        )
    }
}
