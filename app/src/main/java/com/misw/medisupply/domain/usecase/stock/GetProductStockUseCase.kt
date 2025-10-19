package com.misw.medisupply.domain.usecase.stock

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.stock.StockLevel
import com.misw.medisupply.domain.repository.stock.StockRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Get Product Stock Use Case
 * Retrieves stock information for a single product
 */
class GetProductStockUseCase @Inject constructor(
    private val repository: StockRepository
) {
    /**
     * Execute use case
     * 
     * @param productSku Product SKU to query
     * @param distributionCenterId Optional: filter by specific distribution center
     * @param includeReserved Include reserved quantities (default: true)
     * @param includeInTransit Include in-transit quantities (default: false)
     * @return Flow emitting Resource with StockLevel
     */
    operator fun invoke(
        productSku: String,
        distributionCenterId: Int? = null,
        includeReserved: Boolean = true,
        includeInTransit: Boolean = false
    ): Flow<Resource<StockLevel>> {
        return repository.getProductStock(
            productSku = productSku,
            distributionCenterId = distributionCenterId,
            includeReserved = includeReserved,
            includeInTransit = includeInTransit
        )
    }
}
