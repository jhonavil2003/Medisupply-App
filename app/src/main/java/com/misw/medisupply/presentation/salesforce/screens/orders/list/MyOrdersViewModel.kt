// kotlin
package com.misw.medisupply.presentation.salesforce.screens.orders.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.core.session.UserSessionManager
import com.misw.medisupply.data.repository.auth.AuthRepository
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.domain.usecase.customer.GetCustomersBySalespersonEployeeIdUseCase
import com.misw.medisupply.domain.usecase.customer.GetCustomersUseCase
import com.misw.medisupply.domain.usecase.order.DeleteOrderUseCase
import com.misw.medisupply.domain.usecase.order.GetOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyOrdersViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val getCustomersUseCase: GetCustomersUseCase,
    private val getCustomersBySalespersonEployeeIdUseCase: GetCustomersBySalespersonEployeeIdUseCase,
    private val deleteOrderUseCase: DeleteOrderUseCase,
    private val userSessionManager: UserSessionManager,
) : ViewModel() {

    companion object {
        private const val TAG = "MyOrdersViewModel"
    }

    private val _state = MutableStateFlow(MyOrdersState())
    val state: StateFlow<MyOrdersState> = _state.asStateFlow()

    //private val currentSellerId: String = "SELLER-001"
    private val maxResults: Int = 1000

    init {
        Log.d(TAG, "init - starting loadOrders & loadCustomers")
        loadOrders()
        loadCustomers()
    }

    fun onEvent(event: MyOrdersEvent) {
        when (event) {
            is MyOrdersEvent.LoadOrders -> {
                Log.d(TAG, "Event: LoadOrders")
                loadOrders()
            }
            is MyOrdersEvent.LoadCustomers -> {
                Log.d(TAG, "Event: LoadCustomers")
                loadCustomers()
            }
            is MyOrdersEvent.RefreshOrders -> {
                Log.d(TAG, "Event: RefreshOrders")
                refreshOrders()
            }
            is MyOrdersEvent.FilterByStatus -> {
                Log.d(TAG, "Event: FilterByStatus -> ${event.status}")
                filterByStatus(event.status)
            }
            is MyOrdersEvent.FilterByCustomer -> {
                Log.d(TAG, "Event: FilterByCustomer -> ${event.customerId}")
                filterByCustomer(event.customerId)
            }
            is MyOrdersEvent.FilterByDateRange -> {
                Log.d(TAG, "Event: FilterByDateRange -> ${event.dateRange}")
                filterByDateRange(event.dateRange)
            }
            is MyOrdersEvent.ClearFilters -> {
                Log.d(TAG, "Event: ClearFilters")
                clearAllFilters()
            }
            is MyOrdersEvent.SelectOrder -> {
                Log.d(TAG, "Event: SelectOrder -> ${event.order.id}")
                selectOrder(event.order)
            }
            is MyOrdersEvent.ClearError -> {
                Log.d(TAG, "Event: ClearError")
                clearError()
            }
            is MyOrdersEvent.DeleteOrder -> {
                Log.d(TAG, "Event: DeleteOrder -> ${event.orderId}")
                deleteOrder(event.orderId)
            }
            is MyOrdersEvent.ClearSuccessMessage -> {
                Log.d(TAG, "Event: ClearSuccessMessage")
                clearSuccessMessage()
            }
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            //Log.d(TAG, "loadOrders - start sellerId=$currentSellerId")
            val currentSellerId = userSessionManager.requireSalespersonSubString()
            Log.d("HomeViewModel", "SalespersonId = $currentSellerId")
            _state.update { it.copy(isLoading = true, error = null) }

            val currentState = _state.value

            getOrdersUseCase(
                sellerId = currentSellerId,
                customerId = currentState.selectedCustomerId,
                status = currentState.selectedStatus?.value,
                page = 1,
                perPage = maxResults
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Log.d(TAG, "loadOrders - loading")
                    }
                    is Resource.Success -> {
                        val paginatedResult = resource.data
                        val count = paginatedResult?.items?.size ?: 0
                        Log.d(TAG, "loadOrders - success items=$count")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                orders = paginatedResult?.items ?: emptyList(),
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "loadOrders - error: ${resource.message}")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = resource.message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadCustomers() {
        viewModelScope.launch {
            Log.d(TAG, "loadCustomers - start")
            _state.update { it.copy(isLoadingCustomers = true) }


            val salespersonId = userSessionManager.requireSalespersonSubString()
            Log.d("HomeViewModel", "SalespersonId = $salespersonId")


            getCustomersBySalespersonEployeeIdUseCase(
                salespersonId = salespersonId
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Log.d(TAG, "loadCustomers - loading")
                    }
                    is Resource.Success -> {
                        Log.d(TAG, "loadCustomers - success count=${resource.data?.size ?: 0}")
                        _state.update {
                            it.copy(
                                isLoadingCustomers = false,
                                customers = resource.data ?: emptyList()
                            )
                        }
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "loadCustomers - error: ${resource.message}")
                        _state.update {
                            it.copy(
                                isLoadingCustomers = false,
                                error = resource.message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun refreshOrders() {
        viewModelScope.launch {
            Log.d(TAG, "refreshOrders - start")
            _state.update { it.copy(isRefreshing = true) }
            loadOrders()
        }
    }

    private fun filterByStatus(status: OrderStatus?) {
        Log.d(TAG, "filterByStatus -> $status")
        _state.update { it.copy(selectedStatus = status) }
        loadOrders()
    }

    private fun filterByCustomer(customerId: Int?) {
        Log.d(TAG, "filterByCustomer -> $customerId")
        _state.update { it.copy(selectedCustomerId = customerId) }
        loadOrders()
    }

    private fun filterByDateRange(dateRange: DateRange?) {
        Log.d(TAG, "filterByDateRange -> $dateRange")
        _state.update { it.copy(selectedDateRange = dateRange) }
    }

    private fun clearAllFilters() {
        Log.d(TAG, "clearAllFilters")
        _state.update {
            it.copy(
                selectedStatus = null,
                selectedCustomerId = null,
                selectedDateRange = null
            )
        }
        loadOrders()
    }

    private fun selectOrder(order: Order) {
        Log.d(TAG, "selectOrder -> ${order.id}")
        _state.update { it.copy(selectedOrder = order) }
    }

    private fun clearError() {
        Log.d(TAG, "clearError")
        _state.update { it.copy(error = null) }
    }

    private fun deleteOrder(orderId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "deleteOrder - start orderId=$orderId")
            _state.update { it.copy(isDeleting = true, error = null) }

            deleteOrderUseCase(orderId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Log.d(TAG, "deleteOrder - loading")
                    }
                    is Resource.Success -> {
                        Log.d(TAG, "deleteOrder - success orderId=$orderId")
                        _state.update { currentState ->
                            currentState.copy(
                                isDeleting = false,
                                orders = currentState.orders.filter { it.id != orderId },
                                successMessage = "order_deleted_successfully",
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "deleteOrder - error: ${resource.message}")
                        _state.update {
                            it.copy(
                                isDeleting = false,
                                error = resource.message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun clearSuccessMessage() {
        Log.d(TAG, "clearSuccessMessage")
        _state.update { it.copy(successMessage = null) }
    }
}
