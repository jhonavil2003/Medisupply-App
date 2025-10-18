package com.misw.medisupply.domain.usecase.order

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.repository.order.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for deleting an order
 * Encapsulates the business logic for order deletion
 */
class DeleteOrderUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    /**
     * Delete an order by ID
     * 
     * @param orderId ID of the order to delete
     * @return Flow emitting Resource with success status
     */
    operator fun invoke(orderId: Int): Flow<Resource<Unit>> {
        return repository.deleteOrder(orderId)
    }
}
