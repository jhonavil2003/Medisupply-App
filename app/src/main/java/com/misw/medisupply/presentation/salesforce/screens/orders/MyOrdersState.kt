package com.misw.medisupply.presentation.salesforce.screens.orders

import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderStatus

/**
 * UI State for My Orders Screen
 * Represents all possible states of the my orders screen
 */
data class MyOrdersState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null,
    val selectedStatus: OrderStatus? = null,
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,
    val sellerId: String = "SELLER-001" // TODO: Get from session/auth
) {
    /**
     * Check if there are orders to display
     */
    fun hasOrders(): Boolean = orders.isNotEmpty()
    
    /**
     * Get filtered orders based on selected status and search query
     */
    fun getFilteredOrders(): List<Order> {
        var filtered = orders
        
        // Filter by status if selected
        if (selectedStatus != null) {
            filtered = filtered.filter { it.status == selectedStatus }
        }
        
        // Filter by search query
        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter { order ->
                order.orderNumber.contains(searchQuery, ignoreCase = true) ||
                order.sellerName.contains(searchQuery, ignoreCase = true) ||
                order.deliveryCity.contains(searchQuery, ignoreCase = true) ||
                order.getStatusDisplayName().contains(searchQuery, ignoreCase = true)
            }
        }
        
        return filtered
    }
    
    /**
     * Get orders count by status
     */
    fun getOrdersCountByStatus(status: OrderStatus): Int {
        return orders.count { it.status == status }
    }
    
    /**
     * Get total orders count
     */
    fun getTotalOrdersCount(): Int = orders.size
    
    /**
     * Get total amount of all orders
     */
    fun getTotalAmount(): Double {
        return orders.sumOf { it.totalAmount }
    }
    
    /**
     * Get formatted total amount
     */
    fun getFormattedTotalAmount(): String {
        return "$ ${String.format("%,.2f", getTotalAmount())}"
    }
}

/**
 * Sealed class representing user events/actions on My Orders Screen
 */
sealed class MyOrdersEvent {
    object LoadOrders : MyOrdersEvent()
    object RefreshOrders : MyOrdersEvent()
    data class FilterByStatus(val status: OrderStatus?) : MyOrdersEvent()
    data class SearchOrders(val query: String) : MyOrdersEvent()
    data class SelectOrder(val order: Order) : MyOrdersEvent()
    object ClearError : MyOrdersEvent()
}
