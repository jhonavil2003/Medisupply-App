package com.misw.medisupply.presentation.salesforce.viewmodel.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.usecase.customer.GetCustomersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel for Orders Screen
 * Manages UI state and handles user actions for customer selection to create orders
 */
@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getCustomersUseCase: GetCustomersUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(OrdersState())
    val state: StateFlow<OrdersState> = _state.asStateFlow()
    
    init {
        loadCustomers()
    }
    
    /**
     * Handle user events
     */
    fun onEvent(event: OrdersEvent) {
        when (event) {
            is OrdersEvent.LoadCustomers -> loadCustomers()
            is OrdersEvent.RefreshCustomers -> refreshCustomers()
            is OrdersEvent.FilterByType -> filterByType(event.type)
            is OrdersEvent.SearchCustomers -> searchCustomers(event.query)
            is OrdersEvent.SelectCustomer -> selectCustomer(event.customer)
            is OrdersEvent.ClearError -> clearError()
        }
    }
    
    /**
     * Load customers from repository
     */
    private fun loadCustomers(
        customerType: String? = null,
        city: String? = null,
        isActive: Boolean? = true
    ) {
        getCustomersUseCase(customerType, city, isActive)
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                customers = resource.data ?: emptyList(),
                                error = null,
                                isRefreshing = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = resource.message,
                                isRefreshing = false
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Refresh customers list
     */
    private fun refreshCustomers() {
        _state.update { it.copy(isRefreshing = true) }
        loadCustomers(
            customerType = _state.value.selectedFilter?.name?.lowercase(),
            isActive = true
        )
    }
    
    /**
     * Filter customers by type
     */
    private fun filterByType(type: CustomerType?) {
        _state.update { it.copy(selectedFilter = type) }
        loadCustomers(
            customerType = type?.name?.lowercase(),
            isActive = true
        )
    }
    
    /**
     * Search customers locally
     */
    private fun searchCustomers(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }
    
    /**
     * Handle customer selection
     * Navigate to order creation screen
     */
    private fun selectCustomer(customer: Customer) {
        // TODO: Navigate to order creation screen
        // This will be implemented when order creation flow is set up
    }
    
    /**
     * Clear error message
     */
    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    /**
     * Get all customer types for filter
     */
    fun getCustomerTypes(): List<CustomerType> {
        return CustomerType.entries
    }
}
