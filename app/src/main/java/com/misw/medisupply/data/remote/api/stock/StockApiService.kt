package com.misw.medisupply.data.remote.api.stock

import com.misw.medisupply.data.remote.dto.stock.MultipleStockLevelsDto
import com.misw.medisupply.data.remote.dto.stock.StockLevelDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Stock API Service
 * Defines endpoints for logistics service stock operations
 */
interface StockApiService {
    
    /**
     * Get stock levels for products
     * Can query single or multiple products
     * 
     * @param productSku Single product SKU (mutually exclusive with productSkus)
     * @param productSkus Comma-separated list of SKUs (mutually exclusive with productSku)
     * @param distributionCenterId Filter by specific distribution center
     * @param onlyAvailable Only return products with available stock > 0
     * @param includeReserved Include reserved quantities in response (default: true)
     * @param includeInTransit Include in-transit quantities in response (default: false)
     * @return Response with single product stock or multiple products stock
     */
    @GET("inventory/stock-levels")
    suspend fun getStockLevels(
        @Query("product_sku") productSku: String? = null,
        @Query("product_skus") productSkus: String? = null,
        @Query("distribution_center_id") distributionCenterId: Int? = null,
        @Query("only_available") onlyAvailable: Boolean? = null,
        @Query("include_reserved") includeReserved: Boolean? = true,
        @Query("include_in_transit") includeInTransit: Boolean? = false
    ): Response<Any> // Returns either StockLevelDto or MultipleStockLevelsDto
    
    /**
     * Get stock level for a single product (convenience method)
     */
    @GET("inventory/stock-levels")
    suspend fun getSingleProductStock(
        @Query("product_sku") productSku: String,
        @Query("distribution_center_id") distributionCenterId: Int? = null,
        @Query("include_reserved") includeReserved: Boolean? = true,
        @Query("include_in_transit") includeInTransit: Boolean? = false
    ): Response<StockLevelDto>
    
    /**
     * Get stock levels for multiple products (convenience method)
     */
    @GET("inventory/stock-levels")
    suspend fun getMultipleProductsStock(
        @Query("product_skus") productSkus: String,
        @Query("distribution_center_id") distributionCenterId: Int? = null,
        @Query("only_available") onlyAvailable: Boolean? = null,
        @Query("include_reserved") includeReserved: Boolean? = true,
        @Query("include_in_transit") includeInTransit: Boolean? = false
    ): Response<MultipleStockLevelsDto>
}
