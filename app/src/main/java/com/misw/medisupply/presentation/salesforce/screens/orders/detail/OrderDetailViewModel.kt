package com.misw.medisupply.presentation.salesforce.screens.orders.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.domain.model.order.OrderItem
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
    // TODO: Add UpdateOrderUseCase and DeleteOrderUseCase when available
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
            is OrderDetailEvent.UpdateItemQuantity -> {
                updateItemQuantity(event.itemId, event.newQuantity)
            }
            is OrderDetailEvent.RemoveItem -> {
                removeItem(event.itemId)
            }
            is OrderDetailEvent.ConfirmOrder -> {
                confirmOrder()
            }
            is OrderDetailEvent.DeleteOrder -> {
                deleteOrder()
            }
            is OrderDetailEvent.RetryLoad -> {
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
                                editedItems = emptyMap(), // Reset edited items
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
    
    /**
     * Update item quantity
     */
    private fun updateItemQuantity(itemId: Int, newQuantity: Int) {
        val currentOrder = _state.value.order ?: return
        val item = currentOrder.items.find { it.id == itemId } ?: return
        
        if (newQuantity <= 0) {
            // If quantity is 0 or less, remove the item
            removeItem(itemId)
            return
        }
        
        // Calculate new subtotal
        val newSubtotal = item.unitPrice * newQuantity
        val updatedItem = item.copy(
            quantity = newQuantity,
            subtotal = newSubtotal
        )
        
        _state.update { 
            it.copy(
                editedItems = it.editedItems + (itemId to updatedItem)
            )
        }
    }
    
    /**
     * Remove item from order
     */
    private fun removeItem(itemId: Int) {
        val currentOrder = _state.value.order ?: return
        val item = currentOrder.items.find { it.id == itemId } ?: return
        
        // Mark item as removed by setting quantity to 0
        val removedItem = item.copy(quantity = 0, subtotal = 0.0)
        
        _state.update { 
            it.copy(
                editedItems = it.editedItems + (itemId to removedItem)
            )
        }
    }
    
    /**
     * Confirm order with changes
     * TODO: Implement actual API call when UpdateOrderUseCase is available
     */
    private fun confirmOrder() {
        viewModelScope.launch {
            _state.update { it.copy(isUpdating = true, error = null) }
            
            // TODO: Call UpdateOrderUseCase here
            // For now, just simulate success
            kotlinx.coroutines.delay(1000)
            
            _state.update { 
                it.copy(
                    isUpdating = false,
                    updateSuccess = true,
                    error = null
                )
            }
        }
    }
    
    /**
     * Delete entire order
     * TODO: Implement actual API call when DeleteOrderUseCase is available
     */
    private fun deleteOrder() {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true, error = null) }
            
            // TODO: Call DeleteOrderUseCase here
            // For now, just simulate success
            kotlinx.coroutines.delay(1000)
            
            _state.update { 
                it.copy(
                    isDeleting = false,
                    deleteSuccess = true,
                    error = null
                )
            }
        }
    }
}
