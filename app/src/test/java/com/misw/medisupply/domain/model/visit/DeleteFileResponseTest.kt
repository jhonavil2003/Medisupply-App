package com.misw.medisupply.domain.model.visit

import org.junit.Assert.*
import org.junit.Test

class DeleteFileResponseTest {

    @Test
    fun `delete file response creation with success works correctly`() {
        val response = DeleteFileResponse(
            success = true,
            message = "Archivo eliminado exitosamente",
            deletedFileId = 1
        )

        assertTrue(response.success)
        assertEquals("Archivo eliminado exitosamente", response.message)
        assertEquals(1, response.deletedFileId)
    }

    @Test
    fun `delete file response creation with failure works correctly`() {
        val response = DeleteFileResponse(
            success = false,
            message = "Error al eliminar archivo",
            deletedFileId = null
        )

        assertFalse(response.success)
        assertEquals("Error al eliminar archivo", response.message)
        assertNull(response.deletedFileId)
    }

    @Test
    fun `delete file response with different error messages works correctly`() {
        val responses = listOf(
            DeleteFileResponse(false, "Archivo no encontrado", null),
            DeleteFileResponse(false, "Sin permisos para eliminar", null),
            DeleteFileResponse(false, "Error de servidor", null),
            DeleteFileResponse(false, "Visita no existe", null)
        )

        responses.forEach { response ->
            assertFalse(response.success)
            assertNotNull(response.message)
            assertNull(response.deletedFileId)
        }

        assertEquals("Archivo no encontrado", responses[0].message)
        assertEquals("Sin permisos para eliminar", responses[1].message)
        assertEquals("Error de servidor", responses[2].message)
        assertEquals("Visita no existe", responses[3].message)
    }

    @Test
    fun `delete file response with different file IDs works correctly`() {
        val responses = listOf(
            DeleteFileResponse(true, "Eliminado", 1),
            DeleteFileResponse(true, "Eliminado", 999),
            DeleteFileResponse(true, "Eliminado", 123456)
        )

        responses.forEach { response ->
            assertTrue(response.success)
            assertEquals("Eliminado", response.message)
            assertNotNull(response.deletedFileId)
        }

        assertEquals(1, responses[0].deletedFileId)
        assertEquals(999, responses[1].deletedFileId)
        assertEquals(123456, responses[2].deletedFileId)
    }

    @Test
    fun `delete file response equals and hashCode work correctly`() {
        val response1 = DeleteFileResponse(true, "Success", 1)
        val response2 = DeleteFileResponse(true, "Success", 1)
        val response3 = DeleteFileResponse(false, "Error", null)
        val response4 = DeleteFileResponse(true, "Success", 2)

        assertEquals(response1, response2)
        assertEquals(response1.hashCode(), response2.hashCode())
        assertNotEquals(response1, response3)
        assertNotEquals(response1.hashCode(), response3.hashCode())
        assertNotEquals(response1, response4)
        assertNotEquals(response1.hashCode(), response4.hashCode())
    }

    @Test
    fun `delete file response copy works correctly`() {
        val originalResponse = DeleteFileResponse(
            success = true,
            message = "Original message",
            deletedFileId = 1
        )

        val copiedResponse = originalResponse.copy(
            message = "Updated message",
            deletedFileId = 2
        )

        assertEquals(originalResponse.success, copiedResponse.success)
        assertEquals("Updated message", copiedResponse.message)
        assertEquals(2, copiedResponse.deletedFileId)
    }

    @Test
    fun `delete file response with empty message works correctly`() {
        val response = DeleteFileResponse(
            success = true,
            message = "",
            deletedFileId = 1
        )

        assertTrue(response.success)
        assertEquals("", response.message)
        assertEquals(1, response.deletedFileId)
    }

    @Test
    fun `delete file response success scenarios`() {
        val successCases = listOf(
            1, 10, 100, 999, 123456, 9999999
        )

        successCases.forEach { fileId ->
            val response = DeleteFileResponse(
                success = true,
                message = "Archivo $fileId eliminado",
                deletedFileId = fileId
            )

            assertTrue("File $fileId deletion should be successful", response.success)
            assertEquals(fileId, response.deletedFileId)
            assertEquals("Archivo $fileId eliminado", response.message)
        }
    }

    @Test
    fun `delete file response failure scenarios`() {
        val errorCases = listOf(
            "Archivo no encontrado",
            "Archivo en uso",
            "Error de permisos",
            "Error de red",
            "Servidor no disponible"
        )

        errorCases.forEach { errorMessage ->
            val response = DeleteFileResponse(
                success = false,
                message = errorMessage,
                deletedFileId = null
            )

            assertFalse("Response should indicate failure", response.success)
            assertNull("DeletedFileId should be null on failure", response.deletedFileId)
            assertEquals(errorMessage, response.message)
        }
    }
}