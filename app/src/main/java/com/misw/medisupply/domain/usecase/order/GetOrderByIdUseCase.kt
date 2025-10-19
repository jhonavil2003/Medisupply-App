package com.misw.medisupply.domain.usecase.order

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.repository.order.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting a single order by ID
 * Encapsulates the business logic for retrieving a specific order
 */
class GetOrderByIdUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    /**
     * Execute the use case
     * 
     * @param orderId The unique ID of the order to retrieve
     * @return Flow emitting Resource with order data
     */
    operator fun invoke(orderId: Int): Flow<Resource<Order>> {
        return repository.getOrderById(orderId)
    }
}
