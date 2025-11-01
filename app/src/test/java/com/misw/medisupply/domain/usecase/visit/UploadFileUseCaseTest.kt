package com.misw.medisupply.domain.usecase.visit

import app.cash.turbine.test
import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.visit.VisitFile
import com.misw.medisupply.domain.repository.VisitRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.File

class UploadFileUseCaseTest {

    private lateinit var repository: VisitRepository
    private lateinit var useCase: UploadFileUseCase
    private lateinit var tempFolder: TemporaryFolder

    private val testVisitFile = VisitFile(
        id = 1,
        visitId = 100,
        fileName = "documento.pdf",
        filePath = "/uploads/visits/100/documento_20251101_143022_a1b2c3d4.pdf",
        fileSize = 2048576L,
        mimeType = "application/pdf",
        uploadDate = "2025-11-01T14:30:22.123456"
    )

    @Before
    fun setup() {
        repository = mock()
        useCase = UploadFileUseCase(repository)
        tempFolder = TemporaryFolder()
        tempFolder.create()
    }

    @Test
    fun `invoke with valid PDF file returns success when repository succeeds`() = runTest {
        val testFile = createTestFile("documento.pdf", "PDF content")
        whenever(repository.uploadFile(any(), any(), anyOrNull())).thenReturn(Result.success(testVisitFile))

        useCase.invoke(100, testFile).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            assertEquals(testVisitFile, (success as Resource.Success).data)
            awaitComplete()
        }

        verify(repository).uploadFile(100, testFile, null)
    }

    @Test
    fun `invoke with original file name passes name to repository`() = runTest {
        val testFile = createTestFile("temp_file.pdf", "PDF content")
        val originalFileName = "documento_original.pdf"
        whenever(repository.uploadFile(any(), any(), anyOrNull())).thenReturn(Result.success(testVisitFile))

        useCase.invoke(100, testFile, originalFileName).test {
            awaitItem() // loading
            awaitItem() // success
            awaitComplete()
        }

        verify(repository).uploadFile(100, testFile, originalFileName)
    }

    @Test
    fun `invoke returns error when file does not exist`() = runTest {
        val nonExistentFile = File("/non/existent/file.pdf")

        useCase.invoke(100, nonExistentFile).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertEquals("El archivo no existe", (error as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error when file exceeds size limit`() = runTest {
        val largeFile = createTestFile("large.pdf", "x".repeat(11 * 1024 * 1024)) // 11 MB

        useCase.invoke(100, largeFile).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertEquals("El archivo no puede superar 10MB", (error as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error when file name is null or blank`() = runTest {
        val validFile = createTestFile("temp_file", "content")

        // Usar originalFileName como string vacío para probar la validación
        useCase.invoke(100, validFile, "").test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertEquals("El archivo no tiene un nombre válido", (error as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error when file extension is not allowed`() = runTest {
        val executableFile = createTestFile("malware.exe", "executable content")

        useCase.invoke(100, executableFile).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertTrue((error as Resource.Error).message!!.contains("Extensión no permitida"))
            assertTrue(error.message!!.contains("PDF, DOC, DOCX, TXT, JPG, PNG, XLS, XLSX, ZIP"))
            awaitComplete()
        }
    }

    @Test
    fun `invoke with original file name validates original name extension`() = runTest {
        val tempFile = createTestFile("temp_123.tmp", "content")
        val originalFileName = "document.exe"

        useCase.invoke(100, tempFile, originalFileName).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertTrue((error as Resource.Error).message!!.contains("Extensión no permitida"))
            awaitComplete()
        }
    }

    @Test
    fun `invoke with valid image file returns success`() = runTest {
        val imageFile = createTestFile("imagen.jpg", "JPEG image content")
        whenever(repository.uploadFile(any(), any(), anyOrNull())).thenReturn(Result.success(testVisitFile))

        useCase.invoke(100, imageFile).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with valid Excel file returns success`() = runTest {
        val excelFile = createTestFile("spreadsheet.xlsx", "Excel content")
        whenever(repository.uploadFile(any(), any(), anyOrNull())).thenReturn(Result.success(testVisitFile))

        useCase.invoke(100, excelFile).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            awaitComplete()
        }
    }

    @Test
    fun `invoke handles repository failure correctly`() = runTest {
        val testFile = createTestFile("documento.pdf", "PDF content")
        val errorMessage = "Error de red"
        whenever(repository.uploadFile(any(), any(), anyOrNull())).thenReturn(Result.failure(Exception(errorMessage)))

        useCase.invoke(100, testFile).test {
            val loading = awaitItem()
            val error = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(error is Resource.Error)
            assertEquals(errorMessage, (error as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with maximum allowed file size succeeds`() = runTest {
        val maxSizeFile = createTestFile("max_size.pdf", "x".repeat(10 * 1024 * 1024)) // Exactly 10 MB
        whenever(repository.uploadFile(any(), any(), anyOrNull())).thenReturn(Result.success(testVisitFile))

        useCase.invoke(100, maxSizeFile).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with all allowed extensions works correctly`() = runTest {
        val allowedExtensions = listOf("pdf", "doc", "docx", "txt", "rtf", "jpg", "jpeg", "png", "gif", "bmp", "xlsx", "xls", "csv", "zip", "rar")
        
        whenever(repository.uploadFile(any(), any(), anyOrNull())).thenReturn(Result.success(testVisitFile))

        allowedExtensions.forEach { ext ->
            val testFile = createTestFile("test.$ext", "content")
            
            useCase.invoke(100, testFile).test {
                val loading = awaitItem()
                val success = awaitItem()
                
                assertTrue("Extension $ext should be allowed", loading is Resource.Loading)
                assertTrue("Extension $ext should be allowed", success is Resource.Success)
                awaitComplete()
            }
        }
    }

    @Test
    fun `invoke with case insensitive extensions works correctly`() = runTest {
        val testCases = listOf("PDF", "Doc", "JPEG", "PNG", "XLSX")
        whenever(repository.uploadFile(any(), any(), anyOrNull())).thenReturn(Result.success(testVisitFile))

        testCases.forEach { ext ->
            val testFile = createTestFile("test.$ext", "content")
            
            useCase.invoke(100, testFile).test {
                val loading = awaitItem()
                val success = awaitItem()
                
                assertTrue("Extension $ext should be allowed (case insensitive)", loading is Resource.Loading)
                assertTrue("Extension $ext should be allowed (case insensitive)", success is Resource.Success)
                awaitComplete()
            }
        }
    }

    @Test
    fun `invoke with empty file succeeds if extension is valid`() = runTest {
        val emptyFile = createTestFile("empty.pdf", "")
        whenever(repository.uploadFile(any(), any(), anyOrNull())).thenReturn(Result.success(testVisitFile))

        useCase.invoke(100, emptyFile).test {
            val loading = awaitItem()
            val success = awaitItem()
            
            assertTrue(loading is Resource.Loading)
            assertTrue(success is Resource.Success)
            awaitComplete()
        }
    }

    private fun createTestFile(fileName: String, content: String): File {
        val file = File(tempFolder.root, fileName)
        file.writeText(content)
        return file
    }
}