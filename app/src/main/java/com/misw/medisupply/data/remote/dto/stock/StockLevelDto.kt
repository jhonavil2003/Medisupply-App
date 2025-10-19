package com.misw.medisupply.data.remote.dto.stock

import com.google.gson.annotations.SerializedName
import com.misw.medisupply.domain.model.stock.StockLevel

/**
 * Stock Level DTO for single product
 * Maps JSON response from logistics service API
 */
data class StockLevelDto(
    @SerializedName("product_sku")
    val productSku: String,
    
    @SerializedName("total_available")
    val totalAvailable: Int,
    
    @SerializedName("total_reserved")
    val totalReserved: Int? = null,
    
    @SerializedName("total_in_transit")
    val totalInTransit: Int? = null,
    
    @SerializedName("distribution_centers")
    val distributionCenters: List<DistributionCenterDto>
) {
    /**
     * Convert DTO to domain model
     */
    fun toDomain(): StockLevel {
        return StockLevel(
            productSku = productSku,
            totalAvailable = totalAvailable,
            totalReserved = totalReserved,
            totalInTransit = totalInTransit,
            distributionCenters = distributionCenters.map { it.toDomain() }
        )
    }
}
