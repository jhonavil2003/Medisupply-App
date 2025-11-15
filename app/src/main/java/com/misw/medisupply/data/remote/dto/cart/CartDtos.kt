package com.misw.medisupply.data.remote.dto.cart

import com.google.gson.annotations.SerializedName

/**
 * Request to reserve stock temporarily
 */
data class ReserveStockRequest(
    @SerializedName("product_sku")
    val productSku: String,
    
    @SerializedName("quantity")
    val quantity: Int,
    
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("session_id")
    val sessionId: String,
    
    @SerializedName("distribution_center_id")
    val distributionCenterId: Int = 1,
    
    @SerializedName("ttl_minutes")
    val ttlMinutes: Int = 15
)

/**
 * Response from reserve stock operation
 */
data class ReserveStockResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("reservation_id")
    val reservationId: Int?,
    
    @SerializedName("product_sku")
    val productSku: String,
    
    @SerializedName("quantity_reserved")
    val quantityReserved: Int?,
    
    @SerializedName("stock_available")
    val stockAvailable: Int?,
    
    @SerializedName("expires_at")
    val expiresAt: String?,
    
    @SerializedName("remaining_time_seconds")
    val remainingTimeSeconds: Int?,
    
    @SerializedName("message")
    val message: String,
    
    // Error fields
    @SerializedName("error")
    val error: String? = null,
    
    @SerializedName("requested_quantity")
    val requestedQuantity: Int? = null,
    
    @SerializedName("available_quantity")
    val availableQuantity: Int? = null
)

/**
 * Request to release reserved stock
 */
data class ReleaseStockRequest(
    @SerializedName("product_sku")
    val productSku: String,
    
    @SerializedName("quantity")
    val quantity: Int,
    
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("session_id")
    val sessionId: String
)

/**
 * Response from release stock operation
 */
data class ReleaseStockResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("product_sku")
    val productSku: String,
    
    @SerializedName("quantity_released")
    val quantityReleased: Int,
    
    @SerializedName("stock_available")
    val stockAvailable: Int,
    
    @SerializedName("message")
    val message: String
)

/**
 * Request to clear all cart reservations
 */
data class ClearCartRequest(
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("session_id")
    val sessionId: String
)

/**
 * Response from clear cart operation
 */
data class ClearCartResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("cleared_count")
    val clearedCount: Int,
    
    @SerializedName("products_affected")
    val productsAffected: List<String>,
    
    @SerializedName("message")
    val message: String
)

/**
 * Single cart reservation item
 */
data class CartReservationDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("product_sku")
    val productSku: String,
    
    @SerializedName("quantity_reserved")
    val quantityReserved: Int,
    
    @SerializedName("expires_at")
    val expiresAt: String,
    
    @SerializedName("remaining_time_seconds")
    val remainingTimeSeconds: Int,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("distribution_center_id")
    val distributionCenterId: Int
)

/**
 * Response with user's cart reservations
 */
data class UserReservationsResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("reservations")
    val reservations: List<CartReservationDto>,
    
    @SerializedName("total_count")
    val totalCount: Int
)

/**
 * Distribution center stock in real-time response
 */
data class RealTimeDistributionCenterDto(
    @SerializedName("distribution_center_id")
    val distributionCenterId: Int,
    
    @SerializedName("distribution_center_code")
    val distributionCenterCode: String,
    
    @SerializedName("distribution_center_name")
    val distributionCenterName: String,
    
    @SerializedName("city")
    val city: String,
    
    @SerializedName("physical_stock")
    val physicalStock: Int,
    
    @SerializedName("reserved_in_carts")
    val reservedInCarts: Int,
    
    @SerializedName("available_for_purchase")
    val availableForPurchase: Int,
    
    @SerializedName("is_out_of_stock")
    val isOutOfStock: Boolean
)

/**
 * Single product real-time stock
 */
data class RealTimeProductStockDto(
    @SerializedName("product_sku")
    val productSku: String,
    
    @SerializedName("total_physical_stock")
    val totalPhysicalStock: Int,
    
    @SerializedName("total_reserved_in_carts")
    val totalReservedInCarts: Int,
    
    @SerializedName("total_available_for_purchase")
    val totalAvailableForPurchase: Int,
    
    @SerializedName("distribution_centers")
    val distributionCenters: List<RealTimeDistributionCenterDto>
)

/**
 * Response with real-time stock information
 * Can be for single or multiple products
 */
data class RealTimeStockResponse(
    // Single product response
    @SerializedName("product_sku")
    val productSku: String? = null,
    
    @SerializedName("total_physical_stock")
    val totalPhysicalStock: Int? = null,
    
    @SerializedName("total_reserved_in_carts")
    val totalReservedInCarts: Int? = null,
    
    @SerializedName("total_available_for_purchase")
    val totalAvailableForPurchase: Int? = null,
    
    @SerializedName("distribution_centers")
    val distributionCenters: List<RealTimeDistributionCenterDto>? = null,
    
    // Multiple products response
    @SerializedName("products")
    val products: List<RealTimeProductStockDto>? = null,
    
    @SerializedName("total_products")
    val totalProducts: Int? = null
)
