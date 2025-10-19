package com.misw.medisupply.presentation.salesforce.screens.orders.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.domain.usecase.order.GetOrderByIdUseCase
import com.misw.medisupply.core.base.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Order Detail Screen
 */
@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val getOrderByIdUseCase: GetOrderByIdUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(OrderDetailState())
    val state: StateFlow<OrderDetailState> = _state.asStateFlow()
    
    /**
     * Handle events from the UI
     */
    fun onEvent(event: OrderDetailEvent) {
        when (event) {
            is OrderDetailEvent.LoadOrderDetail -> {
                loadOrderDetail(event.orderId)
            }
            is OrderDetailEvent.RetryLoad -> {
                // Retry with the current order ID if available
                _state.value.order?.id?.let { orderId ->
                    loadOrderDetail(orderId.toString())
                }
            }
            is OrderDetailEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }
    
    /**
     * Load order detail by ID
     */
    fun loadOrderDetail(orderId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            getOrderByIdUseCase(orderId.toIntOrNull() ?: 0).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                order = resource.data,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                error = resource.message ?: "Error al cargar el detalle del pedido"
                            )
                        }
                    }
                }
            }
        }
    }
}
