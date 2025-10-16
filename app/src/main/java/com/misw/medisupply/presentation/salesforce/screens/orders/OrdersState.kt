package com.misw.medisupply.presentation.salesforce.screens.orders

import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType

/**
 * UI State for Orders Screen
 * Represents all possible states of the orders screen (customer list for creating orders)
 */
data class OrdersState(
    val isLoading: Boolean = false,
    val customers: List<Customer> = emptyList(),
    val error: String? = null,
    val selectedFilter: CustomerType? = null,
    val searchQuery: String = "",
    val isRefreshing: Boolean = false
) {
    /**
     * Check if there are customers to display
     */
    fun hasCustomers(): Boolean = customers.isNotEmpty()
    
    /**
     * Get filtered customers based on search query
     */
    fun getFilteredCustomers(): List<Customer> {
        if (searchQuery.isBlank()) return customers
        
        return customers.filter { customer ->
            customer.businessName.contains(searchQuery, ignoreCase = true) ||
            customer.tradeName?.contains(searchQuery, ignoreCase = true) == true ||
            customer.city?.contains(searchQuery, ignoreCase = true) == true
        }
    }
}

/**
 * Sealed class representing user events/actions on Orders Screen
 */
sealed class OrdersEvent {
    object LoadCustomers : OrdersEvent()
    object RefreshCustomers : OrdersEvent()
    data class FilterByType(val type: CustomerType?) : OrdersEvent()
    data class SearchCustomers(val query: String) : OrdersEvent()
    data class SelectCustomer(val customer: Customer) : OrdersEvent()
    object ClearError : OrdersEvent()
}
