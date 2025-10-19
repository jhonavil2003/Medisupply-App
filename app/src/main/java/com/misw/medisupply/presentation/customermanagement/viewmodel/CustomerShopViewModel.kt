package com.misw.medisupply.presentation.customermanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.repository.order.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Customer Shop Screen
 * Manages orders for a specific customer (customer_id = 1 for now)
 */
@HiltViewModel
class CustomerShopViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CustomerShopUiState())
    val uiState: StateFlow<CustomerShopUiState> = _uiState.asStateFlow()
    
    // Customer ID fijo por ahora, después será dinámico desde login
    private val customerId = 1
    
    init {
        loadCustomerOrders()
    }
    
    /**
     * Load orders for the current customer
     */
    private fun loadCustomerOrders() {
        viewModelScope.launch {
            orderRepository.getOrders(customerId = customerId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            orders = resource.data ?: emptyList(),
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = resource.message
                        )
                    }
                }
            }
        }
    }
    
    /**
     * Retry loading orders
     */
    fun retryLoadOrders() {
        loadCustomerOrders()
    }
}

/**
 * UI State for Customer Shop Screen
 */
data class CustomerShopUiState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null
)