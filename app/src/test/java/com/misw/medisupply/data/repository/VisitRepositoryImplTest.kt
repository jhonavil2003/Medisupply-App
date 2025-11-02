package com.misw.medisupply.data.repository

import com.misw.medisupply.data.network.api.VisitApiService
import com.misw.medisupply.domain.model.visit.Visit
import com.misw.medisupply.domain.model.visit.VisitStatus
import com.misw.medisupply.domain.repository.VisitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime

@ExperimentalCoroutinesApi
class VisitRepositoryImplTest {

    @Mock
    private lateinit var visitApiService: VisitApiService
    private lateinit var repository: VisitRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = VisitRepositoryImpl(visitApiService)
    }

    @Test
    fun `createVisit returns success when api call succeeds`() = runTest {
        // Given
        val visit = Visit(
            id = 1,
            customerId = 123,
            salespersonId = 456,
            visitDate = LocalDate.of(2023, 12, 1),
            visitTime = LocalTime.of(10, 0),
            status = VisitStatus.PROGRAMADA,
            address = "123 Main St"
        )
        whenever(visitApiService.createVisit(any())).thenReturn(Response.success(visit))

        // When
        val result = repository.createVisit(visit)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(visit, result.getOrNull())
    }

    @Test
    fun `createVisit returns failure when api call fails`() = runTest {
        // Given
        val visit = Visit(
            id = 1,
            customerId = 123,
            salespersonId = 456,
            visitDate = LocalDate.of(2023, 12, 1),
            visitTime = LocalTime.of(10, 0),
            status = VisitStatus.PROGRAMADA,
            address = "123 Main St"
        )
        val errorResponse = Response.error<Visit>(400, okhttp3.ResponseBody.create(null, ""))
        whenever(visitApiService.createVisit(any())).thenReturn(errorResponse)

        // When
        val result = repository.createVisit(visit)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `updateVisit returns success when api call succeeds`() = runTest {
        // Given
        val visitId = 1
        val visit = Visit(
            id = visitId,
            customerId = 123,
            salespersonId = 456,
            visitDate = LocalDate.of(2023, 12, 1),
            visitTime = LocalTime.of(10, 0),
            status = VisitStatus.COMPLETADA,
            address = "123 Main St"
        )
        whenever(visitApiService.updateVisit(visitId, visit)).thenReturn(Response.success(visit))

        // When
        val result = repository.updateVisit(visitId, visit)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(visit, result.getOrNull())
    }

    @Test
    fun `completeVisit returns success when api call succeeds`() = runTest {
        // Given
        val visitId = 1
        val completedVisit = Visit(
            id = visitId,
            customerId = 123,
            salespersonId = 456,
            visitDate = LocalDate.of(2023, 12, 1),
            visitTime = LocalTime.of(10, 0),
            status = VisitStatus.COMPLETADA,
            address = "123 Main St"
        )
        whenever(visitApiService.completeVisit(visitId)).thenReturn(Response.success(completedVisit))

        // When
        val result = repository.completeVisit(visitId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(VisitStatus.COMPLETADA, result.getOrNull()?.status)
    }
}