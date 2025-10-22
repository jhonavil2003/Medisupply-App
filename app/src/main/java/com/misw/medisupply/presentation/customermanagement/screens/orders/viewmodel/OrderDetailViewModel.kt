package com.misw.medisupply.presentation.customermanagement.screens.orders.viewmodel

import android.util.Log
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
 * ViewModel for Order Detail Screen
 * Manages order detail data for customer order tracking
 */
@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "OrderDetailViewModel"
    }
    
    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()
    
    /**
     * Load order details by ID
     */
    fun loadOrderDetail(orderId: Int) {
        Log.d(TAG, "=== CARGANDO DETALLE DEL PEDIDO ===")
        Log.d(TAG, "Order ID: $orderId")
        
        viewModelScope.launch {
            orderRepository.getOrderById(orderId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Log.d(TAG, "Estado: Loading")
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Resource.Success -> {
                        Log.d(TAG, "Estado: Success - Pedido cargado: ${resource.data?.orderNumber}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            order = resource.data,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "Estado: Error - ${resource.message}")
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
     * Retry loading order detail
     */
    fun retryLoad(orderId: Int) {
        loadOrderDetail(orderId)
    }
}

/**
 * UI State for Order Detail Screen
 */
data class OrderDetailUiState(
    val isLoading: Boolean = false,
    val order: Order? = null,
    val error: String? = null
)