package com.misw.medisupply.domain.usecase.visit

import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.visit.VisitFile
import com.misw.medisupply.domain.repository.VisitRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetVisitFilesUseCaseTest {

    private lateinit var repository: VisitRepository
    private lateinit var useCase: GetVisitFilesUseCase

    private val testVisitFiles = listOf(
        VisitFile(
            id = 1,
            visitId = 100,
            fileName = "documento1.pdf",
            filePath = "/uploads/visits/100/documento1.pdf",
            fileSize = 2048576L,
            mimeType = "application/pdf",
            uploadDate = "2025-11-01T14:30:22.123456"
        ),
        VisitFile(
            id = 2,
            visitId = 100,
            fileName = "imagen.jpg",
            filePath = "/uploads/visits/100/imagen.jpg",
            fileSize = 1024000L,
            mimeType = "image/jpeg",
            uploadDate = "2025-11-01T15:45:33.789012"
        ),
        VisitFile(
            id = 3,
            visitId = 100,
            fileName = "hoja_calculo.xlsx",
            filePath = "/uploads/visits/100/hoja_calculo.xlsx",
            fileSize = 512000L,
            mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            uploadDate = "2025-11-01T16:20:15.456789"
        )
    )

    @Before
    fun setup() {
        repository = mock()
        useCase = GetVisitFilesUseCase(repository)
    }

    @Test
    fun `invoke returns success with file list when repository succeeds`() = runTest {
        whenever(repository.getVisitFiles(any())).thenReturn(Result.success(testVisitFiles))

        useCase.invoke(100).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            assertEquals(testVisitFiles, (success as Resource.Success).data)
            assertEquals(3, success.data!!.size)
            awaitComplete()
        }

        verify(repository).getVisitFiles(100)
    }

    @Test
    fun `invoke returns success with empty list when visit has no files`() = runTest {
        whenever(repository.getVisitFiles(any())).thenReturn(Result.success(emptyList()))

        useCase.invoke(100).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            assertTrue((success as Resource.Success).data!!.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        val errorMessage = "Error al obtener archivos"
        whenever(repository.getVisitFiles(any())).thenReturn(Result.failure(Exception(errorMessage)))

        useCase.invoke(100).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertEquals(errorMessage, (error as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits loading state first`() = runTest {
        whenever(repository.getVisitFiles(any())).thenReturn(Result.success(testVisitFiles))

        useCase.invoke(100).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with different visit IDs works correctly`() = runTest {
        whenever(repository.getVisitFiles(any())).thenReturn(Result.success(testVisitFiles))

        val visitIds = listOf(1, 100, 999, 123456)
        
        visitIds.forEach { visitId ->
            useCase.invoke(visitId).test {
                val loading = awaitItem()
                val success = awaitItem()
                
                assertTrue("Visit ID $visitId should work", loading is Resource.Loading)
                assertTrue("Visit ID $visitId should work", success is Resource.Success)
                awaitComplete()
            }
            
            verify(repository).getVisitFiles(visitId)
        }
    }

    @Test
    fun `invoke handles network error correctly`() = runTest {
        val networkError = "Error de conexión"
        whenever(repository.getVisitFiles(any())).thenReturn(Result.failure(Exception(networkError)))

        useCase.invoke(100).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertEquals(networkError, (error as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke handles visit not found error correctly`() = runTest {
        val notFoundError = "Visita no encontrada"
        whenever(repository.getVisitFiles(any())).thenReturn(Result.failure(Exception(notFoundError)))

        useCase.invoke(999).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertEquals(notFoundError, (error as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns files sorted by upload date`() = runTest {
        whenever(repository.getVisitFiles(any())).thenReturn(Result.success(testVisitFiles))

        useCase.invoke(100).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            
            val files = (success as Resource.Success).data!!
            assertEquals(3, files.size)
            assertEquals("documento1.pdf", files[0].fileName)
            assertEquals("imagen.jpg", files[1].fileName)
            assertEquals("hoja_calculo.xlsx", files[2].fileName)
            awaitComplete()
        }
    }

    @Test
    fun `invoke verifies file properties are correctly returned`() = runTest {
        whenever(repository.getVisitFiles(any())).thenReturn(Result.success(testVisitFiles))

        useCase.invoke(100).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            
            val files = (success as Resource.Success).data!!
            val pdfFile = files[0]
            val imageFile = files[1]
            val excelFile = files[2]
            
            // Verify PDF file
            assertEquals(1, pdfFile.id)
            assertEquals(100, pdfFile.visitId)
            assertEquals("documento1.pdf", pdfFile.fileName)
            assertEquals("application/pdf", pdfFile.mimeType)
            assertEquals(2048576L, pdfFile.fileSize)
            
            // Verify image file
            assertEquals(2, imageFile.id)
            assertEquals("imagen.jpg", imageFile.fileName)
            assertEquals("image/jpeg", imageFile.mimeType)
            assertEquals(1024000L, imageFile.fileSize)
            
            // Verify Excel file
            assertEquals(3, excelFile.id)
            assertEquals("hoja_calculo.xlsx", excelFile.fileName)
            assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelFile.mimeType)
            assertEquals(512000L, excelFile.fileSize)
            
            awaitComplete()
        }
    }

    @Test
    fun `invoke handles single file result correctly`() = runTest {
        val singleFile = listOf(testVisitFiles[0])
        whenever(repository.getVisitFiles(any())).thenReturn(Result.success(singleFile))

        useCase.invoke(100).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            assertEquals(1, (success as Resource.Success).data!!.size)
            assertEquals("documento1.pdf", success.data!![0].fileName)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with zero visit ID works correctly`() = runTest {
        whenever(repository.getVisitFiles(any())).thenReturn(Result.success(emptyList()))

        useCase.invoke(0).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            awaitComplete()
        }

        verify(repository).getVisitFiles(0)
    }

    @Test
    fun `invoke with negative visit ID works correctly`() = runTest {
        val errorMessage = "ID de visita inválido"
        whenever(repository.getVisitFiles(any())).thenReturn(Result.failure(Exception(errorMessage)))

        useCase.invoke(-1).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertEquals(errorMessage, (error as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke handles timeout error correctly`() = runTest {
        val timeoutError = "Timeout al obtener archivos"
        whenever(repository.getVisitFiles(any())).thenReturn(Result.failure(Exception(timeoutError)))

        useCase.invoke(100).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertEquals(timeoutError, (error as Resource.Error).message)
            awaitComplete()
        }
    }
}