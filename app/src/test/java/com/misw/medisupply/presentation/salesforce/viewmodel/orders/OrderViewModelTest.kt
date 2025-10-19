package com.misw.medisupply.presentation.salesforce.viewmodel.orders

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.model.customer.DocumentType
import com.misw.medisupply.domain.model.order.CartItem
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.PaymentMethod
import com.misw.medisupply.domain.model.order.PaymentTerms
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.domain.repository.order.OrderItemRequest
import com.misw.medisupply.domain.usecase.order.CreateOrderUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.eq


@OptIn(ExperimentalCoroutinesApi::class)
class OrderViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    
    private lateinit var createOrderUseCase: CreateOrderUseCase
    private lateinit var viewModel: OrderViewModel

    private val testCustomer = Customer(
        id = 1,
        customerType = CustomerType.HOSPITAL,
        businessName = "Hospital Central",
        tradeName = "HC Central",
        documentType = DocumentType.NIT,
        documentNumber = "900123456-1",
        contactName = "Juan Pérez",
        contactEmail = "contacto@hospitalcentral.com",
        contactPhone = "3001234567",
        address = "Calle 123 #45-67",
        city = "Bogotá",
        department = "Cundinamarca",
        country = "Colombia",
        creditLimit = 50000000.0,
        creditDays = 30,
        isActive = true,
        createdAt = null,
        updatedAt = null
    )

    private val testCartItems = mapOf(
        "PROD001" to CartItem(
            productSku = "PROD001",
            productName = "Product 1",
            quantity = 2,
            unitPrice = 100.0f,
            stockAvailable = 10,
            requiresColdChain = false,
            category = "Medicamentos"
        ),
        "PROD002" to CartItem(
            productSku = "PROD002",
            productName = "Product 2",
            quantity = 1,
            unitPrice = 50.0f,
            stockAvailable = 5,
            requiresColdChain = true,
            category = "Insumos"
        )
    )

    private val testOrder = Order(
        id = 1,
        orderNumber = "ORD-2024-001",
        customerId = 1,
        sellerId = "SELLER001",
        sellerName = "Vendedor Demo",
        orderDate = null,
        status = OrderStatus.PENDING,
        subtotal = 250.0,
        discountAmount = 0.0,
        taxAmount = 47.5,
        totalAmount = 297.5,
        paymentTerms = PaymentTerms.CASH,
        paymentMethod = PaymentMethod.CASH,
        deliveryAddress = "Calle 123 #45-67",
        deliveryCity = "Bogotá",
        deliveryDepartment = "Cundinamarca",
        preferredDistributionCenter = null,
        notes = null,
        createdAt = null,
        updatedAt = null,
        customer = null,
        items = emptyList()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        createOrderUseCase = mock()
        viewModel = OrderViewModel(createOrderUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertNull(state.createdOrder)
    }

    @Test
    fun `createOrder shows loading state`() = runTest {
        whenever(createOrderUseCase.invoke(
            any(), any(), any(), any(), any(), anyOrNull(), any(), any(), any(), anyOrNull(), anyOrNull()
        )).thenReturn(flowOf(Resource.Loading()))

        viewModel.createOrder(
            customer = testCustomer,
            cartItems = testCartItems,
            paymentTerms = PaymentTerms.CASH
        )

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isLoading)
            assertNull(state.error)
            assertNull(state.createdOrder)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `createOrder successfully creates order`() = runTest {
        whenever(createOrderUseCase.invoke(
            any(), any(), any(), any(), any(), anyOrNull(), any(), any(), any(), anyOrNull(), anyOrNull()
        )).thenReturn(flowOf(
            Resource.Loading(),
            Resource.Success(testOrder)
        ))

        viewModel.createOrder(
            customer = testCustomer,
            cartItems = testCartItems
        )

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNull(state.error)
            assertNotNull(state.createdOrder)
            assertEquals(testOrder, state.createdOrder)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `createOrder with error updates error state`() = runTest {
        val errorMessage = "Error al crear la orden"
        whenever(createOrderUseCase.invoke(
            any(), any(), any(), any(), any(), anyOrNull(), any(), any(), any(), anyOrNull(), anyOrNull()
        )).thenReturn(flowOf(
            Resource.Loading(),
            Resource.Error(errorMessage)
        ))

        viewModel.createOrder(
            customer = testCustomer,
            cartItems = testCartItems
        )

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(errorMessage, state.error)
            assertNull(state.createdOrder)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `createOrder calls use case with correct parameters`() = runTest {
        whenever(createOrderUseCase.invoke(
            any(), any(), any(), any(), any(), anyOrNull(), any(), any(), any(), anyOrNull(), anyOrNull()
        )).thenReturn(flowOf(Resource.Success(testOrder)))

        viewModel.createOrder(
            customer = testCustomer,
            cartItems = testCartItems,
            paymentTerms = PaymentTerms.CREDIT_30,
            paymentMethod = PaymentMethod.TRANSFER
        )

        verify(createOrderUseCase).invoke(
            customerId = eq(testCustomer.id),
            sellerId = eq("SELLER-001"),
            sellerName = eq("Vendedor Demo"),
            items = any(),
            paymentTerms = eq(PaymentTerms.CREDIT_30),
            paymentMethod = eq(PaymentMethod.TRANSFER),
            deliveryAddress = eq(testCustomer.address),
            deliveryCity = eq(testCustomer.city),
            deliveryDepartment = eq(testCustomer.department),
            preferredDistributionCenter = anyOrNull(),
            notes = anyOrNull()
        )
    }

    @Test
    fun `createOrder with custom delivery address uses custom address`() = runTest {
        val customAddress = "Custom Address 123"
        val customCity = "Custom City"
        val customDepartment = "Custom Department"
        
        whenever(createOrderUseCase.invoke(
            any(), any(), any(), any(), any(), anyOrNull(), any(), any(), any(), anyOrNull(), anyOrNull()
        )).thenReturn(flowOf(Resource.Success(testOrder)))

        viewModel.createOrder(
            customer = testCustomer,
            cartItems = testCartItems,
            deliveryAddress = customAddress,
            deliveryCity = customCity,
            deliveryDepartment = customDepartment
        )

        verify(createOrderUseCase).invoke(
            customerId = any(),
            sellerId = any(),
            sellerName = any(),
            items = any(),
            paymentTerms = any(),
            paymentMethod = anyOrNull(),
            deliveryAddress = eq(customAddress),
            deliveryCity = eq(customCity),
            deliveryDepartment = eq(customDepartment),
            preferredDistributionCenter = anyOrNull(),
            notes = anyOrNull()
        )
    }

    @Test
    fun `createOrder with notes passes notes to use case`() = runTest {
        val notes = "Urgent delivery required"
        whenever(createOrderUseCase.invoke(
            any(), any(), any(), any(), any(), anyOrNull(), any(), any(), any(), anyOrNull(), anyOrNull()
        )).thenReturn(flowOf(Resource.Success(testOrder)))

        viewModel.createOrder(
            customer = testCustomer,
            cartItems = testCartItems,
            notes = notes
        )

        verify(createOrderUseCase).invoke(
            customerId = any(),
            sellerId = any(),
            sellerName = any(),
            items = any(),
            paymentTerms = any(),
            paymentMethod = anyOrNull(),
            deliveryAddress = any(),
            deliveryCity = any(),
            deliveryDepartment = any(),
            preferredDistributionCenter = anyOrNull(),
            notes = eq(notes)
        )
    }

    @Test
    fun `resetState clears all state`() = runTest {
        whenever(createOrderUseCase.invoke(
            any(), any(), any(), any(), any(), anyOrNull(), any(), any(), any(), anyOrNull(), anyOrNull()
        )).thenReturn(flowOf(Resource.Success(testOrder)))
        
        viewModel.createOrder(testCustomer, testCartItems)

        viewModel.resetState()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertNull(state.createdOrder)
    }

    @Test
    fun `clearError clears error message`() = runTest {
        whenever(createOrderUseCase.invoke(
            any(), any(), any(), any(), any(), anyOrNull(), any(), any(), any(), anyOrNull(), anyOrNull()
        )).thenReturn(flowOf(Resource.Error("Test error")))
        
        viewModel.createOrder(testCustomer, testCartItems)

        viewModel.clearError()

        val state = viewModel.state.value
        assertNull(state.error)
    }

    @Test
    fun `createOrder converts cart items to order items correctly`() = runTest {
        whenever(createOrderUseCase.invoke(
            any(), any(), any(), any(), any(), anyOrNull(), any(), any(), any(), anyOrNull(), anyOrNull()
        )).thenReturn(flowOf(Resource.Success(testOrder)))

        viewModel.createOrder(
            customer = testCustomer,
            cartItems = testCartItems
        )

        verify(createOrderUseCase).invoke(
            customerId = any(),
            sellerId = any(),
            sellerName = any(),
            items = any<List<OrderItemRequest>>(),
            paymentTerms = any(),
            paymentMethod = anyOrNull(),
            deliveryAddress = any(),
            deliveryCity = any(),
            deliveryDepartment = any(),
            preferredDistributionCenter = anyOrNull(),
            notes = anyOrNull()
        )
    }

    @Test
    fun `createOrder with empty cart items creates order with empty items`() = runTest {
        whenever(createOrderUseCase.invoke(
            any(), any(), any(), any(), any(), anyOrNull(), any(), any(), any(), anyOrNull(), anyOrNull()
        )).thenReturn(flowOf(Resource.Success(testOrder)))

        viewModel.createOrder(
            customer = testCustomer,
            cartItems = emptyMap()
        )

        verify(createOrderUseCase).invoke(
            customerId = any(),
            sellerId = any(),
            sellerName = any(),
            items = any<List<OrderItemRequest>>(),
            paymentTerms = any(),
            paymentMethod = anyOrNull(),
            deliveryAddress = any(),
            deliveryCity = any(),
            deliveryDepartment = any(),
            preferredDistributionCenter = anyOrNull(),
            notes = anyOrNull()
        )
    }

    @Test
    fun `createOrder uses default payment terms when not specified`() = runTest {
        whenever(createOrderUseCase.invoke(
            any(), any(), any(), any(), any(), anyOrNull(), any(), any(), any(), anyOrNull(), anyOrNull()
        )).thenReturn(flowOf(Resource.Success(testOrder)))

        viewModel.createOrder(
            customer = testCustomer,
            cartItems = testCartItems
        )

        verify(createOrderUseCase).invoke(
            customerId = any(),
            sellerId = any(),
            sellerName = any(),
            items = any(),
            paymentTerms = eq(PaymentTerms.CASH),
            paymentMethod = anyOrNull(),
            deliveryAddress = any(),
            deliveryCity = any(),
            deliveryDepartment = any(),
            preferredDistributionCenter = anyOrNull(),
            notes = anyOrNull()
        )
    }

    @Test
    fun `multiple createOrder calls update state correctly`() = runTest {
        whenever(createOrderUseCase.invoke(
            any(), any(), any(), any(), any(), anyOrNull(), any(), any(), any(), anyOrNull(), anyOrNull()
        )).thenReturn(flowOf(Resource.Success(testOrder)))

        viewModel.createOrder(testCustomer, testCartItems)
        viewModel.resetState()
        viewModel.createOrder(testCustomer, testCartItems)

        viewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.createdOrder)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
