package com.misw.medisupply.presentation.salesforce.screens.orders

import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderItem
import com.misw.medisupply.domain.model.order.OrderStatus

/**
 * UI State for Edit Order Screen
 * Represents all possible states of the edit order screen
 */
data class EditOrderState(
    val isLoading: Boolean = false,
    val order: Order? = null,
    val error: String? = null,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val orderItems: List<EditableOrderItem> = emptyList(),
    val searchQuery: String = "",
    val showDeleteConfirmation: Boolean = false,
    val successMessage: String? = null
) {
    /**
     * Get total number of product lines (not quantity)
     */
    fun getProductLinesCount(): Int = orderItems.size
    
    /**
     * Get total amount of the order
     */
    fun getTotalAmount(): Double {
        return orderItems.sumOf { it.quantity * it.unitPrice }
    }
    
    /**
     * Get formatted total amount
     */
    fun getFormattedTotalAmount(): String {
        return "$ ${String.format("%,.2f", getTotalAmount())}"
    }
    
    /**
     * Check if order can be confirmed
     */
    fun canConfirmOrder(): Boolean {
        return orderItems.isNotEmpty() && 
               orderItems.all { it.quantity > 0 } &&
               !isSaving &&
               !isDeleting
    }
}

/**
 * Editable order item with additional UI state
 */
data class EditableOrderItem(
    val productId: String,
    val productName: String,
    val productCode: String,
    val unitPrice: Double,
    val quantity: Int,
    val availableStock: Int,
    val isInStock: Boolean
) {
    /**
     * Get formatted unit price
     */
    fun getFormattedUnitPrice(): String {
        return "$ ${String.format("%,.2f", unitPrice)}"
    }
    
    /**
     * Get formatted subtotal
     */
    fun getFormattedSubtotal(): String {
        return "$ ${String.format("%,.2f", unitPrice * quantity)}"
    }
    
    /**
     * Check if can increment quantity
     */
    fun canIncrement(): Boolean {
        return quantity < availableStock
    }
    
    /**
     * Check if can decrement quantity
     */
    fun canDecrement(): Boolean {
        return quantity > 0
    }
    
    companion object {
        /**
         * Convert OrderItem to EditableOrderItem
         */
        fun fromOrderItem(item: OrderItem): EditableOrderItem {
            return EditableOrderItem(
                productId = item.productSku,
                productName = item.productName,
                productCode = item.productSku,
                unitPrice = item.unitPrice,
                quantity = item.quantity,
                availableStock = 100, // TODO: Get from product details/inventory
                isInStock = item.stockConfirmed
            )
        }
    }
}

/**
 * Sealed class representing user events/actions on Edit Order Screen
 */
sealed class EditOrderEvent {
    data class LoadOrder(val orderId: String) : EditOrderEvent()
    data class SearchProduct(val query: String) : EditOrderEvent()
    data class IncrementQuantity(val productId: String) : EditOrderEvent()
    data class DecrementQuantity(val productId: String) : EditOrderEvent()
    data class RemoveProduct(val productId: String) : EditOrderEvent()
    data class AddProduct(val productId: String) : EditOrderEvent()
    object ConfirmOrder : EditOrderEvent()
    object DeleteOrder : EditOrderEvent()
    object ShowDeleteConfirmation : EditOrderEvent()
    object HideDeleteConfirmation : EditOrderEvent()
    object ClearError : EditOrderEvent()
    object ClearSuccess : EditOrderEvent()
}
