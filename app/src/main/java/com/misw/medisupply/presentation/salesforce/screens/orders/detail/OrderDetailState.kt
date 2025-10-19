package com.misw.medisupply.presentation.salesforce.screens.orders.detail

import com.misw.medisupply.domain.model.order.Order

/**
 * UI State for Order Detail Screen
 */
data class OrderDetailState(
    val isLoading: Boolean = false,
    val order: Order? = null,
    val error: String? = null
) {
    /**
     * Check if order is loaded
     */
    fun hasOrder(): Boolean = order != null
    
    /**
     * Check if screen is in error state
     */
    fun hasError(): Boolean = error != null
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
     * Retry loading order after error
     */
    object RetryLoad : OrderDetailEvent()
    
    /**
     * Clear error message
     */
    object ClearError : OrderDetailEvent()
}
