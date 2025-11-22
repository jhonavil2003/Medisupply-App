package com.misw.medisupply.domain.usecase.route

import com.misw.medisupply.domain.model.route.Location
import com.misw.medisupply.domain.model.route.OptimizationStrategy
import com.misw.medisupply.domain.model.route.Route
import com.misw.medisupply.domain.model.route.RouteMetrics
import com.misw.medisupply.domain.model.route.RouteStatus
import com.misw.medisupply.domain.model.route.RouteGenerationResult
import com.misw.medisupply.domain.model.route.RouteStop
import com.misw.medisupply.domain.model.route.StopStatus
import com.misw.medisupply.domain.model.route.WorkHours
import com.misw.medisupply.domain.repository.RouteRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalTime

class GenerateRouteUseCaseTest {

    private lateinit var repository: RouteRepository
    private lateinit var useCase: GenerateRouteUseCase

    private val testRoute = Route(
        id = 1,
        routeCode = "RT-20251117-001",
        salespersonId = 1,
        salespersonName = "Vendedor Test",
        salespersonEmployeeId = "EMP001",
        plannedDate = LocalDate.now().plusDays(1),
        status = RouteStatus.DRAFT,
        metrics = RouteMetrics(
            totalStops = 1,
            totalDistanceKm = 15.5,
            estimatedDurationMinutes = 120,
            optimizationScore = 0.95
        ),
        stops = listOf(
            RouteStop(
                id = 1,
                sequenceOrder = 1,
                customerId = 101,
                customerName = "Cliente 1",
                customerDocument = "900123456-1",
                customerType = "HOSPITAL",
                address = "Calle 1",
                neighborhood = "Centro",
                city = "Bogotá",
                latitude = 4.6097,
                longitude = -74.0817,
                contactName = "Juan Pérez",
                contactPhone = "3001234567",
                contactEmail = "juan@cliente1.com",
                estimatedArrival = LocalDate.now().plusDays(1).atTime(9, 0),
                estimatedDeparture = LocalDate.now().plusDays(1).atTime(9, 30),
                serviceMinutes = 30,
                distanceFromPreviousKm = 0.0,
                travelTimeMinutes = 0,
                actualArrival = null,
                actualDeparture = null,
                notes = null
            )
        ),
        startLocation = null,
        endLocation = null,
        createdAt = null,
        updatedAt = null
    )

    @Before
    fun setup() {
        repository = mock()
        useCase = GenerateRouteUseCase(repository)
    }

    @Test
    fun `invoke with valid data returns success with route and estimated time`() = runTest {
        val customerIds = listOf(101, 102, 103)
        val plannedDate = LocalDate.now().plusDays(1)
        val estimatedTime = 120.0

        whenever(
            repository.generateRoute(
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
        ).thenReturn(Result.success(RouteGenerationResult(
            route = testRoute,
            computationTime = estimatedTime
        )))

        val result = useCase(
            salespersonId = 1,
            salespersonName = "Vendedor Test",
            employeeId = "EMP001",
            customerIds = customerIds,
            plannedDate = plannedDate
        )

        assertTrue(result.isSuccess)
        val routeResult = result.getOrNull()!!
        assertEquals(testRoute, routeResult.route)
        assertEquals(estimatedTime, routeResult.computationTime)
        verify(repository).generateRoute(
            salespersonId = 1,
            salespersonName = "Vendedor Test",
            employeeId = "EMP001",
            customerIds = customerIds,
            plannedDate = plannedDate,
            optimizationStrategy = OptimizationStrategy.MINIMIZE_DISTANCE,
            startLocation = null,
            endLocation = null,
            workHours = null,
            serviceTimePerVisitMinutes = 30
        )
    }

    @Test
    fun `invoke with empty customer list returns failure`() = runTest {
        val result = useCase(
            salespersonId = 1,
            salespersonName = "Vendedor Test",
            employeeId = "EMP001",
            customerIds = emptyList(),
            plannedDate = LocalDate.now().plusDays(1)
        )

        assertTrue(result.isFailure)
        assertEquals("Debe seleccionar al menos un cliente", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with more than 20 customers returns failure`() = runTest {
        val tooManyCustomers = (1..21).toList()

        val result = useCase(
            salespersonId = 1,
            salespersonName = "Vendedor Test",
            employeeId = "EMP001",
            customerIds = tooManyCustomers,
            plannedDate = LocalDate.now().plusDays(1)
        )

        assertTrue(result.isFailure)
        assertEquals("Máximo 20 clientes por ruta", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with past date returns failure`() = runTest {
        val pastDate = LocalDate.now().minusDays(1)

        val result = useCase(
            salespersonId = 1,
            salespersonName = "Vendedor Test",
            employeeId = "EMP001",
            customerIds = listOf(101),
            plannedDate = pastDate
        )

        assertTrue(result.isFailure)
        assertEquals("La fecha de planificación debe ser al menos mañana", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with custom optimization strategy passes it to repository`() = runTest {
        val customerIds = listOf(101, 102)
        val plannedDate = LocalDate.now().plusDays(1)
        val strategy = OptimizationStrategy.MINIMIZE_TIME

        whenever(
            repository.generateRoute(
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
        ).thenReturn(Result.success(RouteGenerationResult(route = testRoute)))

        useCase(
            salespersonId = 1,
            salespersonName = "Vendedor Test",
            employeeId = "EMP001",
            customerIds = customerIds,
            plannedDate = plannedDate,
            optimizationStrategy = strategy
        )

        verify(repository).generateRoute(
            salespersonId = 1,
            salespersonName = "Vendedor Test",
            employeeId = "EMP001",
            customerIds = customerIds,
            plannedDate = plannedDate,
            optimizationStrategy = strategy,
            startLocation = null,
            endLocation = null,
            workHours = null,
            serviceTimePerVisitMinutes = 30
        )
    }

    @Test
    fun `invoke with start and end locations passes them to repository`() = runTest {
        val customerIds = listOf(101)
        val plannedDate = LocalDate.now().plusDays(1)
        val startLocation = Location("Punto Inicio", 4.6097, -74.0817)
        val endLocation = Location("Punto Final", 4.6143, -74.0721)

        whenever(
            repository.generateRoute(
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
        ).thenReturn(Result.success(RouteGenerationResult(route = testRoute)))

        useCase(
            salespersonId = 1,
            salespersonName = "Vendedor Test",
            employeeId = "EMP001",
            customerIds = customerIds,
            plannedDate = plannedDate,
            startLocation = startLocation,
            endLocation = endLocation
        )

        verify(repository).generateRoute(
            salespersonId = any(),
            salespersonName = any(),
            employeeId = any(),
            customerIds = any(),
            plannedDate = any(),
            optimizationStrategy = any(),
            startLocation = eq(startLocation),
            endLocation = eq(endLocation),
            workHours = anyOrNull(),
            serviceTimePerVisitMinutes = any()
        )
    }

    @Test
    fun `invoke with work hours passes them to repository`() = runTest {
        val customerIds = listOf(101)
        val plannedDate = LocalDate.now().plusDays(1)
        val workHours = WorkHours(
            start = LocalTime.of(8, 0),
            end = LocalTime.of(17, 0)
        )

        whenever(
            repository.generateRoute(
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
        ).thenReturn(Result.success(RouteGenerationResult(route = testRoute)))

        useCase(
            salespersonId = 1,
            salespersonName = "Vendedor Test",
            employeeId = "EMP001",
            customerIds = customerIds,
            plannedDate = plannedDate,
            workHours = workHours
        )

        verify(repository).generateRoute(
            salespersonId = any(),
            salespersonName = any(),
            employeeId = any(),
            customerIds = any(),
            plannedDate = any(),
            optimizationStrategy = any(),
            startLocation = anyOrNull(),
            endLocation = anyOrNull(),
            workHours = eq(workHours),
            serviceTimePerVisitMinutes = any()
        )
    }

    @Test
    fun `invoke with repository error returns failure`() = runTest {
        val customerIds = listOf(101)
        val plannedDate = LocalDate.now().plusDays(1)
        val errorMessage = "Error de conexión"

        whenever(
            repository.generateRoute(
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

        val result = useCase(
            salespersonId = 1,
            salespersonName = "Vendedor Test",
            employeeId = "EMP001",
            customerIds = customerIds,
            plannedDate = plannedDate
        )

        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with custom service time passes it to repository`() = runTest {
        val customerIds = listOf(101)
        val plannedDate = LocalDate.now().plusDays(1)
        val serviceTime = 45

        whenever(
            repository.generateRoute(
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
        ).thenReturn(Result.success(RouteGenerationResult(route = testRoute)))

        useCase(
            salespersonId = 1,
            salespersonName = "Vendedor Test",
            employeeId = "EMP001",
            customerIds = customerIds,
            plannedDate = plannedDate,
            serviceTimePerVisitMinutes = serviceTime
        )

        verify(repository).generateRoute(
            salespersonId = any(),
            salespersonName = any(),
            employeeId = any(),
            customerIds = any(),
            plannedDate = any(),
            optimizationStrategy = any(),
            startLocation = anyOrNull(),
            endLocation = anyOrNull(),
            workHours = anyOrNull(),
            serviceTimePerVisitMinutes = eq(serviceTime)
        )
    }
}
