package com.misw.medisupply.domain.model.customer

import org.junit.Assert.*
import org.junit.Test

class DocumentTypeTest {

    @Test
    fun `fromString returns NIT for nit`() {
        val result = DocumentType.fromString("NIT")
        
        assertEquals(DocumentType.NIT, result)
    }

    @Test
    fun `fromString returns CC for cc`() {
        val result = DocumentType.fromString("CC")
        
        assertEquals(DocumentType.CC, result)
    }

    @Test
    fun `fromString returns CE for ce`() {
        val result = DocumentType.fromString("CE")
        
        assertEquals(DocumentType.CE, result)
    }

    @Test
    fun `fromString returns RUT for rut`() {
        val result = DocumentType.fromString("RUT")
        
        assertEquals(DocumentType.RUT, result)
    }

    @Test
    fun `fromString returns DNI for dni`() {
        val result = DocumentType.fromString("DNI")
        
        assertEquals(DocumentType.DNI, result)
    }

    @Test
    fun `fromString is case insensitive`() {
        assertEquals(DocumentType.NIT, DocumentType.fromString("nit"))
        assertEquals(DocumentType.CC, DocumentType.fromString("cC"))
        assertEquals(DocumentType.RUT, DocumentType.fromString("RuT"))
    }

    @Test
    fun `fromString returns NIT for invalid value`() {
        val result = DocumentType.fromString("INVALID_DOCUMENT")
        
        assertEquals(DocumentType.NIT, result)
    }

    @Test
    fun `enum values have correct display names`() {
        assertEquals("NIT", DocumentType.NIT.displayName)
        assertEquals("Cédula de Ciudadanía", DocumentType.CC.displayName)
        assertEquals("Cédula de Extranjería", DocumentType.CE.displayName)
        assertEquals("RUT", DocumentType.RUT.displayName)
        assertEquals("DNI", DocumentType.DNI.displayName)
    }
}
