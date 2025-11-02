package com.misw.medisupply.domain.model.visit

import org.junit.Assert.*
import org.junit.Test

class VisitStatusTest {

    @Test
    fun `all visit status values exist`() {
        val statuses = VisitStatus.values()
        
        assertEquals(3, statuses.size)
        assertTrue(statuses.contains(VisitStatus.PROGRAMADA))
        assertTrue(statuses.contains(VisitStatus.COMPLETADA))
        assertTrue(statuses.contains(VisitStatus.ELIMINADA))
    }

    @Test
    fun `visit status names are correct`() {
        assertEquals("PROGRAMADA", VisitStatus.PROGRAMADA.name)
        assertEquals("COMPLETADA", VisitStatus.COMPLETADA.name)
        assertEquals("ELIMINADA", VisitStatus.ELIMINADA.name)
    }

    @Test
    fun `visit status valueOf works correctly`() {
        assertEquals(VisitStatus.PROGRAMADA, VisitStatus.valueOf("PROGRAMADA"))
        assertEquals(VisitStatus.COMPLETADA, VisitStatus.valueOf("COMPLETADA"))
        assertEquals(VisitStatus.ELIMINADA, VisitStatus.valueOf("ELIMINADA"))
    }

    @Test
    fun `visit status toString returns name`() {
        assertEquals("PROGRAMADA", VisitStatus.PROGRAMADA.toString())
        assertEquals("COMPLETADA", VisitStatus.COMPLETADA.toString())
        assertEquals("ELIMINADA", VisitStatus.ELIMINADA.toString())
    }

    @Test
    fun `visit status ordinals are correct`() {
        assertEquals(0, VisitStatus.PROGRAMADA.ordinal)
        assertEquals(1, VisitStatus.COMPLETADA.ordinal)
        assertEquals(2, VisitStatus.ELIMINADA.ordinal)
    }

    @Test
    fun `visit status comparison works correctly`() {
        assertTrue(VisitStatus.PROGRAMADA.ordinal < VisitStatus.COMPLETADA.ordinal)
        assertTrue(VisitStatus.COMPLETADA.ordinal < VisitStatus.ELIMINADA.ordinal)
        assertTrue(VisitStatus.PROGRAMADA.ordinal < VisitStatus.ELIMINADA.ordinal)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `valueOf with invalid status throws exception`() {
        VisitStatus.valueOf("INVALID_STATUS")
    }

    @Test
    fun `status equality works correctly`() {
        assertEquals(VisitStatus.PROGRAMADA, VisitStatus.PROGRAMADA)
        assertEquals(VisitStatus.COMPLETADA, VisitStatus.COMPLETADA)
        assertEquals(VisitStatus.ELIMINADA, VisitStatus.ELIMINADA)
        
        assertNotEquals(VisitStatus.PROGRAMADA, VisitStatus.COMPLETADA)
        assertNotEquals(VisitStatus.COMPLETADA, VisitStatus.ELIMINADA)
        assertNotEquals(VisitStatus.PROGRAMADA, VisitStatus.ELIMINADA)
    }
}