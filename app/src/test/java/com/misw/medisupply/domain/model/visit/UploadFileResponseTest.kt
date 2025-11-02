package com.misw.medisupply.domain.model.visit

import org.junit.Assert.*
import org.junit.Test

class UploadFileResponseTest {

    @Test
    fun `upload file response creation with success works correctly`() {
        val visitFile = VisitFile(
            id = 1,
            visitId = 100,
            fileName = "documento.pdf",
            filePath = "/uploads/documento.pdf",
            fileSize = 2048576L,
            mimeType = "application/pdf",
            uploadDate = "2025-11-01T14:30:22.123456"
        )

        val response = UploadFileResponse(
            success = true,
            message = "Archivo subido exitosamente",
            file = visitFile
        )

        assertTrue(response.success)
        assertEquals("Archivo subido exitosamente", response.message)
        assertNotNull(response.file)
        assertEquals(1, response.file!!.id)
        assertEquals("documento.pdf", response.file!!.fileName)
    }

    @Test
    fun `upload file response creation with failure works correctly`() {
        val response = UploadFileResponse(
            success = false,
            message = "Error al subir archivo",
            file = null
        )

        assertFalse(response.success)
        assertEquals("Error al subir archivo", response.message)
        assertNull(response.file)
    }

    @Test
    fun `upload file response with different error messages works correctly`() {
        val responses = listOf(
            UploadFileResponse(false, "Extensión no permitida", null),
            UploadFileResponse(false, "Archivo muy grande", null),
            UploadFileResponse(false, "Error de red", null),
            UploadFileResponse(false, "Visita no encontrada", null)
        )

        responses.forEach { response ->
            assertFalse(response.success)
            assertNotNull(response.message)
            assertNull(response.file)
        }

        assertEquals("Extensión no permitida", responses[0].message)
        assertEquals("Archivo muy grande", responses[1].message)
        assertEquals("Error de red", responses[2].message)
        assertEquals("Visita no encontrada", responses[3].message)
    }

    @Test
    fun `upload file response equals and hashCode work correctly`() {
        val file1 = VisitFile(id = 1, visitId = 100, fileName = "test.pdf")
        val file2 = VisitFile(id = 1, visitId = 100, fileName = "test.pdf")

        val response1 = UploadFileResponse(true, "Success", file1)
        val response2 = UploadFileResponse(true, "Success", file2)
        val response3 = UploadFileResponse(false, "Error", null)

        assertEquals(response1, response2)
        assertEquals(response1.hashCode(), response2.hashCode())
        assertNotEquals(response1, response3)
        assertNotEquals(response1.hashCode(), response3.hashCode())
    }

    @Test
    fun `upload file response copy works correctly`() {
        val originalFile = VisitFile(id = 1, visitId = 100, fileName = "original.pdf")
        val originalResponse = UploadFileResponse(
            success = true,
            message = "Original message",
            file = originalFile
        )

        val copiedResponse = originalResponse.copy(message = "Updated message")

        assertEquals(originalResponse.success, copiedResponse.success)
        assertEquals("Updated message", copiedResponse.message)
        assertEquals(originalResponse.file, copiedResponse.file)
    }

    @Test
    fun `upload file response with empty message works correctly`() {
        val response = UploadFileResponse(
            success = true,
            message = "",
            file = VisitFile(visitId = 100)
        )

        assertTrue(response.success)
        assertEquals("", response.message)
        assertNotNull(response.file)
    }
}