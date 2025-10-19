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
 * Manages the state and business logic for displaying seller's orders
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
    
    init {
        loadOrders()
    }
    
    /**
     * Handle events from the UI
     */
    fun onEvent(event: MyOrdersEvent) {
        when (event) {
            is MyOrdersEvent.LoadOrders -> loadOrders()
            is MyOrdersEvent.RefreshOrders -> refreshOrders()
            is MyOrdersEvent.FilterByStatus -> filterByStatus(event.status)
            is MyOrdersEvent.SelectOrder -> selectOrder(event.order)
            is MyOrdersEvent.ClearError -> clearError()
        }
    }
    
    /**
     * Load orders from repository
     */
    private fun loadOrders() {
        viewModelScope.launch {
            getOrdersUseCase(sellerId = currentSellerId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                orders = resource.data ?: emptyList(),
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
     * Refresh orders (pull to refresh)
     */
    private fun refreshOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            
            getOrdersUseCase(sellerId = currentSellerId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Keep isRefreshing = true
                    }
                    is Resource.Success -> {
                        _state.update { 
                            it.copy(
                                isRefreshing = false,
                                orders = resource.data ?: emptyList(),
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { 
                            it.copy(
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
     * Filter orders by status
     */
    private fun filterByStatus(status: com.misw.medisupply.domain.model.order.OrderStatus?) {
        _state.update { it.copy(selectedStatus = status) }
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
