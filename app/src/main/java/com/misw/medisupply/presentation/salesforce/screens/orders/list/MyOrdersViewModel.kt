package com.misw.medisupply.presentation.salesforce.screens.orders.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.usecase.customer.GetCustomersUseCase
import com.misw.medisupply.domain.usecase.order.GetOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for My Orders Screen
 * Manages the state and business logic for displaying seller's orders with advanced filtering
 */
@HiltViewModel
class MyOrdersViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val getCustomersUseCase: GetCustomersUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(MyOrdersState())
    val state: StateFlow<MyOrdersState> = _state.asStateFlow()
    
    /**
     * ID del vendedor actual
     * TODO: Obtener del UserSession cuando esté disponible
     */
    private val currentSellerId: String = "SELLER-001"
    
    /**
     * Obtener todos los resultados sin paginación
     */
    private val maxResults: Int = 1000
    
    init {
        loadOrders()
        loadCustomers()
    }
    
    /**
     * Handle events from the UI
     */
    fun onEvent(event: MyOrdersEvent) {
        when (event) {
            is MyOrdersEvent.LoadOrders -> loadOrders()
            is MyOrdersEvent.LoadCustomers -> loadCustomers()
            is MyOrdersEvent.RefreshOrders -> refreshOrders()
            is MyOrdersEvent.FilterByStatus -> filterByStatus(event.status)
            is MyOrdersEvent.FilterByCustomer -> filterByCustomer(event.customerId)
            is MyOrdersEvent.FilterByDateRange -> filterByDateRange(event.dateRange)
            is MyOrdersEvent.ClearFilters -> clearAllFilters()
            is MyOrdersEvent.SelectOrder -> selectOrder(event.order)
            is MyOrdersEvent.ClearError -> clearError()
        }
    }
    
    /**
     * Load orders from repository with current filters
     */
    private fun loadOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val currentState = _state.value
            
            getOrdersUseCase(
                sellerId = currentSellerId,
                customerId = currentState.selectedCustomerId,
                status = currentState.selectedStatus?.value,
                page = 1,
                perPage = maxResults
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Loading already set above
                    }
                    is Resource.Success -> {
                        val paginatedResult = resource.data
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                orders = paginatedResult?.items ?: emptyList(),
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = resource.message
                            )
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Load customers for filtering
     */
    private fun loadCustomers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingCustomers = true) }
            
            getCustomersUseCase(
                sellerId = currentSellerId.toIntOrNull() // Convert if needed
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Loading already set above
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoadingCustomers = false,
                                customers = resource.data ?: emptyList()
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoadingCustomers = false,
                                error = resource.message
                            )
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Refresh orders (pull to refresh)
     */
    private fun refreshOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            loadOrders()
        }
    }
    
    /**
     * Filter orders by status
     * Reloads orders with filter applied on backend
     */
    private fun filterByStatus(status: com.misw.medisupply.domain.model.order.OrderStatus?) {
        _state.update { it.copy(selectedStatus = status) }
        loadOrders()
    }
    
    /**
     * Filter orders by customer
     * Reloads orders with filter applied on backend
     */
    private fun filterByCustomer(customerId: Int?) {
        _state.update { it.copy(selectedCustomerId = customerId) }
        loadOrders()
    }
    
    /**
     * Filter orders by date range
     * Applied on client-side since API doesn't support date filtering
     */
    private fun filterByDateRange(dateRange: DateRange?) {
        _state.update { it.copy(selectedDateRange = dateRange) }
    }
    
    /**
     * Clear all filters
     */
    private fun clearAllFilters() {
        _state.update {
            it.copy(
                selectedStatus = null,
                selectedCustomerId = null,
                selectedDateRange = null
            )
        }
        loadOrders()
    }
    
    /**
     * Select an order to view details
     */
    private fun selectOrder(order: com.misw.medisupply.domain.model.order.Order) {
        _state.update { it.copy(selectedOrder = order) }
    }
    
    /**
     * Clear error message
     */
    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
