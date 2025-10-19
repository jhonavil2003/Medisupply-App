package com.misw.medisupply.domain.repository.stock

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.stock.MultipleStockLevels
import com.misw.medisupply.domain.model.stock.StockLevel
import kotlinx.coroutines.flow.Flow

/**
 * Stock Repository Interface
 * Defines contract for stock data operations
 */
interface StockRepository {
    
    /**
     * Get stock level for a single product
     * 
     * @param productSku Product SKU to query
     * @param distributionCenterId Optional: filter by specific distribution center
     * @param includeReserved Include reserved quantities (default: true)
     * @param includeInTransit Include in-transit quantities (default: false)
     * @return Flow emitting Resource with StockLevel
     */
    fun getProductStock(
        productSku: String,
        distributionCenterId: Int? = null,
        includeReserved: Boolean = true,
        includeInTransit: Boolean = false
    ): Flow<Resource<StockLevel>>
    
    /**
     * Get stock levels for multiple products
     * 
     * @param productSkus List of product SKUs to query
     * @param distributionCenterId Optional: filter by specific distribution center
     * @param onlyAvailable Only return products with available stock > 0
     * @param includeReserved Include reserved quantities (default: true)
     * @param includeInTransit Include in-transit quantities (default: false)
     * @return Flow emitting Resource with MultipleStockLevels
     */
    fun getMultipleProductsStock(
        productSkus: List<String>,
        distributionCenterId: Int? = null,
        onlyAvailable: Boolean? = null,
        includeReserved: Boolean = true,
        includeInTransit: Boolean = false
    ): Flow<Resource<MultipleStockLevels>>
}
