package com.misw.medisupply.domain.repository.product

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.product.Pagination
import com.misw.medisupply.domain.model.product.Product
import kotlinx.coroutines.flow.Flow

/**
 * Product Repository Interface
 * Defines contract for product data operations
 */
interface ProductRepository {
    
    /**
     * Get products list with optional filters
     * @param search General search text
     * @param sku SKU filter
     * @param category Category filter
     * @param subcategory Subcategory filter
     * @param supplierId Supplier ID filter
     * @param isActive Active status filter
     * @param requiresColdChain Cold chain filter
     * @param page Page number
     * @param perPage Items per page
     * @return Flow with Resource containing products list and pagination
     */
    fun getProducts(
        search: String? = null,
        sku: String? = null,
        category: String? = null,
        subcategory: String? = null,
        supplierId: Int? = null,
        isActive: Boolean? = true,
        requiresColdChain: Boolean? = null,
        page: Int? = 1,
        perPage: Int? = 20
    ): Flow<Resource<Pair<List<Product>, Pagination>>>
    
    /**
     * Get product by SKU
     * @param sku Product SKU
     * @return Flow with Resource containing product details
     */
    fun getProductBySku(sku: String): Flow<Resource<Product>>
}
