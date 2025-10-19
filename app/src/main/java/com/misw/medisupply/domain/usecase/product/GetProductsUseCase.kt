package com.misw.medisupply.domain.usecase.product

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.product.Pagination
import com.misw.medisupply.domain.model.product.Product
import com.misw.medisupply.domain.repository.product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Get Products Use Case
 * Retrieves products list with optional filters and pagination
 */
class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    /**
     * Invoke use case to get products
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
    operator fun invoke(
        search: String? = null,
        sku: String? = null,
        category: String? = null,
        subcategory: String? = null,
        supplierId: Int? = null,
        isActive: Boolean? = true,
        requiresColdChain: Boolean? = null,
        page: Int? = 1,
        perPage: Int? = 20
    ): Flow<Resource<Pair<List<Product>, Pagination>>> {
        return repository.getProducts(
            search = search,
            sku = sku,
            category = category,
            subcategory = subcategory,
            supplierId = supplierId,
            isActive = isActive,
            requiresColdChain = requiresColdChain,
            page = page,
            perPage = perPage
        )
    }
}
