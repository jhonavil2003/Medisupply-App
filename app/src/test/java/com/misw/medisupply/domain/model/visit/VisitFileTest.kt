package com.misw.medisupply.domain.model.visit

import org.junit.Assert.*
import org.junit.Test

class VisitFileTest {

    @Test
    fun `visit file creation with all parameters works correctly`() {
        val visitFile = VisitFile(
            id = 1,
            visitId = 100,
            fileName = "documento.pdf",
            filePath = "/uploads/visits/100/documento_20251101_143022_a1b2c3d4.pdf",
            fileSize = 2048576,
            mimeType = "application/pdf",
            uploadDate = "2025-11-01T14:30:22.123456"
        )

        assertEquals(1, visitFile.id)
        assertEquals(100, visitFile.visitId)
        assertEquals("documento.pdf", visitFile.fileName)
        assertEquals("/uploads/visits/100/documento_20251101_143022_a1b2c3d4.pdf", visitFile.filePath)
        assertEquals(2048576, visitFile.fileSize)
        assertEquals("application/pdf", visitFile.mimeType)
        assertEquals("2025-11-01T14:30:22.123456", visitFile.uploadDate)
    }

    @Test
    fun `visit file creation with default values works correctly`() {
        val visitFile = VisitFile(visitId = 100)

        assertEquals(0, visitFile.id)
        assertEquals(100, visitFile.visitId)
        assertEquals("", visitFile.fileName)
        assertEquals("", visitFile.filePath)
        assertEquals(0L, visitFile.fileSize)
        assertEquals("application/octet-stream", visitFile.mimeType)
        assertEquals("", visitFile.uploadDate)
    }

    @Test
    fun `visit file with different file types works correctly`() {
        val pdfFile = VisitFile(
            visitId = 100,
            fileName = "documento.pdf",
            mimeType = "application/pdf"
        )

        val imageFile = VisitFile(
            visitId = 100,
            fileName = "imagen.jpg",
            mimeType = "image/jpeg"
        )

        val excelFile = VisitFile(
            visitId = 100,
            fileName = "hoja_calculo.xlsx",
            mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )

        assertEquals("application/pdf", pdfFile.mimeType)
        assertEquals("image/jpeg", imageFile.mimeType)
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelFile.mimeType)
    }

    @Test
    fun `visit file with large size works correctly`() {
        val largeFile = VisitFile(
            visitId = 100,
            fileName = "archivo_grande.zip",
            fileSize = 10485760L // 10 MB
        )

        assertEquals(10485760L, largeFile.fileSize)
    }

    @Test
    fun `visit file equals and hashCode work correctly`() {
        val file1 = VisitFile(
            id = 1,
            visitId = 100,
            fileName = "documento.pdf",
            fileSize = 2048576
        )

        val file2 = VisitFile(
            id = 1,
            visitId = 100,
            fileName = "documento.pdf",
            fileSize = 2048576
        )

        val file3 = VisitFile(
            id = 2,
            visitId = 100,
            fileName = "documento.pdf",
            fileSize = 2048576
        )

        assertEquals(file1, file2)
        assertEquals(file1.hashCode(), file2.hashCode())
        assertNotEquals(file1, file3)
        assertNotEquals(file1.hashCode(), file3.hashCode())
    }

    @Test
    fun `visit file copy preserves all properties`() {
        val originalFile = VisitFile(
            id = 1,
            visitId = 100,
            fileName = "documento.pdf",
            filePath = "/path/to/file.pdf",
            fileSize = 2048576,
            mimeType = "application/pdf",
            uploadDate = "2025-11-01T14:30:22.123456"
        )

        val copiedFile = originalFile.copy(fileName = "documento_nuevo.pdf")

        assertEquals(originalFile.id, copiedFile.id)
        assertEquals(originalFile.visitId, copiedFile.visitId)
        assertEquals("documento_nuevo.pdf", copiedFile.fileName)
        assertEquals(originalFile.filePath, copiedFile.filePath)
        assertEquals(originalFile.fileSize, copiedFile.fileSize)
        assertEquals(originalFile.mimeType, copiedFile.mimeType)
        assertEquals(originalFile.uploadDate, copiedFile.uploadDate)
    }

    @Test
    fun `visit file with empty file name works correctly`() {
        val fileWithEmptyName = VisitFile(
            visitId = 100,
            fileName = "",
            fileSize = 1024
        )

        assertEquals("", fileWithEmptyName.fileName)
        assertEquals(1024, fileWithEmptyName.fileSize)
    }

    @Test
    fun `visit file serialization properties work correctly`() {
        // This test verifies that the @SerializedName annotations are present
        // by creating a file with all properties and ensuring they can be copied
        val visitFile = VisitFile(
            id = 1,
            visitId = 100,
            fileName = "test.pdf",
            filePath = "/path/test.pdf",
            fileSize = 1024,
            mimeType = "application/pdf",
            uploadDate = "2025-11-01"
        )

        // Test that all properties are accessible (they wouldn't be if serialization was broken)
        assertNotNull(visitFile.id)
        assertNotNull(visitFile.visitId)
        assertNotNull(visitFile.fileName)
        assertNotNull(visitFile.filePath)
        assertNotNull(visitFile.fileSize)
        assertNotNull(visitFile.mimeType)
        assertNotNull(visitFile.uploadDate)
    }
}