package com.misw.medisupply.data.remote.dto.stock

import com.google.gson.annotations.SerializedName
import com.misw.medisupply.domain.model.stock.DistributionCenter

/**
 * Distribution Center DTO
 * Maps JSON response from logistics service API
 */
data class DistributionCenterDto(
    @SerializedName("distribution_center_id")
    val distributionCenterId: Int,
    
    @SerializedName("distribution_center_code")
    val distributionCenterCode: String,
    
    @SerializedName("distribution_center_name")
    val distributionCenterName: String,
    
    @SerializedName("city")
    val city: String,
    
    @SerializedName("quantity_available")
    val quantityAvailable: Int,
    
    @SerializedName("quantity_reserved")
    val quantityReserved: Int? = null,
    
    @SerializedName("quantity_in_transit")
    val quantityInTransit: Int? = null,
    
    @SerializedName("is_low_stock")
    val isLowStock: Boolean,
    
    @SerializedName("is_out_of_stock")
    val isOutOfStock: Boolean
) {
    /**
     * Convert DTO to domain model
     */
    fun toDomain(): DistributionCenter {
        return DistributionCenter(
            id = distributionCenterId,
            code = distributionCenterCode,
            name = distributionCenterName,
            city = city,
            quantityAvailable = quantityAvailable,
            quantityReserved = quantityReserved,
            quantityInTransit = quantityInTransit,
            isLowStock = isLowStock,
            isOutOfStock = isOutOfStock
        )
    }
}
