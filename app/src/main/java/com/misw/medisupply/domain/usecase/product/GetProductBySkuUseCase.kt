package com.misw.medisupply.domain.usecase.product

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.product.Product
import com.misw.medisupply.domain.repository.product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Get Product By SKU Use Case
 * Retrieves detailed product information by SKU
 * Includes certifications and regulatory conditions
 */
class GetProductBySkuUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    /**
     * Invoke use case to get product by SKU
     * @param sku Product SKU
     * @return Flow with Resource containing product details
     */
    operator fun invoke(sku: String): Flow<Resource<Product>> {
        return repository.getProductBySku(sku)
    }
}
