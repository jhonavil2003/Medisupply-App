package com.misw.medisupply.presentation.salesforce.viewmodel.orders

import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.model.order.CartItem
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderStatus

data class OrdersState(
    val isLoading: Boolean = false,
    val customers: List<Customer> = emptyList(),
    val orders: List<Order> = emptyList(),
    val error: String? = null,
    val selectedFilter: CustomerType? = null,
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,
    val selectedCustomer: Customer? = null,
    
    // Fields for edit mode
    val cartItems: Map<String, CartItem> = emptyMap(),
    val deliveryAddress: String? = null,
    val orderIdEditing: String? = null,
    val orderStatus: OrderStatus? = null,
    val isSaving: Boolean = false,
    val successMessage: String? = null,
    val updatedOrder: Order? = null // Store the updated order for success dialog
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
    data class LoadOrderForEdit(val orderId: String) : OrdersEvent()
    object UpdateOrder : OrdersEvent()
}
