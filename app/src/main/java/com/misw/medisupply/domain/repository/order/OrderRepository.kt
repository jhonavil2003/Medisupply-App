package com.misw.medisupply.domain.repository.order

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.order.Order
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Order operations
 * Defines the contract for data access
 */
interface OrderRepository {
    
    /**
     * Get list of orders with optional filters
     * 
     * @param sellerId Filter by seller ID
     * @param customerId Filter by customer ID
     * @param status Filter by order status
     * @return Flow emitting Resource with list of orders
     */
    fun getOrders(
        sellerId: String? = null,
        customerId: Int? = null,
        status: String? = null
    ): Flow<Resource<List<Order>>>
    
    /**
     * Delete an order by ID
     * 
     * @param orderId ID of the order to delete
     * @return Flow emitting Resource with success status
     */
    fun deleteOrder(orderId: Int): Flow<Resource<Unit>>
}
