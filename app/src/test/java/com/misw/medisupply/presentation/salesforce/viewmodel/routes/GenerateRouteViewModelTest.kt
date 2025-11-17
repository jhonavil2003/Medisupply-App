package com.misw.medisupply.presentation.salesforce.screens.routes.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.core.session.UserSessionManager
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.customer.CustomerType
import com.misw.medisupply.domain.model.customer.DocumentType
import com.misw.medisupply.domain.model.route.OptimizationStrategy
import com.misw.medisupply.domain.model.route.Route
import com.misw.medisupply.domain.model.route.RouteMetrics
import com.misw.medisupply.domain.model.route.RouteStatus
import com.misw.medisupply.domain.usecase.customer.GetCustomersUseCase
import com.misw.medisupply.domain.usecase.route.GenerateRouteUseCase
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
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class GenerateRouteViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var generateRouteUseCase: GenerateRouteUseCase
    private lateinit var getCustomersUseCase: GetCustomersUseCase
    private lateinit var userSessionManager: UserSessionManager
    private lateinit var viewModel: GenerateRouteViewModel

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
            latitude = 4.6097,
            longitude = -74.0817,
            creditLimit = 10000.0,
            creditDays = 30,
            isActive = true,
            createdAt = null,
            updatedAt = null,
            salespersonId = 1,
            salesperson = null
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
            latitude = 4.6143,
            longitude = -74.0721,
            creditLimit = 15000.0,
            creditDays = 30,
            isActive = true,
            createdAt = null,
            updatedAt = null,
            salespersonId = 1,
            salesperson = null
        ),
        Customer(
            id = 3,
            customerType = CustomerType.HOSPITAL,
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
            latitude = null, // Cliente sin GPS
            longitude = null,
            creditLimit = 20000.0,
            creditDays = 45,
            isActive = true,
            createdAt = null,
            updatedAt = null,
            salespersonId = 1,
            salesperson = null
        )
    )

    private val testRoute = Route(
        id = 1,
        routeCode = "RT-20251117-001",
        salespersonId = 1,
        salespersonName = "Vendedor Demo",
        salespersonEmployeeId = "EMP001",
        plannedDate = LocalDate.now().plusDays(1),
        status = RouteStatus.DRAFT,
        metrics = RouteMetrics(
            totalStops = 2,
            totalDistanceKm = 15.5,
            estimatedDurationMinutes = 120,
            optimizationScore = 0.95
        ),
        stops = emptyList(),
        startLocation = null,
        endLocation = null,
        createdAt = null,
        updatedAt = null
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        generateRouteUseCase = mock()
        getCustomersUseCase = mock()
        userSessionManager = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): GenerateRouteViewModel {
        return GenerateRouteViewModel(
            generateRouteUseCase,
            getCustomersUseCase,
            userSessionManager
        )
    }

    @Test
    fun `init loads customers with GPS coordinates only`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Loading(), Resource.Success(testCustomers)))

        viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoadingCustomers)
            // Solo 2 clientes tienen GPS (cliente 3 no tiene)
            assertEquals(2, state.customers.size)
            assertTrue(state.customers.all { it.latitude != null && it.longitude != null })
        }
    }

    @Test
    fun `init loading customers shows loading state`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Loading()))

        viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isLoadingCustomers)
        }
    }

    @Test
    fun `init with no customers shows error message`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Loading(), Resource.Success(emptyList())))

        viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("No hay clientes asignados con ubicación GPS configurada", state.customerError)
        }
    }

    @Test
    fun `init with error shows error message`() = runTest {
        val errorMessage = "Error al cargar clientes"
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Loading(), Resource.Error(errorMessage)))

        viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(errorMessage, state.customerError)
        }
    }

    @Test
    fun `updateSearchQuery updates search query in state`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()

        viewModel.updateSearchQuery("Hospital")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Hospital", state.searchQuery)
        }
    }

    @Test
    fun `toggleCustomerSelection adds customer when not selected`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()
        viewModel.toggleCustomerSelection(1)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(1 in state.selectedCustomerIds)
            assertTrue(state.isFormValid)
        }
    }

    @Test
    fun `toggleCustomerSelection removes customer when already selected`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()
        viewModel.toggleCustomerSelection(1)
        viewModel.toggleCustomerSelection(1) // Deselect

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(1 in state.selectedCustomerIds)
            assertFalse(state.isFormValid)
        }
    }

    @Test
    fun `toggleCustomerSelection shows error when exceeding 20 customers`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()

        // Select 20 customers
        repeat(20) { viewModel.toggleCustomerSelection(it) }
        
        // Try to select 21st customer
        viewModel.toggleCustomerSelection(21)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Máximo 20 clientes por ruta", state.validationError)
            assertEquals(20, state.selectedCustomerIds.size)
        }
    }

    @Test
    fun `clearSelection removes all selected customers`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()

        // Select customers
        viewModel.toggleCustomerSelection(1)
        viewModel.toggleCustomerSelection(2)
        viewModel.clearSelection()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.selectedCustomerIds.isEmpty())
            assertFalse(state.isFormValid)
        }
    }

    @Test
    fun `updateSelectedDate updates date in state`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()
        val newDate = LocalDate.now().plusDays(5)
        viewModel.updateSelectedDate(newDate)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(newDate, state.selectedDate)
        }
    }

    @Test
    fun `updateOptimizationStrategy updates strategy in state`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()
        viewModel.updateOptimizationStrategy(OptimizationStrategy.MINIMIZE_TIME)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(OptimizationStrategy.MINIMIZE_TIME, state.optimizationStrategy)
        }
    }

    @Test
    fun `updateWorkHoursStart updates start time in state`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()
        val newTime = LocalTime.of(9, 0)
        viewModel.updateWorkHoursStart(newTime)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(newTime, state.workHoursStart)
        }
    }

    @Test
    fun `updateWorkHoursEnd updates end time in state`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()
        val newTime = LocalTime.of(18, 0)
        viewModel.updateWorkHoursEnd(newTime)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(newTime, state.workHoursEnd)
        }
    }

    @Test
    fun `updateServiceTime updates service time within valid range`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()
        viewModel.updateServiceTime(45)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(45, state.serviceTimeMinutes)
        }
    }

    @Test
    fun `updateServiceTime coerces value to minimum 15 minutes`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()
        viewModel.updateServiceTime(5)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(15, state.serviceTimeMinutes)
        }
    }

    @Test
    fun `updateServiceTime coerces value to maximum 120 minutes`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()
        viewModel.updateServiceTime(150)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(120, state.serviceTimeMinutes)
        }
    }

    @Test
    fun `generateRoute with valid data calls use case and invokes onSuccess`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        whenever(
            generateRouteUseCase.invoke(
                salespersonId = any(),
                salespersonName = any(),
                employeeId = any(),
                customerIds = any(),
                plannedDate = any(),
                optimizationStrategy = any(),
                startLocation = anyOrNull(),
                endLocation = anyOrNull(),
                workHours = anyOrNull(),
                serviceTimePerVisitMinutes = any()
            )
        ).thenReturn(Result.success(Pair(testRoute, 120.0)))

        viewModel = createViewModel()
        viewModel.toggleCustomerSelection(1)
        viewModel.toggleCustomerSelection(2)

        var successCalled = false
        var routeId = 0

        viewModel.generateRoute { id ->
            successCalled = true
            routeId = id
        }

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isGenerating)
            assertNull(state.error)
            assertTrue(successCalled)
            assertEquals(1, routeId)
        }
    }

    @Test
    fun `generateRoute without selected customers shows error`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()

        var successCalled = false
        viewModel.generateRoute { successCalled = true }

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Complete todos los campos requeridos", state.error)
            assertFalse(successCalled)
        }
    }

    @Test
    fun `generateRoute with use case error shows error message`() = runTest {
        val errorMessage = "Error al generar ruta"
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        whenever(
            generateRouteUseCase.invoke(
                salespersonId = any(),
                salespersonName = any(),
                employeeId = any(),
                customerIds = any(),
                plannedDate = any(),
                optimizationStrategy = any(),
                startLocation = anyOrNull(),
                endLocation = anyOrNull(),
                workHours = anyOrNull(),
                serviceTimePerVisitMinutes = any()
            )
        ).thenReturn(Result.failure(Exception(errorMessage)))

        viewModel = createViewModel()
        viewModel.toggleCustomerSelection(1)

        viewModel.generateRoute {}

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(errorMessage, state.error)
        }
    }

    @Test
    fun `toggleCustomStartLocation enables custom start location`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()
        viewModel.toggleCustomStartLocation(true)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.useCustomStartLocation)
        }
    }

    @Test
    fun `updateStartLocation updates location data in state`() = runTest {
        whenever(getCustomersUseCase.invoke(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(flowOf(Resource.Success(testCustomers)))

        viewModel = createViewModel()
        viewModel.updateStartLocation("Oficina Central", "4.6097", "-74.0817")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Oficina Central", state.startLocationName)
            assertEquals("4.6097", state.startLocationLatitude)
            assertEquals("-74.0817", state.startLocationLongitude)
        }
    }
}
