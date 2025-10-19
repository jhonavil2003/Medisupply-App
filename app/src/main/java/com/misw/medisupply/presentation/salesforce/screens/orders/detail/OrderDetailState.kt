package com.misw.medisupply.presentation.salesforce.screens.orders.detail

import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderItem

/**
 * UI State for Order Detail Screen
 */
data class OrderDetailState(
    val isLoading: Boolean = false,
    val order: Order? = null,
    val editedItems: Map<Int, OrderItem> = emptyMap(), // Items being edited (key: item.id)
    val error: String? = null,
    val isUpdating: Boolean = false,
    val isDeleting: Boolean = false,
    val updateSuccess: Boolean = false,
    val deleteSuccess: Boolean = false
) {
    /**
     * Check if order is loaded
     */
    fun hasOrder(): Boolean = order != null
    
    /**
     * Check if screen is in error state
     */
    fun hasError(): Boolean = error != null
    
    /**
     * Get current items (edited or original)
     */
    fun getCurrentItems(): List<OrderItem> {
        return order?.items?.map { item ->
            editedItems[item.id] ?: item
        } ?: emptyList()
    }
    
    /**
     * Check if order has been modified
     */
    fun isModified(): Boolean = editedItems.isNotEmpty()
    
    /**
     * Calculate new total based on edited items
     */
    fun calculateNewTotal(): Double {
        return getCurrentItems().sumOf { it.subtotal }
    }
}

/**
 * Events that can be triggered from Order Detail Screen
 */
sealed class OrderDetailEvent {
    /**
     * Load order detail by ID
     */
    data class LoadOrderDetail(val orderId: String) : OrderDetailEvent()
    
    /**
     * Update item quantity
     */
    data class UpdateItemQuantity(val itemId: Int, val newQuantity: Int) : OrderDetailEvent()
    
    /**
     * Remove item from order
     */
    data class RemoveItem(val itemId: Int) : OrderDetailEvent()
    
    /**
     * Confirm/Update order with changes
     */
    object ConfirmOrder : OrderDetailEvent()
    
    /**
     * Delete entire order
     */
    object DeleteOrder : OrderDetailEvent()
    
    /**
     * Retry loading order after error
     */
    object RetryLoad : OrderDetailEvent()
    
    /**
     * Clear error message
     */
    object ClearError : OrderDetailEvent()
}
