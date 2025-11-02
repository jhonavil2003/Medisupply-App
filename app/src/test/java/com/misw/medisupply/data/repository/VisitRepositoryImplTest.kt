package com.misw.medisupply.data.repository

import com.misw.medisupply.data.network.api.VisitApiService
import com.misw.medisupply.data.network.dto.visit.CreateVisitApiResponse
import com.misw.medisupply.data.network.dto.visit.CreateVisitRequest
import com.misw.medisupply.data.network.dto.visit.CreateVisitResponse
import com.misw.medisupply.data.network.dto.visit.UpdateVisitRequest
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
import org.mockito.kotlin.eq
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
        
        val createVisitResponse = CreateVisitResponse(
            id = 1,
            customerId = 123,
            salespersonId = 456,
            visitDate = "2023-12-01",
            visitTime = "10:00:00",
            contactedPersons = null,
            clinicalFindings = null,
            additionalNotes = null,
            address = "123 Main St",
            latitude = null,
            longitude = null,
            createdAt = "2023-12-01T10:00:00Z",
            updatedAt = "2023-12-01T10:00:00Z",
            status = "PROGRAMADA"
        )
        
        val apiResponse = CreateVisitApiResponse(
            message = "Visit created successfully",
            visit = createVisitResponse
        )
        
        whenever(visitApiService.createVisit(any<CreateVisitRequest>())).thenReturn(Response.success(apiResponse))

        // When
        val result = repository.createVisit(visit)

        // Then
        assertTrue(result.isSuccess)
        val resultVisit = result.getOrNull()
        assertEquals(1, resultVisit?.id)
        assertEquals(123, resultVisit?.customerId)
        assertEquals(456, resultVisit?.salespersonId)
        assertEquals(VisitStatus.PROGRAMADA, resultVisit?.status)
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
        val errorResponse = Response.error<CreateVisitApiResponse>(400, okhttp3.ResponseBody.create(null, ""))
        whenever(visitApiService.createVisit(any<CreateVisitRequest>())).thenReturn(errorResponse)

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
        
        val updateVisitResponse = CreateVisitResponse(
            id = visitId,
            customerId = 123,
            salespersonId = 456,
            visitDate = "2023-12-01",
            visitTime = "10:00",
            contactedPersons = null,
            clinicalFindings = null,
            additionalNotes = null,
            address = "123 Main St",
            latitude = null,
            longitude = null,
            createdAt = "2023-12-01T10:00:00Z",
            updatedAt = "2023-12-01T10:00:00Z",
            status = "COMPLETADA"
        )
        
        val apiResponse = CreateVisitApiResponse(
            message = "Visit updated successfully",
            visit = updateVisitResponse
        )
        
        whenever(visitApiService.updateVisit(eq(visitId), any<UpdateVisitRequest>())).thenReturn(Response.success(apiResponse))

        // When
        val result = repository.updateVisit(visitId, visit)

        // Then
        assertTrue(result.isSuccess)
        val resultVisit = result.getOrNull()
        assertEquals(visitId, resultVisit?.id)
        assertEquals(123, resultVisit?.customerId)
        assertEquals(VisitStatus.COMPLETADA, resultVisit?.status)
    }

    @Test
    fun `completeVisit returns success when api call succeeds`() = runTest {
        // Given
        val visitId = 1
        val completedVisitResponse = CreateVisitResponse(
            id = visitId,
            customerId = 123,
            salespersonId = 456,
            visitDate = "2023-12-01",
            visitTime = "10:00",
            contactedPersons = null,
            clinicalFindings = null,
            additionalNotes = null,
            address = "123 Main St",
            latitude = null,
            longitude = null,
            createdAt = "2023-12-01T10:00:00Z",
            updatedAt = "2023-12-01T10:00:00Z",
            status = "COMPLETADA"
        )
        
        val apiResponse = CreateVisitApiResponse(
            message = "Visit completed successfully",
            visit = completedVisitResponse
        )
        
        whenever(visitApiService.completeVisit(visitId)).thenReturn(Response.success(apiResponse))

        // When
        val result = repository.completeVisit(visitId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(VisitStatus.COMPLETADA, result.getOrNull()?.status)
    }
}