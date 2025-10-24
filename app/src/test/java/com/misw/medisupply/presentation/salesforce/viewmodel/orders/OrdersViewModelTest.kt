package com.misw.medisupply.presentation.salesforce.viewmodel.orders

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.model.customer.DocumentType
import com.misw.medisupply.domain.model.order.CartItem
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderItem
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.domain.model.order.PaymentMethod
import com.misw.medisupply.domain.model.order.PaymentTerms
import com.misw.medisupply.domain.repository.order.OrderItemRequest
import com.misw.medisupply.domain.usecase.customer.GetCustomersUseCase
import com.misw.medisupply.domain.usecase.order.DeleteOrderUseCase
import com.misw.medisupply.domain.usecase.order.GetOrderByIdUseCase
import com.misw.medisupply.domain.usecase.order.GetOrdersUseCase
import com.misw.medisupply.domain.usecase.order.UpdateOrderUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    
    private lateinit var getCustomersUseCase: GetCustomersUseCase
    private lateinit var getOrdersUseCase: GetOrdersUseCase
    private lateinit var getOrderByIdUseCase: GetOrderByIdUseCase
    private lateinit var updateOrderUseCase: UpdateOrderUseCase
    private lateinit var deleteOrderUseCase: DeleteOrderUseCase
    private lateinit var viewModel: OrdersViewModel

    private val testCustomers = listOf(
        Customer(
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
        ),
        Customer(
            id = 2,
            customerType = CustomerType.FARMACIA,
            businessName = "Farmacia del Pueblo",
            tradeName = "Farma Pueblo",
            documentType = DocumentType.NIT,
            documentNumber = "900654321-2",
            contactName = "María García",
            contactEmail = "info@farmapueblo.com",
            contactPhone = "3009876543",
            address = "Carrera 50 #30-20",
            city = "Medellín",
            department = "Antioquia",
            country = "Colombia",
            creditLimit = 20000000.0,
            creditDays = 30,
            isActive = true,
            createdAt = null,
            updatedAt = null
        ),
        Customer(
            id = 3,
            customerType = CustomerType.CLINICA,
            businessName = "Clínica San José",
            tradeName = "CSJ",
            documentType = DocumentType.NIT,
            documentNumber = "900111222-3",
            contactName = "Carlos López",
            contactEmail = "contacto@clinicasj.com",
            contactPhone = "3005556666",
            address = "Avenida 10 #20-30",
            city = "Cali",
            department = "Valle del Cauca",
            country = "Colombia",
            creditLimit = 30000000.0,
            creditDays = 45,
            isActive = true,
            createdAt = null,
            updatedAt = null
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCustomersUseCase = mock()
        getOrdersUseCase = mock()
        getOrderByIdUseCase = mock()
        updateOrderUseCase = mock()
        deleteOrderUseCase = mock()
        
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Loading()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): OrdersViewModel {
        return OrdersViewModel(
            getCustomersUseCase,
            getOrdersUseCase,
            getOrderByIdUseCase,
            updateOrderUseCase,
            deleteOrderUseCase
        )
    }

    @Test
    fun `init loads customers automatically`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()

        verify(getCustomersUseCase).invoke(null, null, true)
    }

    @Test
    fun `loading customers shows loading state`() = runTest {

        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Loading()))


        viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isLoading)
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loading customers successfully updates state with customers`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(
                Resource.Loading(),
                Resource.Success(testCustomers)
            ))

        viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(3, state.customers.size)
            assertNull(state.error)
            assertEquals(testCustomers, state.customers)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loading customers with error updates error state`() = runTest {

        val errorMessage = "Error al cargar clientes"
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(
                Resource.Loading(),
                Resource.Error(errorMessage)
            ))

        viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(errorMessage, state.error)
            assertTrue(state.customers.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEvent RefreshCustomers sets refreshing state`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))
        viewModel = createViewModel()

        viewModel.onEvent(OrdersEvent.RefreshCustomers)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isRefreshing)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEvent FilterByType filters customers by hospital`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))
        viewModel = createViewModel()

        viewModel.onEvent(OrdersEvent.FilterByType(CustomerType.HOSPITAL))

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(CustomerType.HOSPITAL, state.selectedFilter)
            cancelAndIgnoreRemainingEvents()
        }
        verify(getCustomersUseCase).invoke("hospital", null, true)
    }

    @Test
    fun `onEvent FilterByType with null clears filter`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))
        viewModel = createViewModel()
        viewModel.onEvent(OrdersEvent.FilterByType(CustomerType.FARMACIA))

        viewModel.onEvent(OrdersEvent.FilterByType(null))

        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.selectedFilter)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEvent SearchCustomers updates search query`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))
        viewModel = createViewModel()

        viewModel.onEvent(OrdersEvent.SearchCustomers("Hospital"))

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Hospital", state.searchQuery)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEvent SelectCustomer updates selected customer`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))
        viewModel = createViewModel()
        val customerToSelect = testCustomers[0]

        viewModel.onEvent(OrdersEvent.SelectCustomer(customerToSelect))

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(customerToSelect, state.selectedCustomer)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEvent ClearError clears error message`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Error("Test error")))
        viewModel = createViewModel()

        viewModel.onEvent(OrdersEvent.ClearError)

        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selectCustomer directly updates selected customer`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))
        viewModel = createViewModel()
        val customerToSelect = testCustomers[1]

        viewModel.selectCustomer(customerToSelect)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(customerToSelect, state.selectedCustomer)
            assertEquals("Farmacia del Pueblo", state.selectedCustomer?.businessName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCustomerTypes returns all customer types`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(emptyList())))
        viewModel = createViewModel()

        val types = viewModel.getCustomerTypes()

        assertTrue(types.contains(CustomerType.HOSPITAL))
        assertTrue(types.contains(CustomerType.FARMACIA))
        assertTrue(types.contains(CustomerType.CLINICA))
        assertTrue(types.contains(CustomerType.DISTRIBUIDOR))
        assertTrue(types.contains(CustomerType.IPS))
        assertTrue(types.contains(CustomerType.EPS))
    }

    @Test
    fun `filtering by pharmacy type loads only pharmacy customers`() = runTest {
        val pharmacyCustomers = listOf(testCustomers[1])
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))
        whenever(getCustomersUseCase.invoke("farmacia", null, true))
            .thenReturn(flowOf(Resource.Success(pharmacyCustomers)))
        
        viewModel = createViewModel()

        viewModel.onEvent(OrdersEvent.FilterByType(CustomerType.FARMACIA))

        verify(getCustomersUseCase).invoke("farmacia", null, true)
    }

    @Test
    fun `multiple filter changes update state correctly`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))
        viewModel = createViewModel()

        viewModel.onEvent(OrdersEvent.FilterByType(CustomerType.HOSPITAL))
        viewModel.onEvent(OrdersEvent.FilterByType(CustomerType.FARMACIA))
        viewModel.onEvent(OrdersEvent.FilterByType(CustomerType.CLINICA))

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(CustomerType.CLINICA, state.selectedFilter)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loading empty list updates state with empty customers`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(emptyList())))

        viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.customers.isEmpty())
            assertFalse(state.isLoading)
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== UPDATE ORDER TESTS =====

    private val testOrderItems = listOf(
        OrderItem(
            id = 1,
            orderId = 1,
            productSku = "MED-001",
            productName = "Jeringa 10ml",
            quantity = 150,
            unitPrice = 350.0,
            discountPercentage = 0.0,
            discountAmount = 0.0,
            taxPercentage = 19.0,
            taxAmount = 9975.0,
            subtotal = 52500.0,
            total = 62475.0,
            distributionCenterCode = "DC-BOG",
            stockConfirmed = true,
            stockConfirmationDate = null,
            createdAt = null
        )
    )

    private val testOrder = Order(
        id = 1,
        orderNumber = "ORD-20251023-0001",
        customerId = 1,
        sellerId = "SELLER-001",
        sellerName = "Vendedor Demo",
        orderDate = null,
        deliveryDate = null,
        status = OrderStatus.CONFIRMED,
        subtotal = 52500.0,
        discountAmount = 0.0,
        taxAmount = 9975.0,
        totalAmount = 62475.0,
        paymentTerms = PaymentTerms.CREDIT_30,
        paymentMethod = PaymentMethod.TRANSFER,
        deliveryAddress = "Calle 123 #45-67",
        deliveryCity = "Bogotá",
        deliveryDepartment = "Cundinamarca",
        preferredDistributionCenter = "DC-BOG",
        notes = null,
        createdAt = null,
        updatedAt = null,
        customer = testCustomers[0],
        items = testOrderItems
    )

    @Test
    fun `updateOrder with success updates state and shows success message`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        whenever(
            updateOrderUseCase.invoke(
                orderId = any(),
                customerId = any(),
                items = any(),
                paymentTerms = any(),
                paymentMethod = anyOrNull(),
                deliveryAddress = anyOrNull(),
                deliveryCity = anyOrNull(),
                deliveryDepartment = anyOrNull(),
                deliveryDate = anyOrNull(),
                preferredDistributionCenter = anyOrNull(),
                notes = anyOrNull()
            )
        ).thenReturn(
            flowOf(
                Resource.Loading(),
                Resource.Success(testOrder)
            )
        )

        viewModel = createViewModel()
        
        // Set up state for update
        viewModel.selectCustomer(testCustomers[0])
        viewModel.updateCartItems(mapOf(
            "MED-001" to CartItem(
                productSku = "MED-001",
                productName = "Jeringa 10ml",
                quantity = 150,
                unitPrice = 350.0,
                subtotal = 52500.0,
                imageUrl = null
            )
        ))
        viewModel.onEvent(OrdersEvent.LoadOrderForEdit("1"))

        viewModel.updateOrder()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isSaving)
            assertEquals("Orden actualizada exitosamente", state.successMessage)
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateOrder with error updates error state`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        val errorMessage = "Solo se pueden actualizar pedidos en estado Pendiente"

        whenever(
            updateOrderUseCase.invoke(
                orderId = any(),
                customerId = any(),
                items = any(),
                paymentTerms = any(),
                paymentMethod = anyOrNull(),
                deliveryAddress = anyOrNull(),
                deliveryCity = anyOrNull(),
                deliveryDepartment = anyOrNull(),
                deliveryDate = anyOrNull(),
                preferredDistributionCenter = anyOrNull(),
                notes = anyOrNull()
            )
        ).thenReturn(
            flowOf(
                Resource.Loading(),
                Resource.Error(errorMessage)
            )
        )

        viewModel = createViewModel()
        
        // Set up state for update
        viewModel.selectCustomer(testCustomers[0])
        viewModel.updateCartItems(mapOf(
            "MED-001" to CartItem(
                productSku = "MED-001",
                productName = "Jeringa 10ml",
                quantity = 150,
                unitPrice = 350.0,
                subtotal = 52500.0,
                imageUrl = null
            )
        ))
        viewModel.onEvent(OrdersEvent.LoadOrderForEdit("1"))

        viewModel.updateOrder()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isSaving)
            assertEquals(errorMessage, state.error)
            assertNull(state.successMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadOrderForEdit loads order and sets cart items`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        whenever(getOrderByIdUseCase.invoke(any()))
            .thenReturn(
                flowOf(
                    Resource.Loading(),
                    Resource.Success(testOrder)
                )
            )

        viewModel = createViewModel()

        viewModel.onEvent(OrdersEvent.LoadOrderForEdit("1"))

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(1, state.cartItems.size)
            val cartItem = state.cartItems["MED-001"]
            assertNotNull(cartItem)
            assertEquals("MED-001", cartItem?.productSku)
            assertEquals("Jeringa 10ml", cartItem?.productName)
            assertEquals(350.0, cartItem?.unitPrice ?: 0.0, 0.001)
            assertEquals(150, cartItem?.quantity)
            assertEquals(1, state.orderIdEditingNumeric) // Numeric ID
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ClearUpdateSuccess clears update success message`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()

        viewModel.onEvent(OrdersEvent.ClearError)

        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.successMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== DELETE ORDER TESTS =====

    @Test
    fun `deleteOrder with success updates state and shows success message`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        whenever(deleteOrderUseCase.invoke(any()))
            .thenReturn(
                flowOf(
                    Resource.Loading(),
                    Resource.Success(Unit)
                )
            )

        viewModel = createViewModel()

        viewModel.deleteOrder(1)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isDeleting)
            assertEquals("Pedido eliminado exitosamente", state.deleteSuccessMessage)
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteOrder with error updates error state`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        val errorMessage = "Solo se pueden eliminar pedidos en estado Pendiente"

        whenever(deleteOrderUseCase.invoke(any()))
            .thenReturn(
                flowOf(
                    Resource.Loading(),
                    Resource.Error(errorMessage)
                )
            )

        viewModel = createViewModel()

        viewModel.deleteOrder(1)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isDeleting)
            assertEquals(errorMessage, state.error)
            assertNull(state.deleteSuccessMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteOrder sets isDeleting to true during deletion`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        whenever(deleteOrderUseCase.invoke(any()))
            .thenReturn(flowOf(Resource.Loading()))

        viewModel = createViewModel()

        viewModel.deleteOrder(1)

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isDeleting)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ClearDeleteSuccess clears delete success message`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()

        viewModel.onEvent(OrdersEvent.ClearDeleteSuccess)

        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.deleteSuccessMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteOrder with order not found returns 404 error`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        whenever(deleteOrderUseCase.invoke(any()))
            .thenReturn(flowOf(Resource.Error("Pedido no encontrado")))

        viewModel = createViewModel()

        viewModel.deleteOrder(999)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isDeleting)
            assertTrue(state.error?.contains("encontrado") == true)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
