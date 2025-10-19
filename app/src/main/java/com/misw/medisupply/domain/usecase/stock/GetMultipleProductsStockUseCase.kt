package com.misw.medisupply.domain.usecase.stock

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.stock.MultipleStockLevels
import com.misw.medisupply.domain.repository.stock.StockRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Get Multiple Products Stock Use Case
 * Retrieves stock information for multiple products
 */
class GetMultipleProductsStockUseCase @Inject constructor(
    private val repository: StockRepository
) {
    /**
     * Execute use case
     * 
     * @param productSkus List of product SKUs to query
     * @param distributionCenterId Optional: filter by specific distribution center
     * @param onlyAvailable Only return products with available stock > 0
     * @param includeReserved Include reserved quantities (default: true)
     * @param includeInTransit Include in-transit quantities (default: false)
     * @return Flow emitting Resource with MultipleStockLevels
     */
    operator fun invoke(
        productSkus: List<String>,
        distributionCenterId: Int? = null,
        onlyAvailable: Boolean? = null,
        includeReserved: Boolean = true,
        includeInTransit: Boolean = false
    ): Flow<Resource<MultipleStockLevels>> {
        return repository.getMultipleProductsStock(
            productSkus = productSkus,
            distributionCenterId = distributionCenterId,
            onlyAvailable = onlyAvailable,
            includeReserved = includeReserved,
            includeInTransit = includeInTransit
        )
    }
}
