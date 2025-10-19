package com.misw.medisupply.presentation.salesforce.viewmodel.orders

import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType

data class OrdersState(
    val isLoading: Boolean = false,
    val customers: List<Customer> = emptyList(),
    val error: String? = null,
    val selectedFilter: CustomerType? = null,
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,
    val selectedCustomer: Customer? = null
) {
    fun hasCustomers(): Boolean = customers.isNotEmpty()
    
    fun getFilteredCustomers(): List<Customer> {
        if (searchQuery.isBlank()) return customers
        
        return customers.filter { customer ->
            customer.businessName.contains(searchQuery, ignoreCase = true) ||
            customer.tradeName?.contains(searchQuery, ignoreCase = true) == true ||
            customer.city?.contains(searchQuery, ignoreCase = true) == true
        }
    }
}

sealed class OrdersEvent {
    object LoadCustomers : OrdersEvent()
    object RefreshCustomers : OrdersEvent()
    data class FilterByType(val type: CustomerType?) : OrdersEvent()
    data class SearchCustomers(val query: String) : OrdersEvent()
    data class SelectCustomer(val customer: Customer) : OrdersEvent()
    object ClearError : OrdersEvent()
}
