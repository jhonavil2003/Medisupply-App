package com.misw.medisupply.presentation.salesforce.screens.orders.list

import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderStatus

/**
 * UI State for My Orders Screen
 */
data class MyOrdersState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val orders: List<Order> = emptyList(),
    val selectedStatus: OrderStatus? = null,
    val error: String? = null,
    val selectedOrder: Order? = null
) {
    /**
     * Check if there are any orders
     */
    fun hasOrders(): Boolean = orders.isNotEmpty()
    
    /**
     * Get filtered orders based on selected status
     */
    fun getFilteredOrders(): List<Order> {
        return if (selectedStatus != null) {
            orders.filter { it.status == selectedStatus }
        } else {
            orders
        }
    }
    
    /**
     * Get count of orders by status
     */
    fun getOrderCountByStatus(status: OrderStatus): Int {
        return orders.count { it.status == status }
    }
    
    /**
     * Get total count of orders
     */
    fun getTotalOrderCount(): Int = orders.size
}

/**
 * Events that can be triggered from My Orders Screen
 */
sealed class MyOrdersEvent {
    /**
     * Load orders from repository
     */
    object LoadOrders : MyOrdersEvent()
    
    /**
     * Refresh orders (pull to refresh)
     */
    object RefreshOrders : MyOrdersEvent()
    
    /**
     * Filter orders by status
     */
    data class FilterByStatus(val status: OrderStatus?) : MyOrdersEvent()
    
    /**
     * Select an order to view details
     */
    data class SelectOrder(val order: Order) : MyOrdersEvent()
    
    /**
     * Clear error message
     */
    object ClearError : MyOrdersEvent()
}
