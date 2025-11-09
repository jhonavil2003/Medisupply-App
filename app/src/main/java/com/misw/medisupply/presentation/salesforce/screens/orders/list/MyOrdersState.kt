package com.misw.medisupply.presentation.salesforce.screens.orders.list

import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderStatus

/**
 * UI State for My Orders Screen with pagination support
 */
data class MyOrdersState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val orders: List<Order> = emptyList(),
    val selectedStatus: OrderStatus? = null,
    val error: String? = null,
    val selectedOrder: Order? = null,
    
    // Pagination state
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val totalOrders: Int = 0,
    val perPage: Int = 20,
    val hasMore: Boolean = false
) {
    /**
     * Check if there are any orders
     */
    fun hasOrders(): Boolean = orders.isNotEmpty()
    
    /**
     * Get orders to display (no local filtering - filter is applied on backend)
     */
    fun getFilteredOrders(): List<Order> {
        return orders
    }
    
    /**
     * Get count of orders by status (from current page only)
     */
    fun getOrderCountByStatus(status: OrderStatus): Int {
        return orders.count { it.status == status }
    }
    
    /**
     * Get total count of loaded orders
     */
    fun getTotalOrderCount(): Int = orders.size
    
    /**
     * Check if we can load more orders
     */
    fun canLoadMore(): Boolean = hasMore && !isLoading && !isLoadingMore && !isRefreshing
}

/**
 * Events that can be triggered from My Orders Screen
 */
sealed class MyOrdersEvent {
    /**
     * Load orders from repository (first page)
     */
    object LoadOrders : MyOrdersEvent()
    
    /**
     * Load next page of orders
     */
    object LoadNextPage : MyOrdersEvent()
    
    /**
     * Load previous page of orders
     */
    object LoadPreviousPage : MyOrdersEvent()
    
    /**
     * Refresh orders (pull to refresh - resets to page 1)
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
