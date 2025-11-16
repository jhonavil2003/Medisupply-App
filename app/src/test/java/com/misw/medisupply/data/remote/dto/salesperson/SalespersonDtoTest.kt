package com.misw.medisupply.data.remote.dto.salesperson

import com.google.gson.Gson
import org.junit.Test
import org.junit.Assert.*

class SalespersonDtoTest {

    private val gson = Gson()

    @Test
    fun `deserialize from JSON correctly`() {
        val json = """
            {
                "id": 2,
                "employee_id": "SALES-TEST-002",
                "full_name": "Maria Gonzalez",
                "email": "maria.gonzalez@medisupply.com",
                "phone": "+57 300 9876543",
                "territory": "Bogota Sur"
            }
        """.trimIndent()

        val dto = gson.fromJson(json, SalespersonDto::class.java)

        assertEquals(2, dto.id)
        assertEquals("SALES-TEST-002", dto.employeeId)
        assertEquals("Maria Gonzalez", dto.fullName)
        assertEquals("maria.gonzalez@medisupply.com", dto.email)
        assertEquals("+57 300 9876543", dto.phone)
        assertEquals("Bogota Sur", dto.territory)
    }

    @Test
    fun `deserialize with null optional fields`() {
        val json = """
            {
                "id": 5,
                "employee_id": null,
                "full_name": "John Doe",
                "email": "john.doe@medisupply.com",
                "phone": null,
                "territory": null
            }
        """.trimIndent()

        val dto = gson.fromJson(json, SalespersonDto::class.java)

        assertEquals(5, dto.id)
        assertNull(dto.employeeId)
        assertEquals("John Doe", dto.fullName)
        assertNull(dto.phone)
        assertNull(dto.territory)
    }

    @Test
    fun `toDomain converts to domain model correctly with two names`() {
        val dto = SalespersonDto(
            id = 2,
            employeeId = "SALES-TEST-002",
            fullName = "Maria Gonzalez",
            email = "maria.gonzalez@medisupply.com",
            phone = "+57 300 9876543",
            territory = "Bogota Sur"
        )

        val domain = dto.toDomain()

        assertEquals(2, domain.id)
        assertEquals("Maria", domain.firstName)
        assertEquals("Gonzalez", domain.lastName)
        assertEquals("maria.gonzalez@medisupply.com", domain.email)
        assertEquals("+57 300 9876543", domain.phone)
        assertEquals("Bogota Sur", domain.territory)
        assertTrue(domain.isActive)
    }

    @Test
    fun `toDomain converts to domain model correctly with single name`() {
        val dto = SalespersonDto(
            id = 5,
            employeeId = "SALES-005",
            fullName = "John",
            email = "john@medisupply.com",
            phone = null,
            territory = null
        )

        val domain = dto.toDomain()

        assertEquals(5, domain.id)
        assertEquals("John", domain.firstName)
        assertEquals("", domain.lastName)
        assertEquals("john@medisupply.com", domain.email)
        assertNull(domain.phone)
        assertNull(domain.territory)
        assertTrue(domain.isActive)
    }

    @Test
    fun `toDomain converts to domain model correctly with multiple names`() {
        val dto = SalespersonDto(
            id = 10,
            employeeId = "SALES-010",
            fullName = "Juan Carlos Rodriguez Martinez",
            email = "juan.rodriguez@medisupply.com",
            phone = "+57 300 1234567",
            territory = "Medellin"
        )

        val domain = dto.toDomain()

        assertEquals(10, domain.id)
        assertEquals("Juan", domain.firstName)
        assertEquals("Carlos Rodriguez Martinez", domain.lastName)
        assertEquals("juan.rodriguez@medisupply.com", domain.email)
        assertEquals("+57 300 1234567", domain.phone)
        assertEquals("Medellin", domain.territory)
        assertTrue(domain.isActive)
    }

    @Test
    fun `serialize to JSON correctly`() {
        val dto = SalespersonDto(
            id = 2,
            employeeId = "SALES-002",
            fullName = "Maria Gonzalez",
            email = "maria.gonzalez@medisupply.com",
            phone = "+57 300 9876543",
            territory = "Bogota Sur"
        )

        val json = gson.toJson(dto)

        assertTrue(json.contains("\"id\":2"))
        assertTrue(json.contains("\"full_name\":\"Maria Gonzalez\""))
        assertTrue(json.contains("\"email\":\"maria.gonzalez@medisupply.com\""))
    }
}
