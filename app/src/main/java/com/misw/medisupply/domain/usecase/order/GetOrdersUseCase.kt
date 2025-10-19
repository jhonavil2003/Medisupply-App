package com.misw.medisupply.domain.usecase.order

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.repository.order.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting a list of orders
 * Encapsulates the business logic for retrieving orders with optional filters
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
     * @return Flow emitting Resource with list of orders
     */
    operator fun invoke(
        sellerId: String? = null,
        customerId: Int? = null,
        status: String? = null
    ): Flow<Resource<List<Order>>> {
        return repository.getOrders(sellerId, customerId, status)
    }
}
