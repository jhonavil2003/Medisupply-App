package com.misw.medisupply.presentation.salesforce.screens.orders.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.base.Resource
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
 * Manages the state and business logic for displaying seller's orders with pagination
 */
@HiltViewModel
class MyOrdersViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(MyOrdersState())
    val state: StateFlow<MyOrdersState> = _state.asStateFlow()
    
    /**
     * ID del vendedor actual
     * TODO: Obtener del UserSession cuando esté disponible
     */
    private val currentSellerId: String = "SELLER-001"
    
    /**
     * Número de órdenes por página
     */
    private val ordersPerPage: Int = 20
    
    init {
        loadOrders()
    }
    
    /**
     * Handle events from the UI
     */
    fun onEvent(event: MyOrdersEvent) {
        when (event) {
            is MyOrdersEvent.LoadOrders -> loadOrders()
            is MyOrdersEvent.LoadNextPage -> loadNextPage()
            is MyOrdersEvent.LoadPreviousPage -> loadPreviousPage()
            is MyOrdersEvent.RefreshOrders -> refreshOrders()
            is MyOrdersEvent.FilterByStatus -> filterByStatus(event.status)
            is MyOrdersEvent.SelectOrder -> selectOrder(event.order)
            is MyOrdersEvent.ClearError -> clearError()
        }
    }
    
    /**
     * Load first page of orders from repository
     */
    private fun loadOrders() {
        loadPage(1)
    }
    
    /**
     * Load next page of orders
     */
    private fun loadNextPage() {
        val currentState = _state.value
        if (currentState.currentPage < currentState.totalPages && !currentState.isLoadingMore) {
            loadPage(currentState.currentPage + 1)
        }
    }
    
    /**
     * Load previous page of orders
     */
    private fun loadPreviousPage() {
        val currentState = _state.value
        if (currentState.currentPage > 1 && !currentState.isLoadingMore) {
            loadPage(currentState.currentPage - 1)
        }
    }
    
    /**
     * Load a specific page of orders
     */
    private fun loadPage(page: Int) {
        viewModelScope.launch {
            // Set loading state based on whether it's first load or page change
            if (page == 1 && _state.value.orders.isEmpty()) {
                _state.update { it.copy(isLoading = true, error = null) }
            } else {
                _state.update { it.copy(isLoadingMore = true, error = null) }
            }
            
            // Get selected status to pass to backend
            val statusFilter = _state.value.selectedStatus?.value
            
            getOrdersUseCase(
                sellerId = currentSellerId,
                status = statusFilter,  // Apply filter on backend
                page = page,
                perPage = ordersPerPage
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
                                isLoadingMore = false,
                                isRefreshing = false,
                                // Replace orders with the new page (not append)
                                orders = paginatedResult?.items ?: emptyList(),
                                currentPage = paginatedResult?.page ?: page,
                                totalPages = paginatedResult?.totalPages ?: 1,
                                totalOrders = paginatedResult?.total ?: 0,
                                hasMore = paginatedResult?.hasMore ?: false,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                isLoadingMore = false,
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
     * Refresh orders (pull to refresh - resets to page 1)
     */
    private fun refreshOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            loadPage(1)
        }
    }
    
    /**
     * Filter orders by status
     * Resets to page 1 and reloads with filter applied on backend
     */
    private fun filterByStatus(status: com.misw.medisupply.domain.model.order.OrderStatus?) {
        _state.update { it.copy(selectedStatus = status) }
        // Reload from page 1 with the new filter
        loadPage(1)
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
