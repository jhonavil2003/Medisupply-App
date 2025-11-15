package com.misw.medisupply.presentation.salesforce.viewmodel.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.order.CartItem
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.PaymentMethod
import com.misw.medisupply.domain.model.order.PaymentTerms
import com.misw.medisupply.domain.repository.order.OrderItemRequest
import com.misw.medisupply.domain.usecase.cart.ClearCartUseCase
import com.misw.medisupply.domain.usecase.order.CreateOrderUseCase
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
 * ViewModel for Order Creation
 * Manages order creation state and business logic
 */
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val createOrderUseCase: CreateOrderUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrderState())
    val state: StateFlow<OrderState> = _state.asStateFlow()

    /**
     * Create order with cart items
     */
    fun createOrder(
        customer: Customer,
        cartItems: Map<String, CartItem>,
        paymentTerms: PaymentTerms = PaymentTerms.CASH,
        paymentMethod: PaymentMethod? = null,
        deliveryAddress: String? = null,
        deliveryCity: String? = null,
        deliveryDepartment: String? = null,
        deliveryDate: String? = null,
        notes: String? = null
    ) {
        // Convert cart items to order item requests
        val orderItems = cartItems.values.map { cartItem ->
            OrderItemRequest(
                productSku = cartItem.productSku,
                productName = cartItem.productName,
                quantity = cartItem.quantity,
                unitPrice = cartItem.unitPrice.toDouble(),
                discountPercentage = 0.0, // No discount
                taxPercentage = 19.0 // Default IVA Colombia
            )
        }

        // TODO: Get seller info from auth/session
        val sellerId = "SELLER-001" // Hardcoded for now
        val sellerName = "Vendedor Demo"

        createOrderUseCase(
            customerId = customer.id,
            sellerId = sellerId,
            sellerName = sellerName,
            items = orderItems,
            paymentTerms = paymentTerms,
            paymentMethod = paymentMethod,
            deliveryAddress = deliveryAddress ?: customer.address,
            deliveryCity = deliveryCity ?: customer.city,
            deliveryDepartment = deliveryDepartment ?: customer.department,
            deliveryDate = deliveryDate,
            preferredDistributionCenter = null,
            notes = notes
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.update {
                        it.copy(
                            isLoading = true,
                            error = null,
                            createdOrder = null
                        )
                    }
                }
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            createdOrder = resource.data
                        )
                    }
                    
                    // Clear cart reservations after successful order creation
                    clearCartReservations()
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = resource.message ?: "Error al crear la orden",
                            createdOrder = null
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Reset state after order creation
     */
    fun resetState() {
        _state.update { OrderState() }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    /**
     * Clear cart reservations after order confirmation
     * This releases all temporary stock reservations
     */
    private fun clearCartReservations() {
        viewModelScope.launch {
            clearCartUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        println("✅ Cart reservations cleared: ${resource.data?.clearedCount} products")
                    }
                    is Resource.Error -> {
                        println("⚠️ Failed to clear cart reservations: ${resource.message}")
                        // Don't fail the order - backend will auto-expire reservations
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }
}

/**
 * UI State for Order Creation
 */
data class OrderState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val createdOrder: Order? = null
)
