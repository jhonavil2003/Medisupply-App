package com.misw.medisupply.data.remote.api.product

import com.misw.medisupply.data.remote.dto.product.ProductDto
import com.misw.medisupply.data.remote.dto.product.ProductsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Product API Service
 * Catalog service endpoints for product management
 */
interface ProductApiService {
    
    /**
     * Get products list with optional filters
     * @param search General search in name, description, SKU, barcode, manufacturer
     * @param sku Filter by SKU (partial match)
     * @param category Filter by category (partial match)
     * @param subcategory Filter by subcategory (partial match)
     * @param supplierId Filter by supplier ID
     * @param isActive Filter by active status (default: true)
     * @param requiresColdChain Filter products requiring cold chain
     * @param page Page number for pagination (default: 1)
     * @param perPage Items per page (default: 20, max: 100)
     * @return Response with products list and pagination metadata
     */
    @GET("products")
    suspend fun getProducts(
        @Query("search") search: String? = null,
        @Query("sku") sku: String? = null,
        @Query("category") category: String? = null,
        @Query("subcategory") subcategory: String? = null,
        @Query("supplier_id") supplierId: Int? = null,
        @Query("is_active") isActive: Boolean? = true,
        @Query("requires_cold_chain") requiresColdChain: Boolean? = null,
        @Query("page") page: Int? = 1,
        @Query("per_page") perPage: Int? = 20
    ): Response<ProductsResponse>
    
    /**
     * Get product by SKU
     * @param sku Product SKU
     * @return Response with product details including certifications and regulatory conditions
     */
    @GET("products/{sku}")
    suspend fun getProductBySku(
        @Path("sku") sku: String
    ): Response<ProductDto>
}
