package com.misw.medisupply.presentation.salesforce.viewmodel.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.usecase.order.GetOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel for Customer Detail Screen
 * Manages customer orders and statistics
 */
@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CustomerDetailState())
    val state: StateFlow<CustomerDetailState> = _state.asStateFlow()

    /**
     * Load customer orders and calculate statistics
     */
    fun loadCustomerOrders(customerId: Int) {
        getOrdersUseCase(
            customerId = customerId,
            perPage = 100 // Get more orders for better statistics
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoadingOrders = true, ordersError = null) }
                }
                is Resource.Success -> {
                    val paginatedResult = resource.data
                    val orders = paginatedResult?.items ?: emptyList()
                    val statistics = calculateStatistics(orders)
                    
                    _state.update {
                        it.copy(
                            isLoadingOrders = false,
                            orders = orders,
                            recentOrders = orders.take(5),
                            statistics = statistics,
                            ordersError = null
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoadingOrders = false,
                            ordersError = resource.message
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Calculate customer statistics from orders
     */
    private fun calculateStatistics(orders: List<Order>): CustomerStatistics {
        if (orders.isEmpty()) {
            return CustomerStatistics()
        }

        val totalOrders = orders.size
        val totalRevenue = orders.sumOf { it.totalAmount }
        val averageOrderValue = totalRevenue / totalOrders

        val activeOrders = orders.filter { order ->
            order.status.name in listOf("PENDING", "CONFIRMED", "PROCESSING", "SHIPPED")
        }

        val statusSummary = orders.groupingBy { it.status.name }.eachCount()

        // Top products (from all order items)
        val productQuantities = mutableMapOf<String, Pair<String, Int>>()
        orders.forEach { order ->
            order.items.forEach { item ->
                val current = productQuantities[item.productSku]
                productQuantities[item.productSku] = Pair(
                    item.productName,
                    (current?.second ?: 0) + item.quantity
                )
            }
        }

        val topProducts = productQuantities.entries
            .sortedByDescending { it.value.second }
            .take(5)
            .map { TopProduct(it.key, it.value.first, it.value.second) }

        return CustomerStatistics(
            totalOrders = totalOrders,
            totalRevenue = totalRevenue,
            averageOrderValue = averageOrderValue,
            activeOrdersCount = activeOrders.size,
            statusSummary = statusSummary,
            topProducts = topProducts
        )
    }

    /**
     * Clear state when leaving screen
     */
    fun clearState() {
        _state.value = CustomerDetailState()
    }
}

/**
 * State for Customer Detail Screen
 */
data class CustomerDetailState(
    val isLoadingOrders: Boolean = false,
    val orders: List<Order> = emptyList(),
    val recentOrders: List<Order> = emptyList(),
    val statistics: CustomerStatistics = CustomerStatistics(),
    val ordersError: String? = null
)

/**
 * Customer statistics calculated from orders
 */
data class CustomerStatistics(
    val totalOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val averageOrderValue: Double = 0.0,
    val activeOrdersCount: Int = 0,
    val statusSummary: Map<String, Int> = emptyMap(),
    val topProducts: List<TopProduct> = emptyList()
)

/**
 * Top product information
 */
data class TopProduct(
    val sku: String,
    val name: String,
    val totalQuantity: Int
)
