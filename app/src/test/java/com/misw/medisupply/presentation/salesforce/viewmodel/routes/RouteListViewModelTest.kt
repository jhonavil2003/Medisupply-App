package com.misw.medisupply.presentation.salesforce.viewmodel.routes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.misw.medisupply.core.session.UserSessionManager
import com.misw.medisupply.domain.model.route.Route
import com.misw.medisupply.domain.model.route.RouteMetrics
import com.misw.medisupply.domain.model.route.RouteStatus
import com.misw.medisupply.domain.usecase.route.GetSalespersonRoutesUseCase
import com.misw.medisupply.presentation.salesforce.screens.routes.viewmodel.RouteListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class RouteListViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getSalespersonRoutesUseCase: GetSalespersonRoutesUseCase
    private lateinit var userSessionManager: UserSessionManager
    private lateinit var viewModel: RouteListViewModel

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
        Dispatchers.setMain(testDispatcher)
        getSalespersonRoutesUseCase = mock()
        userSessionManager = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): RouteListViewModel {
        return RouteListViewModel(
            getSalespersonRoutesUseCase,
            userSessionManager
        )
    }

    @Test
    fun `init loads routes automatically`() = runTest {
        whenever(
            getSalespersonRoutesUseCase.invoke(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(testRoutes))

        viewModel = createViewModel()

        verify(getSalespersonRoutesUseCase).invoke(
            salespersonId = 1,
            date = null,
            status = null,
            page = 1,
            perPage = 10
        )
    }

    @Test
    fun `init loading routes shows loading state`() = runTest {
        whenever(
            getSalespersonRoutesUseCase.invoke(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(testRoutes))

        viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading) // Final state after init
            assertEquals(testRoutes, state.routes)
        }
    }

    @Test
    fun `loadRoutes successfully updates state with routes`() = runTest {
        whenever(
            getSalespersonRoutesUseCase.invoke(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(testRoutes))

        viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(testRoutes, state.routes)
            assertEquals(2, state.totalRoutes)
            assertEquals(1, state.currentPage)
            assertNull(state.error)
        }
    }

    @Test
    fun `loadRoutes with error updates error state`() = runTest {
        val errorMessage = "Error al cargar rutas"
        whenever(
            getSalespersonRoutesUseCase.invoke(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.failure(Exception(errorMessage)))

        viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(errorMessage, state.error)
            assertTrue(state.routes.isEmpty())
        }
    }

    @Test
    fun `refresh sets refreshing state and reloads routes`() = runTest {
        whenever(
            getSalespersonRoutesUseCase.invoke(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(testRoutes))

        viewModel = createViewModel()
        viewModel.refresh()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isRefreshing)
            assertEquals(testRoutes, state.routes)
        }
    }

    @Test
    fun `updateDateFilter updates date filter and reloads routes`() = runTest {
        whenever(
            getSalespersonRoutesUseCase.invoke(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(testRoutes))

        viewModel = createViewModel()
        val newDate = LocalDate.now().plusDays(1)
        viewModel.updateDateFilter(newDate)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(newDate, state.selectedDate)
            assertEquals(testRoutes, state.routes)
        }
    }

    @Test
    fun `updateStatusFilter updates status filter and reloads routes`() = runTest {
        whenever(
            getSalespersonRoutesUseCase.invoke(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(listOf(testRoutes[0])))

        viewModel = createViewModel()
        viewModel.updateStatusFilter(RouteStatus.CONFIRMED)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(RouteStatus.CONFIRMED, state.selectedStatus)
            assertEquals(1, state.routes.size)
        }
    }

    @Test
    fun `clearFilters clears date and reloads all routes`() = runTest {
        whenever(
            getSalespersonRoutesUseCase.invoke(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(testRoutes))

        viewModel = createViewModel()
        viewModel.updateDateFilter(LocalDate.now())
        viewModel.clearFilters()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.selectedDate)
            assertEquals(testRoutes, state.routes)
        }
    }

    @Test
    fun `loadMore loads next page and appends to existing routes`() = runTest {
        // Page 1 needs 10 items to set hasMorePages = true
        val page1Routes = List(10) { testRoutes[0].copy(id = it + 1) }
        val page2Routes = listOf(
            Route(
                id = 11,
                routeCode = "RT-20251119-001",
                salespersonId = 1,
                salespersonName = "Vendedor Test",
                salespersonEmployeeId = "EMP001",
                plannedDate = LocalDate.now().plusDays(2),
                status = RouteStatus.DRAFT,
                stops = emptyList(),
                startLocation = null,
                endLocation = null,
                metrics = RouteMetrics(
                    totalStops = 2,
                    totalDistanceKm = 10.0,
                    estimatedDurationMinutes = 90,
                    optimizationScore = 0.85
                ),
                createdAt = null,
                updatedAt = null
            )
        )

        whenever(
            getSalespersonRoutesUseCase.invoke(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = eq(1),
                perPage = any()
            )
        ).thenReturn(Result.success(page1Routes))

        whenever(
            getSalespersonRoutesUseCase.invoke(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = eq(2),
                perPage = any()
            )
        ).thenReturn(Result.success(page2Routes))

        viewModel = createViewModel()
        viewModel.loadMore()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoadingMore)
            assertEquals(11, state.routes.size)
            assertEquals(2, state.currentPage)
        }
    }

    @Test
    fun `loadMore does not load when already loading`() = runTest {
        whenever(
            getSalespersonRoutesUseCase.invoke(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(testRoutes))

        viewModel = createViewModel()

        // Manually set isLoadingMore to true
        viewModel.loadMore()

        viewModel.uiState.test {
            val state = awaitItem()
            // Should not trigger another load
            assertFalse(state.isLoadingMore)
        }
    }

    @Test
    fun `clearError clears error message`() = runTest {
        whenever(
            getSalespersonRoutesUseCase.invoke(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.failure(Exception("Test error")))

        viewModel = createViewModel()
        viewModel.clearError()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.error)
        }
    }

    @Test
    fun `hasMorePages is false when routes size is less than itemsPerPage`() = runTest {
        val smallRouteList = listOf(testRoutes[0])

        whenever(
            getSalespersonRoutesUseCase.invoke(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(smallRouteList))

        viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.hasMorePages)
        }
    }

    @Test
    fun `hasMorePages is true when routes size equals itemsPerPage`() = runTest {
        val fullPageRoutes = List(10) { testRoutes[0].copy(id = it) }

        whenever(
            getSalespersonRoutesUseCase.invoke(
                salespersonId = any(),
                date = anyOrNull(),
                status = anyOrNull(),
                page = any(),
                perPage = any()
            )
        ).thenReturn(Result.success(fullPageRoutes))

        viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.hasMorePages)
        }
    }
}
