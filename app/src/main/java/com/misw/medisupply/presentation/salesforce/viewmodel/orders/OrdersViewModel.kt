package com.misw.medisupply.presentation.salesforce.viewmodel.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.model.order.CartItem
import com.misw.medisupply.domain.model.order.PaymentTerms
import com.misw.medisupply.domain.usecase.customer.GetCustomersUseCase
import com.misw.medisupply.domain.usecase.order.GetOrderByIdUseCase
import com.misw.medisupply.domain.usecase.order.GetOrdersUseCase
import com.misw.medisupply.domain.usecase.order.UpdateOrderUseCase
import com.misw.medisupply.domain.repository.order.OrderItemRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getCustomersUseCase: GetCustomersUseCase,
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val getOrdersUseCase: GetOrdersUseCase,
    private val updateOrderUseCase: UpdateOrderUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(OrdersState())
    val state: StateFlow<OrdersState> = _state.asStateFlow()
    
    init {
        loadCustomers()
    }
    
    fun onEvent(event: OrdersEvent) {
        when (event) {
            is OrdersEvent.LoadCustomers -> loadCustomers()
            is OrdersEvent.RefreshCustomers -> refreshCustomers()
            is OrdersEvent.FilterByType -> filterByType(event.type)
            is OrdersEvent.SearchCustomers -> searchCustomers(event.query)
            is OrdersEvent.SelectCustomer -> selectCustomer(event.customer)
            is OrdersEvent.ClearError -> clearError()
            is OrdersEvent.LoadOrderForEdit -> loadOrderForEdit(event.orderId)
            is OrdersEvent.UpdateOrder -> updateOrder()
        }
    }
    
    private fun loadCustomers(
        customerType: String? = null,
        city: String? = null,
        isActive: Boolean? = true
    ) {
        getCustomersUseCase(customerType, city, isActive)
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                customers = resource.data ?: emptyList(),
                                error = null,
                                isRefreshing = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = resource.message,
                                isRefreshing = false
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }
    
    private fun refreshCustomers() {
        _state.update { it.copy(isRefreshing = true) }
        loadCustomers(
            customerType = _state.value.selectedFilter?.name?.lowercase(),
            isActive = true
        )
    }
    
    /**
     * Filter customers by type
     */
    private fun filterByType(type: CustomerType?) {
        _state.update { it.copy(selectedFilter = type) }
        loadCustomers(
            customerType = type?.name?.lowercase(),
            isActive = true
        )
    }
    
    private fun searchCustomers(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }
    
    fun selectCustomer(customer: Customer) {
        _state.update { it.copy(selectedCustomer = customer) }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    fun getCustomerTypes(): List<CustomerType> {
        return CustomerType.entries
    }
    
    /**
     * Load an order for editing
     * Populates state with order data including customer, products, delivery info, etc.
     */
    fun loadOrderForEdit(orderId: String) {
        viewModelScope.launch {
            getOrderByIdUseCase(orderId.toIntOrNull() ?: return@launch)
                .onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _state.update { it.copy(isLoading = true, error = null) }
                        }
                        is Resource.Success -> {
                            val order = resource.data
                            if (order != null) {
                                // Convert order items to CartItems
                                val cartItems = order.items.associate { item ->
                                    val cartItem = CartItem(
                                        productSku = item.productSku,
                                        productName = item.productName,
                                        quantity = item.quantity,
                                        unitPrice = item.unitPrice.toFloat(),
                                        stockAvailable = null, // Stock will need to be loaded separately
                                        requiresColdChain = false, // TODO: Get from product info if needed
                                        category = "" // TODO: Get from product info if needed
                                    )
                                    item.productSku to cartItem
                                }
                                
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        selectedCustomer = order.customer,
                                        cartItems = cartItems,
                                        deliveryAddress = order.deliveryAddress,
                                        orderIdEditing = orderId,
                                        orderStatus = order.status,
                                        error = null
                                    )
                                }
                            } else {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        error = "Orden no encontrada"
                                    )
                                }
                            }
                        }
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = resource.message ?: "Error al cargar la orden"
                                )
                            }
                        }
                    }
                }
                .launchIn(this)
        }
    }
    
    /**
     * Update the current order being edited
     * Uses the data from state to build the update request
     */
    fun updateOrder() {
        val currentState = _state.value
        val orderId = currentState.orderIdEditing?.toIntOrNull()
        val customer = currentState.selectedCustomer
        val cartItems = currentState.cartItems
        
        android.util.Log.d("OrdersViewModel", "updateOrder() called")
        android.util.Log.d("OrdersViewModel", "orderId: $orderId")
        android.util.Log.d("OrdersViewModel", "customer: ${customer?.id}")
        android.util.Log.d("OrdersViewModel", "cartItems size: ${cartItems.size}")
        
        if (orderId == null || customer == null || cartItems.isEmpty()) {
            val errorMsg = "Datos incompletos para actualizar la orden"
            android.util.Log.e("OrdersViewModel", errorMsg)
            _state.update { it.copy(error = errorMsg) }
            return
        }
        
        viewModelScope.launch {
            // Convert CartItems to OrderItemRequest
            val items = cartItems.values.map { cartItem ->
                OrderItemRequest(
                    productSku = cartItem.productSku,
                    quantity = cartItem.quantity,
                    discountPercentage = 0.0, // TODO: Add discount to CartItem if needed
                    taxPercentage = 19.0 // Default IVA in Colombia
                )
            }
            
            android.util.Log.d("OrdersViewModel", "Calling updateOrderUseCase with ${items.size} items")
            
            updateOrderUseCase(
                orderId = orderId,
                customerId = customer.id,
                items = items,
                paymentTerms = PaymentTerms.CASH, // TODO: Get from state if needed
                deliveryAddress = currentState.deliveryAddress
            )
                .onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            android.util.Log.d("OrdersViewModel", "updateOrder: Loading")
                            _state.update { it.copy(isSaving = true, error = null) }
                        }
                        is Resource.Success -> {
                            android.util.Log.d("OrdersViewModel", "updateOrder: Success - ${resource.data?.orderNumber}")
                            // Add the updated order to the orders list
                            val updatedOrder = resource.data
                            val currentOrders = _state.value.orders.toMutableList()
                            val index = currentOrders.indexOfFirst { it.id == updatedOrder?.id }
                            if (index != -1 && updatedOrder != null) {
                                currentOrders[index] = updatedOrder
                            }
                            
                            _state.update {
                                it.copy(
                                    isSaving = false,
                                    successMessage = "Orden actualizada exitosamente",
                                    orders = currentOrders,
                                    updatedOrder = updatedOrder, // Store for success dialog
                                    error = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            android.util.Log.e("OrdersViewModel", "updateOrder: Error - ${resource.message}")
                            _state.update {
                                it.copy(
                                    isSaving = false,
                                    error = resource.message ?: "Error al actualizar la orden"
                                )
                            }
                        }
                    }
                }
                .launchIn(this)
        }
    }
    
    /**
     * Load orders for the current seller
     * TODO: Get sellerId from auth/session
     */
    fun loadOrders(sellerId: String = "SELLER-001") {
        viewModelScope.launch {
            getOrdersUseCase(
                sellerId = sellerId,
                customerId = null,
                status = null
            )
                .onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _state.update { it.copy(isLoading = true, error = null) }
                        }
                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    orders = resource.data ?: emptyList(),
                                    error = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = resource.message ?: "Error al cargar las órdenes"
                                )
                            }
                        }
                    }
                }
                .launchIn(this)
        }
    }
    
    /**
     * Clear success message and reload orders
     */
    fun clearSuccessMessage() {
        _state.update { 
            it.copy(
                successMessage = null,
                updatedOrder = null,
                orderIdEditing = null,
                cartItems = emptyMap(),
                deliveryAddress = null,
                orderStatus = null
            ) 
        }
        // Reload orders to show updated list
        loadOrders()
    }
}

