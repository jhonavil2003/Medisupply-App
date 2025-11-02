package com.misw.medisupply.domain.usecase.visit

import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.repository.VisitRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DeleteFileUseCaseTest {

    private lateinit var repository: VisitRepository
    private lateinit var useCase: DeleteFileUseCase

    @Before
    fun setup() {
        repository = mock()
        useCase = DeleteFileUseCase(repository)
    }

    @Test
    fun `invoke returns success when repository succeeds`() = runTest {
        whenever(repository.deleteFile(any())).thenReturn(Result.success(true))

        useCase.invoke(1).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            assertEquals(true, (success as Resource.Success).data)
            awaitComplete()
        }

        verify(repository).deleteFile(1)
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        val errorMessage = "Network error"
        whenever(repository.deleteFile(any())).thenReturn(Result.failure(Exception(errorMessage)))

        useCase.invoke(1).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertEquals(errorMessage, (error as Resource.Error).message)
            awaitComplete()
        }

        verify(repository).deleteFile(1)
    }

    @Test
    fun `invoke with different file IDs works correctly`() = runTest {
        whenever(repository.deleteFile(any())).thenReturn(Result.success(true))

        // Test with different file IDs
        val fileIds = listOf(1, 100, 999)
        
        fileIds.forEach { fileId ->
            useCase.invoke(fileId).test {
                val loading = awaitItem()
                val success = awaitItem()
                
                assertTrue(loading is Resource.Loading)
                assertTrue(success is Resource.Success)
                assertEquals(true, (success as Resource.Success).data)
                awaitComplete()
            }
            
            verify(repository).deleteFile(fileId)
        }
    }

    @Test
    fun `invoke handles repository exception correctly`() = runTest {
        val exception = RuntimeException("Database connection failed")
        whenever(repository.deleteFile(any())).thenReturn(Result.failure(exception))

        useCase.invoke(1).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertEquals("Database connection failed", (error as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits loading state first`() = runTest {
        whenever(repository.deleteFile(any())).thenReturn(Result.success(true))

        useCase.invoke(1).test {
            val firstEmission = awaitItem()
            assertTrue("First emission should be Loading", firstEmission is Resource.Loading)
            
            val secondEmission = awaitItem()
            assertTrue("Second emission should be Success", secondEmission is Resource.Success)
            
            awaitComplete()
        }
    }

    @Test
    fun `invoke with invalid file ID returns success when repository allows it`() = runTest {
        whenever(repository.deleteFile(any())).thenReturn(Result.success(true))

        useCase.invoke(-1).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            awaitComplete()
        }

        verify(repository).deleteFile(-1)
    }

    @Test
    fun `invoke handles file not found error correctly`() = runTest {
        val fileNotFoundError = "File not found"
        whenever(repository.deleteFile(any())).thenReturn(Result.failure(Exception(fileNotFoundError)))

        useCase.invoke(999).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertEquals(fileNotFoundError, (error as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke handles permission error correctly`() = runTest {
        val permissionError = "Permission denied"
        whenever(repository.deleteFile(any())).thenReturn(Result.failure(Exception(permissionError)))

        useCase.invoke(1).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertEquals(permissionError, (error as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke multiple calls work independently`() = runTest {
        whenever(repository.deleteFile(1)).thenReturn(Result.success(true))
        whenever(repository.deleteFile(2)).thenReturn(Result.failure(Exception("File 2 error")))

        // First call - success
        useCase.invoke(1).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            awaitComplete()
        }

        // Second call - error
        useCase.invoke(2).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertEquals("File 2 error", (error as Resource.Error).message)
            awaitComplete()
        }

        verify(repository).deleteFile(1)
        verify(repository).deleteFile(2)
    }

    @Test
    fun `invoke with zero file ID works correctly`() = runTest {
        whenever(repository.deleteFile(0)).thenReturn(Result.success(true))

        useCase.invoke(0).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            assertEquals(true, (success as Resource.Success).data)
            awaitComplete()
        }

        verify(repository).deleteFile(0)
    }

    @Test
    fun `invoke handles network timeout error correctly`() = runTest {
        val timeoutError = "Network timeout"
        whenever(repository.deleteFile(any())).thenReturn(Result.failure(Exception(timeoutError)))

        useCase.invoke(1).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertEquals(timeoutError, (error as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke handles server error correctly`() = runTest {
        val serverError = "Internal server error"
        whenever(repository.deleteFile(any())).thenReturn(Result.failure(Exception(serverError)))

        useCase.invoke(1).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertEquals(serverError, (error as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with large file ID works correctly`() = runTest {
        val largeFileId = Int.MAX_VALUE
        whenever(repository.deleteFile(largeFileId)).thenReturn(Result.success(true))

        useCase.invoke(largeFileId).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            assertEquals(true, (success as Resource.Success).data)
            awaitComplete()
        }

        verify(repository).deleteFile(largeFileId)
    }

    @Test
    fun `invoke handles concurrent deletions correctly`() = runTest {
        whenever(repository.deleteFile(any())).thenReturn(Result.success(true))

        val fileIds = (1..5).toList()
        
        fileIds.forEach { fileId ->
            useCase.invoke(fileId).test {
                val loading = awaitItem()
                val success = awaitItem()
                
                assertTrue(loading is Resource.Loading)
                assertTrue(success is Resource.Success)
                awaitComplete()
            }
        }

        fileIds.forEach { fileId ->
            verify(repository).deleteFile(fileId)
        }
    }
}