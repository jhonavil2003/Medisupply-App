package com.misw.medisupply.presentation.salesforce.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.domain.usecase.order.GetOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel for My Orders Screen
 * Manages UI state and handles user actions for orders list with filters
 */
@HiltViewModel
class MyOrdersViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(MyOrdersState())
    val state: StateFlow<MyOrdersState> = _state.asStateFlow()
    
    init {
        loadOrders()
    }
    
    /**
     * Handle user events
     */
    fun onEvent(event: MyOrdersEvent) {
        when (event) {
            is MyOrdersEvent.LoadOrders -> loadOrders()
            is MyOrdersEvent.RefreshOrders -> refreshOrders()
            is MyOrdersEvent.FilterByStatus -> filterByStatus(event.status)
            is MyOrdersEvent.SearchOrders -> searchOrders(event.query)
            is MyOrdersEvent.SelectOrder -> selectOrder(event.order)
            is MyOrdersEvent.ClearError -> clearError()
        }
    }
    
    /**
     * Load orders from repository
     */
    private fun loadOrders(
        sellerId: String? = _state.value.sellerId,
        status: String? = null
    ) {
        getOrdersUseCase(
            sellerId = sellerId,
            customerId = null,
            status = status
        )
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                orders = resource.data ?: emptyList(),
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
     * Refresh orders list
     */
    private fun refreshOrders() {
        _state.update { it.copy(isRefreshing = true) }
        loadOrders(
            sellerId = _state.value.sellerId,
            status = _state.value.selectedStatus?.value
        )
    }
    
    /**
     * Filter orders by status
     */
    private fun filterByStatus(status: OrderStatus?) {
        _state.update { it.copy(selectedStatus = status) }
        // Note: Filtering is done locally in getFilteredOrders()
        // If you want server-side filtering, uncomment below:
        // loadOrders(
        //     sellerId = _state.value.sellerId,
        //     status = status?.value
        // )
    }
    
    /**
     * Search orders locally
     */
    private fun searchOrders(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }
    
    /**
     * Handle order selection
     * Navigate to order detail screen
     */
    private fun selectOrder(order: Order) {
        // TODO: Navigate to order detail screen
        // This will be implemented when order detail flow is set up
    }
    
    /**
     * Clear error message
     */
    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    /**
     * Get all order statuses for filter
     */
    fun getOrderStatuses(): List<OrderStatus> {
        return OrderStatus.entries
    }
    
    /**
     * Get active order statuses (excluding cancelled)
     */
    fun getActiveOrderStatuses(): List<OrderStatus> {
        return OrderStatus.getActiveStatuses()
    }
}
