package com.misw.medisupply.domain.usecase.order

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.common.PaginatedResult
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.repository.order.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting a paginated list of orders
 * Encapsulates the business logic for retrieving orders with optional filters and pagination
 */
class GetOrdersUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    /**
     * Execute the use case
     * 
     * @param sellerId Optional filter by seller ID
     * @param customerId Optional filter by customer ID
     * @param status Optional filter by order status
     * @param page Page number (default: 1, minimum: 1)
     * @param perPage Number of results per page (default: 20, minimum: 1, maximum: 100)
     * @return Flow emitting Resource with paginated result of orders
     */
    operator fun invoke(
        sellerId: String? = null,
        customerId: Int? = null,
        status: String? = null,
        page: Int = 1,
        perPage: Int = 20
    ): Flow<Resource<PaginatedResult<Order>>> {
        return repository.getOrders(sellerId, customerId, status, page, perPage)
    }
}
