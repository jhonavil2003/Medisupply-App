package com.misw.medisupply.presentation.salesforce.screens.orders

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.usecase.order.DeleteOrderUseCase
import com.misw.medisupply.domain.usecase.order.GetOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Edit Order Screen
 * Manages UI state and handles user actions for editing an order
 */
@HiltViewModel
class EditOrderViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val deleteOrderUseCase: DeleteOrderUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _state = MutableStateFlow(EditOrderState())
    val state: StateFlow<EditOrderState> = _state.asStateFlow()
    
    private val orderId: String? = savedStateHandle.get<String>("orderId")
    
    init {
        orderId?.let { loadOrder(it) }
    }
    
    /**
     * Handle user events
     */
    fun onEvent(event: EditOrderEvent) {
        when (event) {
            is EditOrderEvent.LoadOrder -> loadOrder(event.orderId)
            is EditOrderEvent.SearchProduct -> searchProduct(event.query)
            is EditOrderEvent.IncrementQuantity -> incrementQuantity(event.productId)
            is EditOrderEvent.DecrementQuantity -> decrementQuantity(event.productId)
            is EditOrderEvent.RemoveProduct -> removeProduct(event.productId)
            is EditOrderEvent.AddProduct -> addProduct(event.productId)
            is EditOrderEvent.ConfirmOrder -> confirmOrder()
            is EditOrderEvent.DeleteOrder -> deleteOrder()
            is EditOrderEvent.ShowDeleteConfirmation -> showDeleteConfirmation()
            is EditOrderEvent.HideDeleteConfirmation -> hideDeleteConfirmation()
            is EditOrderEvent.ClearError -> clearError()
            is EditOrderEvent.ClearSuccess -> clearSuccess()
        }
    }
    
    /**
     * Load order by ID
     */
    private fun loadOrder(orderId: String) {
        // TODO: Create GetOrderByIdUseCase
        // For now, we'll load all orders and filter
        getOrdersUseCase(
            sellerId = null,
            customerId = null,
            status = null
        )
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val order = resource.data?.find { it.id.toString() == orderId }
                        if (order != null) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    order = order,
                                    orderItems = order.items.map { item ->
                                        EditableOrderItem.fromOrderItem(item)
                                    },
                                    error = null
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Pedido no encontrado"
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = resource.message
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Search for products to add
     */
    private fun searchProduct(query: String) {
        _state.update { it.copy(searchQuery = query) }
        // TODO: Implement product search
    }
    
    /**
     * Increment product quantity
     */
    private fun incrementQuantity(productId: String) {
        _state.update { currentState ->
            val updatedItems = currentState.orderItems.map { item ->
                if (item.productId == productId && item.canIncrement()) {
                    item.copy(quantity = item.quantity + 1)
                } else {
                    item
                }
            }
            currentState.copy(orderItems = updatedItems)
        }
    }
    
    /**
     * Decrement product quantity
     */
    private fun decrementQuantity(productId: String) {
        _state.update { currentState ->
            val updatedItems = currentState.orderItems.map { item ->
                if (item.productId == productId && item.canDecrement()) {
                    item.copy(quantity = item.quantity - 1)
                } else {
                    item
                }
            }
            currentState.copy(orderItems = updatedItems)
        }
    }
    
    /**
     * Remove product from order
     */
    private fun removeProduct(productId: String) {
        _state.update { currentState ->
            val updatedItems = currentState.orderItems.filter { it.productId != productId }
            currentState.copy(orderItems = updatedItems)
        }
    }
    
    /**
     * Add product to order
     */
    private fun addProduct(productId: String) {
        // TODO: Implement add product functionality
        // This would search and add a new product to the order
    }
    
    /**
     * Confirm/save order changes
     */
    private fun confirmOrder() {
        if (!_state.value.canConfirmOrder()) return
        
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            
            // TODO: Implement update order use case
            // Simulate API call
            kotlinx.coroutines.delay(1000)
            
            _state.update {
                it.copy(
                    isSaving = false,
                    successMessage = "Pedido actualizado exitosamente"
                )
            }
        }
    }
    
    /**
     * Delete order
     */
    private fun deleteOrder() {
        val currentOrder = _state.value.order ?: return
        
        deleteOrderUseCase(currentOrder.id)
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { 
                            it.copy(
                                isDeleting = true, 
                                error = null, 
                                showDeleteConfirmation = false
                            ) 
                        }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isDeleting = false,
                                successMessage = "Pedido eliminado exitosamente"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isDeleting = false,
                                error = resource.message ?: "Error al eliminar el pedido",
                                showDeleteConfirmation = false
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Show delete confirmation dialog
     */
    private fun showDeleteConfirmation() {
        _state.update { it.copy(showDeleteConfirmation = true) }
    }
    
    /**
     * Hide delete confirmation dialog
     */
    private fun hideDeleteConfirmation() {
        _state.update { it.copy(showDeleteConfirmation = false) }
    }
    
    /**
     * Clear error message
     */
    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    /**
     * Clear success message
     */
    private fun clearSuccess() {
        _state.update { it.copy(successMessage = null) }
    }
}
