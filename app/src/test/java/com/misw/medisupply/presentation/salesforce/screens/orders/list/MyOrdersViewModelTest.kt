package com.misw.medisupply.presentation.salesforce.screens.orders.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.model.customer.DocumentType
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderItem
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.domain.model.order.PaymentMethod
import com.misw.medisupply.domain.model.order.PaymentTerms
import com.misw.medisupply.domain.usecase.order.GetOrdersUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

/**
 * Unit tests for MyOrdersViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MyOrdersViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var getOrdersUseCase: GetOrdersUseCase
    private lateinit var viewModel: MyOrdersViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getOrdersUseCase = mockk()
        
        // Configure default mock behavior for init block
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Loading())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        // Given - configure mock to return empty list
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Success(emptyList()))
        
        // When
        viewModel = MyOrdersViewModel(getOrdersUseCase)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.orders).isEmpty()
            assertThat(state.isLoading).isFalse()
            assertThat(state.isRefreshing).isFalse()
            assertThat(state.error).isNull()
            assertThat(state.selectedStatus).isNull()
        }
    }

    @Test
    fun `LoadOrders event sets loading state then success with orders`() = runTest {
        // Given
        val mockOrders = listOf(createMockOrder(1), createMockOrder(2))
        
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(
            Resource.Loading(),
            Resource.Success(mockOrders)
        )

        viewModel = MyOrdersViewModel(getOrdersUseCase)

        // When
        viewModel.onEvent(MyOrdersEvent.LoadOrders)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.orders).hasSize(2)
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `LoadOrders event sets error when API fails`() = runTest {
        // Given
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(
            Resource.Loading(),
            Resource.Error("Network error")
        )

        viewModel = MyOrdersViewModel(getOrdersUseCase)

        // When
        viewModel.onEvent(MyOrdersEvent.LoadOrders)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.orders).isEmpty()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isEqualTo("Network error")
        }
    }

    @Test
    fun `RefreshOrders event sets refreshing state`() = runTest {
        // Given
        val mockOrders = listOf(createMockOrder(1))
        
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Success(mockOrders))

        viewModel = MyOrdersViewModel(getOrdersUseCase)

        // When
        viewModel.onEvent(MyOrdersEvent.RefreshOrders)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.isRefreshing).isFalse() // Should be false after completion
            assertThat(state.orders).hasSize(1)
        }
    }

    @Test
    fun `FilterByStatus updates selected status`() = runTest {
        // Given
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Success(emptyList()))
        
        viewModel = MyOrdersViewModel(getOrdersUseCase)
        advanceUntilIdle()

        // When
        viewModel.onEvent(MyOrdersEvent.FilterByStatus(OrderStatus.CONFIRMED))
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.selectedStatus).isEqualTo(OrderStatus.CONFIRMED)
        }
    }

    @Test
    fun `FilterByStatus with null clears filter`() = runTest {
        // Given
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Success(emptyList()))
        
        viewModel = MyOrdersViewModel(getOrdersUseCase)
        advanceUntilIdle()
        
        viewModel.onEvent(MyOrdersEvent.FilterByStatus(OrderStatus.CONFIRMED))
        advanceUntilIdle()

        // When
        viewModel.onEvent(MyOrdersEvent.FilterByStatus(null))
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.selectedStatus).isNull()
        }
    }

    @Test
    fun `getFilteredOrders returns all orders when no filter selected`() = runTest {
        // Given
        val mockOrders = listOf(
            createMockOrder(1).copy(status = OrderStatus.PENDING),
            createMockOrder(2).copy(status = OrderStatus.CONFIRMED)
        )
        
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Success(mockOrders))

        viewModel = MyOrdersViewModel(getOrdersUseCase)
        viewModel.onEvent(MyOrdersEvent.LoadOrders)
        advanceUntilIdle()

        // When/Then
        viewModel.state.test {
            val state = awaitItem()
            val filtered = state.getFilteredOrders()
            assertThat(filtered).hasSize(2)
        }
    }

    @Test
    fun `getFilteredOrders filters by selected status`() = runTest {
        // Given
        val mockOrders = listOf(
            createMockOrder(1).copy(status = OrderStatus.PENDING),
            createMockOrder(2).copy(status = OrderStatus.CONFIRMED),
            createMockOrder(3).copy(status = OrderStatus.PENDING)
        )
        
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Success(mockOrders))

        viewModel = MyOrdersViewModel(getOrdersUseCase)
        viewModel.onEvent(MyOrdersEvent.LoadOrders)
        advanceUntilIdle()
        
        viewModel.onEvent(MyOrdersEvent.FilterByStatus(OrderStatus.PENDING))
        advanceUntilIdle()

        // When/Then
        viewModel.state.test {
            val state = awaitItem()
            val filtered = state.getFilteredOrders()
            assertThat(filtered).hasSize(2)
            assertThat(filtered.all { it.status == OrderStatus.PENDING }).isTrue()
        }
    }

    @Test
    fun `SelectOrder event updates selected order`() = runTest {
        // Given
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Success(emptyList()))
        
        val mockOrder = createMockOrder(1)
        viewModel = MyOrdersViewModel(getOrdersUseCase)
        advanceUntilIdle()

        // When
        viewModel.onEvent(MyOrdersEvent.SelectOrder(mockOrder))
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.selectedOrder).isEqualTo(mockOrder)
        }
    }

    @Test
    fun `ClearError event clears error message`() = runTest {
        // Given
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Error("Test error"))

        viewModel = MyOrdersViewModel(getOrdersUseCase)
        viewModel.onEvent(MyOrdersEvent.LoadOrders)
        advanceUntilIdle()

        // When
        viewModel.onEvent(MyOrdersEvent.ClearError)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `hasOrders returns true when orders exist`() = runTest {
        // Given
        val mockOrders = listOf(createMockOrder(1))
        
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Success(mockOrders))

        viewModel = MyOrdersViewModel(getOrdersUseCase)
        viewModel.onEvent(MyOrdersEvent.LoadOrders)
        advanceUntilIdle()

        // When/Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.hasOrders()).isTrue()
        }
    }

    @Test
    fun `hasOrders returns false when no orders exist`() = runTest {
        // Given
        coEvery { 
            getOrdersUseCase(any(), any(), any()) 
        } returns flowOf(Resource.Success(emptyList()))
        
        viewModel = MyOrdersViewModel(getOrdersUseCase)
        advanceUntilIdle()

        // When/Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.hasOrders()).isFalse()
        }
    }

    private fun createMockOrder(id: Int): Order {
        return Order(
            id = id,
            orderNumber = "ORD-${String.format("%05d", id)}",
            customerId = 1,
            customer = Customer(
                id = 1,
                documentType = DocumentType.NIT,
                documentNumber = "123456789",
                businessName = "Test Customer",
                tradeName = "Test Trade Name",
                customerType = CustomerType.HOSPITAL,
                contactName = "John Doe",
                contactEmail = "test@example.com",
                contactPhone = "1234567890",
                address = "123 Test St",
                city = "Test City",
                department = "Test Dept",
                country = "Colombia",
                creditLimit = 10000.0,
                creditDays = 30,
                isActive = true,
                createdAt = Date(),
                updatedAt = Date()
            ),
            sellerId = "SELLER-001",
            sellerName = "Test Seller",
            orderDate = Date(),
            status = OrderStatus.PENDING,
            subtotal = 1000.0,
            discountAmount = 0.0,
            taxAmount = 190.0,
            totalAmount = 1190.0,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = PaymentMethod.TRANSFER,
            deliveryAddress = "123 Delivery St",
            deliveryCity = "Delivery City",
            deliveryDepartment = "Delivery Dept",
            preferredDistributionCenter = "DC-001",
            notes = "Test notes",
            createdAt = Date(),
            updatedAt = Date(),
            items = listOf(
                OrderItem(
                    id = 1,
                    orderId = id,
                    productSku = "TEST-001",
                    productName = "Test Product",
                    quantity = 10,
                    unitPrice = 100.0,
                    discountPercentage = 0.0,
                    discountAmount = 0.0,
                    taxPercentage = 19.0,
                    taxAmount = 190.0,
                    subtotal = 1000.0,
                    total = 1190.0,
                    distributionCenterCode = "DC-001",
                    stockConfirmed = true,
                    stockConfirmationDate = Date(),
                    createdAt = Date()
                )
            )
        )
    }
}
