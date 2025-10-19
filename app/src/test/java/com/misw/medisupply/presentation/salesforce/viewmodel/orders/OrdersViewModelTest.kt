package com.misw.medisupply.presentation.salesforce.viewmodel.orders

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.model.customer.DocumentType
import com.misw.medisupply.domain.usecase.customer.GetCustomersUseCase
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
        
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Loading()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads customers automatically`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = OrdersViewModel(getCustomersUseCase)

        verify(getCustomersUseCase).invoke(null, null, true)
    }

    @Test
    fun `loading customers shows loading state`() = runTest {

        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Loading()))


        viewModel = OrdersViewModel(getCustomersUseCase)

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

        viewModel = OrdersViewModel(getCustomersUseCase)

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

        viewModel = OrdersViewModel(getCustomersUseCase)

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
        viewModel = OrdersViewModel(getCustomersUseCase)

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
        viewModel = OrdersViewModel(getCustomersUseCase)

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
        viewModel = OrdersViewModel(getCustomersUseCase)
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
        viewModel = OrdersViewModel(getCustomersUseCase)

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
        viewModel = OrdersViewModel(getCustomersUseCase)
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
        viewModel = OrdersViewModel(getCustomersUseCase)

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
        viewModel = OrdersViewModel(getCustomersUseCase)
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
        viewModel = OrdersViewModel(getCustomersUseCase)

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
        
        viewModel = OrdersViewModel(getCustomersUseCase)

        viewModel.onEvent(OrdersEvent.FilterByType(CustomerType.FARMACIA))

        verify(getCustomersUseCase).invoke("farmacia", null, true)
    }

    @Test
    fun `multiple filter changes update state correctly`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), any()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))
        viewModel = OrdersViewModel(getCustomersUseCase)

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

        viewModel = OrdersViewModel(getCustomersUseCase)

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.customers.isEmpty())
            assertFalse(state.isLoading)
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
