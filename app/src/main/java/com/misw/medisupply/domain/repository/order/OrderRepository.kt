package com.misw.medisupply.domain.repository.order

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.PaymentMethod
import com.misw.medisupply.domain.model.order.PaymentTerms
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Order operations
 * Defines the contract for order data operations
 */
interface OrderRepository {
    
    /**
     * Create a new order
     * 
     * @param customerId ID of the customer placing the order
     * @param sellerId ID of the seller
     * @param sellerName Name of the seller
     * @param items List of order items (productSku, quantity, discountPercentage, taxPercentage)
     * @param paymentTerms Payment terms for the order
     * @param paymentMethod Payment method (optional)
     * @param deliveryAddress Delivery address (optional)
     * @param deliveryCity Delivery city (optional)
     * @param deliveryDepartment Delivery department (optional)
     * @param preferredDistributionCenter Preferred distribution center code (optional)
     * @param notes Additional notes (optional)
     * @return Flow emitting Resource with created Order
     */
    fun createOrder(
        customerId: Int,
        sellerId: String,
        sellerName: String?,
        items: List<OrderItemRequest>,
        paymentTerms: PaymentTerms,
        paymentMethod: PaymentMethod?,
        deliveryAddress: String?,
        deliveryCity: String?,
        deliveryDepartment: String?,
        deliveryDate: String?,
        preferredDistributionCenter: String?,
        notes: String?
    ): Flow<Resource<Order>>
    
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
     * Get a single order by ID
     * 
     * @param orderId ID of the order to retrieve
     * @return Flow emitting Resource with order data
     */
    fun getOrderById(orderId: Int): Flow<Resource<Order>>
    
    /**
     * Delete an order by ID
     * 
     * @param orderId ID of the order to delete
     * @return Flow emitting Resource with deletion result
     */
    fun deleteOrder(orderId: Int): Flow<Resource<Unit>>
    
    /**
     * Update an existing order
     * 
     * @param orderId ID of the order to update
     * @param customerId ID of the customer
     * @param items Updated list of order items
     * @param paymentTerms Payment terms for the order
     * @param paymentMethod Payment method (optional)
     * @param deliveryAddress Delivery address (optional)
     * @param deliveryCity Delivery city (optional)
     * @param deliveryDepartment Delivery department (optional)
     * @param preferredDistributionCenter Preferred distribution center code (optional)
     * @param notes Additional notes (optional)
     * @return Flow emitting Resource with updated Order
     */
    fun updateOrder(
        orderId: Int,
        customerId: Int,
        items: List<OrderItemRequest>,
        paymentTerms: PaymentTerms,
        paymentMethod: PaymentMethod?,
        deliveryAddress: String?,
        deliveryCity: String?,
        deliveryDepartment: String?,
        deliveryDate: String?,
        preferredDistributionCenter: String?,
        notes: String?
    ): Flow<Resource<Order>>
}

/**
 * Data class representing an order item request
 */
data class OrderItemRequest(
    val productSku: String,
    val quantity: Int,
    val discountPercentage: Double = 0.0,
    val taxPercentage: Double = 19.0
)
