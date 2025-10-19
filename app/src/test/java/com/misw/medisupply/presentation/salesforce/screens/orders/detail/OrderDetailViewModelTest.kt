package com.misw.medisupply.presentation.salesforce.screens.orders.detail

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
import com.misw.medisupply.domain.usecase.order.DeleteOrderUseCase
import com.misw.medisupply.domain.usecase.order.GetOrderByIdUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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
 * Unit tests for OrderDetailViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OrderDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var getOrderByIdUseCase: GetOrderByIdUseCase
    private lateinit var deleteOrderUseCase: DeleteOrderUseCase
    private lateinit var viewModel: OrderDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getOrderByIdUseCase = mockk()
        deleteOrderUseCase = mockk()
        viewModel = OrderDetailViewModel(getOrderByIdUseCase, deleteOrderUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        // When/Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.order).isNull()
            assertThat(state.isLoading).isFalse()
            assertThat(state.isUpdating).isFalse()
            assertThat(state.isDeleting).isFalse()
            assertThat(state.error).isNull()
            assertThat(state.editedItems).isEmpty()
        }
    }

    @Test
    fun `loadOrderDetail loads order successfully`() = runTest {
        // Given
        val mockOrder = createMockOrder(1)
        coEvery { 
            getOrderByIdUseCase(1) 
        } returns flowOf(
            Resource.Loading(),
            Resource.Success(mockOrder)
        )

        // When
        viewModel.loadOrderDetail("1")
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.order).isNotNull()
            assertThat(state.order?.id).isEqualTo(1)
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `loadOrderDetail sets error when loading fails`() = runTest {
        // Given
        coEvery { 
            getOrderByIdUseCase(1) 
        } returns flowOf(
            Resource.Loading(),
            Resource.Error("Order not found")
        )

        // When
        viewModel.loadOrderDetail("1")
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.order).isNull()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isEqualTo("Order not found")
        }
    }

    @Test
    fun `UpdateItemQuantity event updates item quantity`() = runTest {
        // Given
        val mockOrder = createMockOrder(1)
        coEvery { getOrderByIdUseCase(1) } returns flowOf(Resource.Success(mockOrder))
        viewModel.loadOrderDetail("1")
        advanceUntilIdle()

        // When
        viewModel.onEvent(OrderDetailEvent.UpdateItemQuantity(1, 20))
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            val editedItem = state.editedItems[1]
            assertThat(editedItem).isNotNull()
            assertThat(editedItem?.quantity).isEqualTo(20)
            assertThat(editedItem?.subtotal).isEqualTo(2000.0) // 20 * 100
        }
    }

    @Test
    fun `RemoveItem event sets quantity to zero`() = runTest {
        // Given
        val mockOrder = createMockOrder(1)
        coEvery { getOrderByIdUseCase(1) } returns flowOf(Resource.Success(mockOrder))
        viewModel.loadOrderDetail("1")
        advanceUntilIdle()

        // When
        viewModel.onEvent(OrderDetailEvent.RemoveItem(1))
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            val editedItem = state.editedItems[1]
            assertThat(editedItem).isNotNull()
            assertThat(editedItem?.quantity).isEqualTo(0)
            assertThat(editedItem?.subtotal).isEqualTo(0.0)
        }
    }

    @Test
    fun `isModified returns true when items are edited`() = runTest {
        // Given
        val mockOrder = createMockOrder(1)
        coEvery { getOrderByIdUseCase(1) } returns flowOf(Resource.Success(mockOrder))
        viewModel.loadOrderDetail("1")
        advanceUntilIdle()

        // When
        viewModel.onEvent(OrderDetailEvent.UpdateItemQuantity(1, 20))
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.isModified()).isTrue()
        }
    }

    @Test
    fun `isModified returns false when no items are edited`() = runTest {
        // Given
        val mockOrder = createMockOrder(1)
        coEvery { getOrderByIdUseCase(1) } returns flowOf(Resource.Success(mockOrder))
        viewModel.loadOrderDetail("1")
        advanceUntilIdle()

        // When/Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.isModified()).isFalse()
        }
    }

    @Test
    fun `calculateNewTotal calculates correct total with edited items`() = runTest {
        // Given
        val mockOrder = createMockOrder(1)
        coEvery { getOrderByIdUseCase(1) } returns flowOf(Resource.Success(mockOrder))
        viewModel.loadOrderDetail("1")
        advanceUntilIdle()

        // When
        viewModel.onEvent(OrderDetailEvent.UpdateItemQuantity(1, 20))
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            val newTotal = state.calculateNewTotal()
            assertThat(newTotal).isEqualTo(2000.0) // 20 * 100
        }
    }

    @Test
    fun `getCurrentItems returns merged items with edits`() = runTest {
        // Given
        val mockOrder = createMockOrder(1)
        coEvery { getOrderByIdUseCase(1) } returns flowOf(Resource.Success(mockOrder))
        viewModel.loadOrderDetail("1")
        advanceUntilIdle()

        // When
        viewModel.onEvent(OrderDetailEvent.UpdateItemQuantity(1, 15))
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            val currentItems = state.getCurrentItems()
            assertThat(currentItems).hasSize(1)
            assertThat(currentItems[0].quantity).isEqualTo(15)
        }
    }

    @Test
    fun `DeleteOrder event deletes order successfully`() = runTest {
        // Given
        val mockOrder = createMockOrder(1)
        coEvery { getOrderByIdUseCase(1) } returns flowOf(Resource.Success(mockOrder))
        coEvery { 
            deleteOrderUseCase(1) 
        } returns flowOf(
            Resource.Loading(),
            Resource.Success(Unit)
        )
        
        viewModel.loadOrderDetail("1")
        advanceUntilIdle()

        // When
        viewModel.onEvent(OrderDetailEvent.DeleteOrder)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.isDeleting).isFalse()
            assertThat(state.deleteSuccess).isTrue()
            assertThat(state.error).isNull()
        }
        
        coVerify { deleteOrderUseCase(1) }
    }

    @Test
    fun `DeleteOrder event sets error when deletion fails`() = runTest {
        // Given
        val mockOrder = createMockOrder(1)
        coEvery { getOrderByIdUseCase(1) } returns flowOf(Resource.Success(mockOrder))
        coEvery { 
            deleteOrderUseCase(1) 
        } returns flowOf(
            Resource.Loading(),
            Resource.Error("No tiene permisos para eliminar este pedido")
        )
        
        viewModel.loadOrderDetail("1")
        advanceUntilIdle()

        // When
        viewModel.onEvent(OrderDetailEvent.DeleteOrder)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.isDeleting).isFalse()
            assertThat(state.deleteSuccess).isFalse()
            assertThat(state.error).contains("No tiene permisos")
        }
    }

    @Test
    fun `ClearError event clears error message`() = runTest {
        // Given
        coEvery { getOrderByIdUseCase(1) } returns flowOf(Resource.Error("Test error"))
        viewModel.loadOrderDetail("1")
        advanceUntilIdle()

        // When
        viewModel.onEvent(OrderDetailEvent.ClearError)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `RetryLoad event reloads order`() = runTest {
        // Given
        val mockOrder = createMockOrder(1)
        coEvery { getOrderByIdUseCase(1) } returns flowOf(Resource.Success(mockOrder))
        viewModel.loadOrderDetail("1")
        advanceUntilIdle()

        // When
        viewModel.onEvent(OrderDetailEvent.RetryLoad)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 2) { getOrderByIdUseCase(1) }
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
