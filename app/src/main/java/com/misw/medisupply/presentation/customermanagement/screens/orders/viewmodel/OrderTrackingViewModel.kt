package com.misw.medisupply.presentation.customermanagement.screens.orders.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.domain.repository.order.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject


/**
 * ViewModel for Order Tracking Screen
 * Manages UI state and business logic for order tracking and filtering
 */
@HiltViewModel
class OrderTrackingViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "OrderTrackingViewModel"
        private const val CUSTOMER_ID = 1 // TODO: Obtener del login/session
    }
    
    private val _uiState = MutableStateFlow(OrderTrackingUiState())
    val uiState: StateFlow<OrderTrackingUiState> = _uiState.asStateFlow()
    
    init {
        // Load orders on initialization
        loadOrders()
    }
    
    /**
     * Load orders with current filters
     */
    fun loadOrders() {
        Log.d(TAG, "=== CARGANDO PEDIDOS ===")
        val currentState = _uiState.value
        Log.d(TAG, "Filtros actuales:")
        Log.d(TAG, "selectedStatus: ${currentState.selectedStatus}")
        Log.d(TAG, "deliveryDateFrom: ${currentState.deliveryDateFrom}")
        Log.d(TAG, "deliveryDateTo: ${currentState.deliveryDateTo}")
        Log.d(TAG, "orderDateFrom: ${currentState.orderDateFrom}")
        Log.d(TAG, "orderDateTo: ${currentState.orderDateTo}")
        
        viewModelScope.launch {
            orderRepository.getOrders(
                customerId = CUSTOMER_ID,
                status = currentState.selectedStatus?.takeIf { it != "todos" },
                page = 1,
                perPage = 100  // Load all orders for customer tracking (customers don't have many orders)
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Log.d(TAG, "Estado: Loading")
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Resource.Success -> {
                        // Extract items from PaginatedResult
                        val allOrders = resource.data?.items ?: emptyList()
                        Log.d(TAG, "Estado: Success - ${allOrders.size} pedidos")
                        Log.d(TAG, "Pedidos recibidos: ${allOrders.map { "ID:${it.id}, Number:${it.orderNumber}" }}")
                        
                        // Log delivery dates for debugging
                        allOrders.forEach { order ->
                            Log.d(TAG, "Pedido ${order.id}: deliveryDate = ${order.deliveryDate}")
                        }
                        
                        val filteredOrders = applyDateFilters(allOrders, currentState)
                        Log.d(TAG, "Pedidos después de filtros: ${filteredOrders.size}")
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            orders = filteredOrders,
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
     * Update status filter
     */
    fun updateStatusFilter(status: String) {
        Log.d(TAG, "Actualizando filtro de estado: $status")
        _uiState.value = _uiState.value.copy(selectedStatus = status)
        loadOrders()
    }
    
    /**
     * Update delivery date from filter
     */
    fun updateDeliveryDateFrom(date: Date?) {
        Log.d(TAG, "Actualizando fecha de entrega desde: $date")
        _uiState.value = _uiState.value.copy(deliveryDateFrom = date)
        loadOrders()
    }
    
    /**
     * Update delivery date to filter
     */
    fun updateDeliveryDateTo(date: Date?) {
        Log.d(TAG, "Actualizando fecha de entrega hasta: $date")
        _uiState.value = _uiState.value.copy(deliveryDateTo = date)
        loadOrders()
    }
    
    /**
     * Update order date from filter
     */
    fun updateOrderDateFrom(date: Date?) {
        Log.d(TAG, "Actualizando fecha de pedido desde: $date")
        _uiState.value = _uiState.value.copy(orderDateFrom = date)
        loadOrders()
    }
    
    /**
     * Update order date to filter
     */
    fun updateOrderDateTo(date: Date?) {
        Log.d(TAG, "Actualizando fecha de pedido hasta: $date")
        _uiState.value = _uiState.value.copy(orderDateTo = date)
        loadOrders()
    }
    
    /**
     * Clear all filters
     */
    fun clearFilters() {
        Log.d(TAG, "Limpiando todos los filtros")
        _uiState.value = OrderTrackingUiState()
        loadOrders()
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Toggle filters visibility
     */
    fun toggleFiltersVisibility() {
        _uiState.value = _uiState.value.copy(
            showFilters = !_uiState.value.showFilters
        )
    }
    
    /**
     * Normalize date to start of day (00:00:00.000)
     */
    private fun Date.toStartOfDay(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
    
    /**
     * Apply date filters locally to orders list
     */
    private fun applyDateFilters(orders: List<Order>, state: OrderTrackingUiState): List<Order> {
        Log.d(TAG, "=== APLICANDO FILTROS DE FECHA ===")
        Log.d(TAG, "Total órdenes antes de filtro: ${orders.size}")
        Log.d(TAG, "Filtro deliveryDateFrom: ${state.deliveryDateFrom}")
        Log.d(TAG, "Filtro deliveryDateTo: ${state.deliveryDateTo}")
        Log.d(TAG, "Filtro orderDateFrom: ${state.orderDateFrom}")
        Log.d(TAG, "Filtro orderDateTo: ${state.orderDateTo}")
        
        val filteredOrders = orders.filter { order ->
            var matchesFilters = true
            
            Log.d(TAG, "Evaluando orden ${order.id}:")
            Log.d(TAG, "  deliveryDate: ${order.deliveryDate}")
            Log.d(TAG, "  orderDate: ${order.orderDate}")
            
            // Filter by delivery date range
            state.deliveryDateFrom?.let { dateFrom ->
                order.deliveryDate?.let { deliveryDate ->
                    // Normalize both dates to start of day for proper comparison
                    val deliveryDateNormalized = deliveryDate.toStartOfDay()
                    val dateFromNormalized = dateFrom.toStartOfDay()
                    val deliveryDateMatches = deliveryDateNormalized >= dateFromNormalized
                    Log.d(TAG, "  deliveryDateFrom check: $deliveryDateNormalized >= $dateFromNormalized = $deliveryDateMatches")
                    matchesFilters = matchesFilters && deliveryDateMatches
                } ?: run {
                    Log.d(TAG, "  deliveryDateFrom check: deliveryDate es null = false")
                    matchesFilters = false
                }
            }
            state.deliveryDateTo?.let { dateTo ->
                order.deliveryDate?.let { deliveryDate ->
                    // Normalize both dates to start of day for proper comparison
                    val deliveryDateNormalized = deliveryDate.toStartOfDay()
                    val dateToNormalized = dateTo.toStartOfDay()
                    val deliveryDateMatches = deliveryDateNormalized <= dateToNormalized
                    Log.d(TAG, "  deliveryDateTo check: $deliveryDateNormalized <= $dateToNormalized = $deliveryDateMatches")
                    matchesFilters = matchesFilters && deliveryDateMatches
                } ?: run {
                    Log.d(TAG, "  deliveryDateTo check: deliveryDate es null = false")
                    matchesFilters = false
                }
            }
            
            // Filter by order date range
            state.orderDateFrom?.let { dateFrom ->
                order.orderDate?.let { orderDate ->
                    // Normalize both dates to start of day for proper comparison
                    val orderDateNormalized = orderDate.toStartOfDay()
                    val dateFromNormalized = dateFrom.toStartOfDay()
                    val orderDateMatches = orderDateNormalized >= dateFromNormalized
                    Log.d(TAG, "  orderDateFrom check: $orderDateNormalized >= $dateFromNormalized = $orderDateMatches")
                    matchesFilters = matchesFilters && orderDateMatches
                } ?: run {
                    Log.d(TAG, "  orderDateFrom check: orderDate es null = false")
                    matchesFilters = false
                }
            }
            state.orderDateTo?.let { dateTo ->
                order.orderDate?.let { orderDate ->
                    // Normalize both dates to start of day for proper comparison
                    val orderDateNormalized = orderDate.toStartOfDay()
                    val dateToNormalized = dateTo.toStartOfDay()
                    val orderDateMatches = orderDateNormalized <= dateToNormalized
                    Log.d(TAG, "  orderDateTo check: $orderDateNormalized <= $dateToNormalized = $orderDateMatches")
                    matchesFilters = matchesFilters && orderDateMatches
                } ?: run {
                    Log.d(TAG, "  orderDateTo check: orderDate es null = false")
                    matchesFilters = false
                }
            }
            
            Log.d(TAG, "  Resultado final para orden ${order.id}: $matchesFilters")
            matchesFilters
        }
        
        Log.d(TAG, "Total órdenes después de filtro: ${filteredOrders.size}")
        return filteredOrders
    }
}

/**
 * UI State for Order Tracking Screen
 */
data class OrderTrackingUiState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null,
    val showFilters: Boolean = false,
    
    // Filters
    val selectedStatus: String? = null,
    val deliveryDateFrom: Date? = null,
    val deliveryDateTo: Date? = null,
    val orderDateFrom: Date? = null,
    val orderDateTo: Date? = null
) {
    /**
     * Get available status options for filter
     */
    val statusOptions = listOf(
        "todos" to "Todos los estados",
        OrderStatus.PENDING.value to OrderStatus.PENDING.displayName,
        OrderStatus.CONFIRMED.value to OrderStatus.CONFIRMED.displayName, 
        OrderStatus.PROCESSING.value to OrderStatus.PROCESSING.displayName,
        OrderStatus.SHIPPED.value to OrderStatus.SHIPPED.displayName,
        OrderStatus.DELIVERED.value to OrderStatus.DELIVERED.displayName,
        OrderStatus.CANCELLED.value to OrderStatus.CANCELLED.displayName
    )
    
    /**
     * Check if any filters are active
     */
    val hasActiveFilters: Boolean
        get() = selectedStatus != null || 
                deliveryDateFrom != null || 
                deliveryDateTo != null ||
                orderDateFrom != null ||
                orderDateTo != null
}