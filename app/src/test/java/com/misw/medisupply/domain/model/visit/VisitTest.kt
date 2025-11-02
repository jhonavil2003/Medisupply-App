package com.misw.medisupply.domain.model.visit

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class VisitTest {

    @Test
    fun `visit creation with all parameters works correctly`() {
        val visit = Visit(
            id = 1,
            customerId = 100,
            salespersonId = 200,
            visitDate = LocalDate.of(2025, 11, 5),
            visitTime = LocalTime.of(14, 30),
            contactedPersons = "Dr. Juan Pérez",
            clinicalFindings = "Revisión de equipos médicos",
            additionalNotes = "Cliente interesado en nuevos productos",
            address = "Calle 123 #45-67",
            latitude = 4.60971,
            longitude = -74.08175,
            status = VisitStatus.PROGRAMADA,
            createdAt = "2025-11-01T10:30:00Z",
            updatedAt = "2025-11-01T10:30:00Z"
        )

        assertEquals(1, visit.id)
        assertEquals(100, visit.customerId)
        assertEquals(200, visit.salespersonId)
        assertEquals(LocalDate.of(2025, 11, 5), visit.visitDate)
        assertEquals(LocalTime.of(14, 30), visit.visitTime)
        assertEquals("Dr. Juan Pérez", visit.contactedPersons)
        assertEquals("Revisión de equipos médicos", visit.clinicalFindings)
        assertEquals("Cliente interesado en nuevos productos", visit.additionalNotes)
        assertEquals("Calle 123 #45-67", visit.address)
        assertEquals(4.60971, visit.latitude!!, 0.0001)
        assertEquals(-74.08175, visit.longitude!!, 0.0001)
        assertEquals(VisitStatus.PROGRAMADA, visit.status)
        assertEquals("2025-11-01T10:30:00Z", visit.createdAt)
        assertEquals("2025-11-01T10:30:00Z", visit.updatedAt)
    }

    @Test
    fun `visit creation with minimal required fields works correctly`() {
        val visit = Visit(
            customerId = 100,
            salespersonId = 200,
            visitDate = LocalDate.of(2025, 11, 5),
            visitTime = LocalTime.of(14, 30)
        )

        assertEquals(0, visit.id)
        assertEquals(100, visit.customerId)
        assertEquals(200, visit.salespersonId)
        assertEquals(LocalDate.of(2025, 11, 5), visit.visitDate)
        assertEquals(LocalTime.of(14, 30), visit.visitTime)
        assertNull(visit.contactedPersons)
        assertNull(visit.clinicalFindings)
        assertNull(visit.additionalNotes)
        assertNull(visit.address)
        assertNull(visit.latitude)
        assertNull(visit.longitude)
        assertEquals(VisitStatus.PROGRAMADA, visit.status)
        assertNull(visit.createdAt)
        assertNull(visit.updatedAt)
    }

    @Test
    fun `visit status updates correctly`() {
        val visit = Visit(
            customerId = 100,
            salespersonId = 200,
            visitDate = LocalDate.of(2025, 11, 5),
            visitTime = LocalTime.of(14, 30),
            status = VisitStatus.PROGRAMADA
        )

        val completedVisit = visit.copy(status = VisitStatus.COMPLETADA)
        val cancelledVisit = visit.copy(status = VisitStatus.ELIMINADA)

        assertEquals(VisitStatus.PROGRAMADA, visit.status)
        assertEquals(VisitStatus.COMPLETADA, completedVisit.status)
        assertEquals(VisitStatus.ELIMINADA, cancelledVisit.status)
    }

    @Test
    fun `visit with coordinates works correctly`() {
        val visit = Visit(
            customerId = 100,
            salespersonId = 200,
            visitDate = LocalDate.of(2025, 11, 5),
            visitTime = LocalTime.of(14, 30),
            latitude = 4.60971,
            longitude = -74.08175
        )

        assertNotNull(visit.latitude)
        assertNotNull(visit.longitude)
        assertEquals(4.60971, visit.latitude!!, 0.0001)
        assertEquals(-74.08175, visit.longitude!!, 0.0001)
    }

    @Test
    fun `visit without coordinates works correctly`() {
        val visit = Visit(
            customerId = 100,
            salespersonId = 200,
            visitDate = LocalDate.of(2025, 11, 5),
            visitTime = LocalTime.of(14, 30)
        )

        assertNull(visit.latitude)
        assertNull(visit.longitude)
    }

    @Test
    fun `visit creation with all status types works correctly`() {
        val programmedVisit = Visit(
            customerId = 100,
            salespersonId = 200,
            visitDate = LocalDate.of(2025, 11, 5),
            visitTime = LocalTime.of(14, 30),
            status = VisitStatus.PROGRAMADA
        )

        val completedVisit = Visit(
            customerId = 100,
            salespersonId = 200,
            visitDate = LocalDate.of(2025, 11, 5),
            visitTime = LocalTime.of(14, 30),
            status = VisitStatus.COMPLETADA
        )

        val cancelledVisit = Visit(
            customerId = 100,
            salespersonId = 200,
            visitDate = LocalDate.of(2025, 11, 5),
            visitTime = LocalTime.of(14, 30),
            status = VisitStatus.ELIMINADA
        )

        assertEquals(VisitStatus.PROGRAMADA, programmedVisit.status)
        assertEquals(VisitStatus.COMPLETADA, completedVisit.status)
        assertEquals(VisitStatus.ELIMINADA, cancelledVisit.status)
    }

    @Test
    fun `visit creation with empty optional fields works correctly`() {
        val visit = Visit(
            customerId = 100,
            salespersonId = 200,
            visitDate = LocalDate.of(2025, 11, 5),
            visitTime = LocalTime.of(14, 30),
            contactedPersons = "",
            clinicalFindings = "",
            additionalNotes = "",
            address = ""
        )

        assertEquals("", visit.contactedPersons)
        assertEquals("", visit.clinicalFindings)
        assertEquals("", visit.additionalNotes)
        assertEquals("", visit.address)
    }

    @Test
    fun `visit creation with long text fields works correctly`() {
        val longText = "Este es un texto muy largo que simula un informe clínico detallado con múltiples líneas y información extensa sobre el estado de los equipos médicos revisados durante la visita."
        
        val visit = Visit(
            customerId = 100,
            salespersonId = 200,
            visitDate = LocalDate.of(2025, 11, 5),
            visitTime = LocalTime.of(14, 30),
            contactedPersons = longText,
            clinicalFindings = longText,
            additionalNotes = longText
        )

        assertEquals(longText, visit.contactedPersons)
        assertEquals(longText, visit.clinicalFindings)
        assertEquals(longText, visit.additionalNotes)
    }

    @Test
    fun `visit copy with updates works correctly`() {
        val originalVisit = Visit(
            customerId = 100,
            salespersonId = 200,
            visitDate = LocalDate.of(2025, 11, 5),
            visitTime = LocalTime.of(14, 30)
        )

        val updatedVisit = originalVisit.copy(
            contactedPersons = "Dr. María González",
            clinicalFindings = "Equipos en buen estado",
            status = VisitStatus.COMPLETADA
        )

        assertEquals(100, updatedVisit.customerId)
        assertEquals(200, updatedVisit.salespersonId)
        assertEquals("Dr. María González", updatedVisit.contactedPersons)
        assertEquals("Equipos en buen estado", updatedVisit.clinicalFindings)
        assertEquals(VisitStatus.COMPLETADA, updatedVisit.status)
    }

    @Test
    fun `visit with extreme coordinates works correctly`() {
        // Coordenadas extremas válidas
        val northPole = Visit(
            customerId = 100,
            salespersonId = 200,
            visitDate = LocalDate.of(2025, 11, 5),
            visitTime = LocalTime.of(14, 30),
            latitude = 90.0,
            longitude = 0.0
        )

        val southPole = Visit(
            customerId = 100,
            salespersonId = 200,
            visitDate = LocalDate.of(2025, 11, 5),
            visitTime = LocalTime.of(14, 30),
            latitude = -90.0,
            longitude = 180.0
        )

        assertEquals(90.0, northPole.latitude!!, 0.0001)
        assertEquals(0.0, northPole.longitude!!, 0.0001)
        assertEquals(-90.0, southPole.latitude!!, 0.0001)
        assertEquals(180.0, southPole.longitude!!, 0.0001)
    }

    @Test
    fun `visit equality works correctly`() {
        val visit1 = Visit(
            id = 1,
            customerId = 100,
            salespersonId = 200,
            visitDate = LocalDate.of(2025, 11, 5),
            visitTime = LocalTime.of(14, 30)
        )

        val visit2 = Visit(
            id = 1,
            customerId = 100,
            salespersonId = 200,
            visitDate = LocalDate.of(2025, 11, 5),
            visitTime = LocalTime.of(14, 30)
        )

        val visit3 = Visit(
            id = 2,
            customerId = 100,
            salespersonId = 200,
            visitDate = LocalDate.of(2025, 11, 5),
            visitTime = LocalTime.of(14, 30)
        )

        assertEquals(visit1, visit2)
        assertNotEquals(visit1, visit3)
    }
}