package com.misw.medisupply.domain.usecase.route

import com.misw.medisupply.domain.model.route.Route
import com.misw.medisupply.domain.model.route.RouteMetrics
import com.misw.medisupply.domain.model.route.RouteStatus
import com.misw.medisupply.domain.repository.RouteRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalTime

class GetSalespersonRoutesUseCaseTest {

    private lateinit var repository: RouteRepository
    private lateinit var useCase: GetSalespersonRoutesUseCase

    private val testRoutes = listOf(
        Route(
            id = 1,
            routeCode = "RT-20251117-001",
            salespersonId = 1,
            salespersonName = "Vendedor Test",
            salespersonEmployeeId = "EMP001",
            plannedDate = LocalDate.now(),
            status = RouteStatus.CONFIRMED,
            metrics = RouteMetrics(
                totalStops = 5,
                totalDistanceKm = 25.0,
                estimatedDurationMinutes = 180,
                optimizationScore = 0.92
            ),
            stops = emptyList(),
            startLocation = null,
            endLocation = null,
            createdAt = null,
            updatedAt = null
        ),
        Route(
            id = 2,
            routeCode = "RT-20251118-001",
            salespersonId = 1,
            salespersonName = "Vendedor Test",
            salespersonEmployeeId = "EMP001",
            plannedDate = LocalDate.now().plusDays(1),
            status = RouteStatus.DRAFT,
            metrics = RouteMetrics(
                totalStops = 3,
                totalDistanceKm = 15.0,
                estimatedDurationMinutes = 120,
                optimizationScore = 0.88
            ),
            stops = emptyList(),
            startLocation = null,
            endLocation = null,
            createdAt = null,
            updatedAt = null
        )
    )

    @Before
    fun setup() {
        repository = mock()
        useCase = GetSalespersonRoutesUseCase(repository)
    }

    @Test
    fun `invoke with salespersonId returns routes successfully`() = runTest {
        val salespersonId = 1

        whenever(
            repository.getSalespersonRoutes(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(testRoutes))

        val result = useCase(salespersonId = salespersonId)

        assertTrue(result.isSuccess)
        assertEquals(testRoutes, result.getOrNull())
        verify(repository).getSalespersonRoutes(
            salespersonId = salespersonId,
            date = null,
            status = null,
            page = 1,
            perPage = 10
        )
    }

    @Test
    fun `invoke with date filter returns filtered routes`() = runTest {
        val salespersonId = 1
        val date = LocalDate.now()
        val filteredRoutes = listOf(testRoutes[0])

        whenever(
            repository.getSalespersonRoutes(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(filteredRoutes))

        val result = useCase(salespersonId = salespersonId, date = date)

        assertTrue(result.isSuccess)
        assertEquals(filteredRoutes, result.getOrNull())
        verify(repository).getSalespersonRoutes(
            salespersonId = salespersonId,
            date = date,
            status = null,
            page = 1,
            perPage = 10
        )
    }

    @Test
    fun `invoke with status filter returns filtered routes`() = runTest {
        val salespersonId = 1
        val status = RouteStatus.CONFIRMED
        val filteredRoutes = listOf(testRoutes[0])

        whenever(
            repository.getSalespersonRoutes(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(filteredRoutes))

        val result = useCase(salespersonId = salespersonId, status = status)

        assertTrue(result.isSuccess)
        assertEquals(filteredRoutes, result.getOrNull())
        verify(repository).getSalespersonRoutes(
            salespersonId = salespersonId,
            date = null,
            status = status,
            page = 1,
            perPage = 10
        )
    }

    @Test
    fun `invoke with pagination parameters returns paginated routes`() = runTest {
        val salespersonId = 1
        val page = 2
        val perPage = 5

        whenever(
            repository.getSalespersonRoutes(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(testRoutes))

        val result = useCase(
            salespersonId = salespersonId,
            page = page,
            perPage = perPage
        )

        assertTrue(result.isSuccess)
        verify(repository).getSalespersonRoutes(
            salespersonId = salespersonId,
            date = null,
            status = null,
            page = page,
            perPage = perPage
        )
    }

    @Test
    fun `invoke with all filters returns filtered and paginated routes`() = runTest {
        val salespersonId = 1
        val date = LocalDate.now()
        val status = RouteStatus.CONFIRMED
        val page = 1
        val perPage = 20

        whenever(
            repository.getSalespersonRoutes(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(listOf(testRoutes[0])))

        val result = useCase(
            salespersonId = salespersonId,
            date = date,
            status = status,
            page = page,
            perPage = perPage
        )

        assertTrue(result.isSuccess)
        verify(repository).getSalespersonRoutes(
            salespersonId = salespersonId,
            date = date,
            status = status,
            page = page,
            perPage = perPage
        )
    }

    @Test
    fun `invoke with repository error returns failure`() = runTest {
        val salespersonId = 1
        val errorMessage = "Error de conexión al servidor"

        whenever(
            repository.getSalespersonRoutes(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.failure(Exception(errorMessage)))

        val result = useCase(salespersonId = salespersonId)

        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke returns empty list when no routes found`() = runTest {
        val salespersonId = 999

        whenever(
            repository.getSalespersonRoutes(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(emptyList()))

        val result = useCase(salespersonId = salespersonId)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun `invoke with default parameters uses page 1 and perPage 10`() = runTest {
        val salespersonId = 1

        whenever(
            repository.getSalespersonRoutes(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(testRoutes))

        useCase(salespersonId = salespersonId)

        verify(repository).getSalespersonRoutes(
            salespersonId = salespersonId,
            date = null,
            status = null,
            page = 1,
            perPage = 10
        )
    }
}
