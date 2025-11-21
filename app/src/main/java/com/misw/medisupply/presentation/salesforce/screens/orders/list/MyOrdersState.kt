package com.misw.medisupply.presentation.salesforce.screens.orders.list

import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderStatus
import java.util.Date

/**
 * UI State for My Orders Screen with advanced filtering
 */
data class MyOrdersState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val orders: List<Order> = emptyList(),
    val customers: List<Customer> = emptyList(),
    val selectedStatus: OrderStatus? = null,
    val selectedCustomerId: Int? = null,
    val selectedDateRange: DateRange? = null,
    val error: String? = null,
    val selectedOrder: Order? = null,
    val isLoadingCustomers: Boolean = false
) {
    /**
     * Check if there are any orders
     */
    fun hasOrders(): Boolean = orders.isNotEmpty()
    
    /**
     * Get orders to display with local filters applied
     */
    fun getFilteredOrders(): List<Order> {
        var filteredOrders = orders
        
        // Apply date filter (client-side since API doesn't support it)
        selectedDateRange?.let { dateRange ->
            filteredOrders = filteredOrders.filter { order ->
                order.orderDate?.let { orderDate ->
                    orderDate.after(dateRange.startDate) || orderDate == dateRange.startDate &&
                    orderDate.before(dateRange.endDate) || orderDate == dateRange.endDate
                } ?: false
            }
        }
        
        return filteredOrders
    }
    
    /**
     * Get customer by ID
     */
    fun getCustomerById(customerId: Int): Customer? {
        return customers.find { it.id == customerId }
    }
    
    /**
     * Check if any filters are active
     */
    fun hasActiveFilters(): Boolean {
        return selectedStatus != null || selectedCustomerId != null || selectedDateRange != null
    }
    
    /**
     * Get total count of filtered orders
     */
    fun getTotalFilteredOrderCount(): Int = getFilteredOrders().size
}

/**
 * Data class for date range filtering
 */
data class DateRange(
    val startDate: Date,
    val endDate: Date
)

/**
 * Events that can be triggered from My Orders Screen
 */
sealed class MyOrdersEvent {
    /**
     * Load orders from repository
     */
    object LoadOrders : MyOrdersEvent()
    
    /**
     * Load customers for filtering
     */
    object LoadCustomers : MyOrdersEvent()
    
    /**
     * Refresh orders (pull to refresh)
     */
    object RefreshOrders : MyOrdersEvent()
    
    /**
     * Filter orders by status
     */
    data class FilterByStatus(val status: OrderStatus?) : MyOrdersEvent()
    
    /**
     * Filter orders by customer
     */
    data class FilterByCustomer(val customerId: Int?) : MyOrdersEvent()
    
    /**
     * Filter orders by date range
     */
    data class FilterByDateRange(val dateRange: DateRange?) : MyOrdersEvent()
    
    /**
     * Clear all filters
     */
    object ClearFilters : MyOrdersEvent()
    
    /**
     * Select an order to view details
     */
    data class SelectOrder(val order: Order) : MyOrdersEvent()
    
    /**
     * Clear error message
     */
    object ClearError : MyOrdersEvent()
}
