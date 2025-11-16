package com.misw.medisupply.data.remote.dto.customer

import com.google.gson.Gson
import org.junit.Test
import org.junit.Assert.*

class CustomerDtoTest {

    private val gson = Gson()

    @Test
    fun `deserialize from JSON correctly`() {
        val json = """
            {
                "id": 1,
                "document_type": "NIT",
                "document_number": "900123456-7",
                "business_name": "Hospital San José",
                "trade_name": "HSJ",
                "customer_type": "HOSPITAL",
                "contact_name": "Juan Pérez",
                "contact_email": "juan@hsj.com",
                "contact_phone": "3001234567",
                "address": "Calle 123",
                "city": "Bogotá",
                "department": "Cundinamarca",
                "country": "Colombia",
                "credit_limit": 50000000.0,
                "credit_days": 30,
                "is_active": true,
                "created_at": "2024-01-01T00:00:00Z",
                "updated_at": "2024-01-01T00:00:00Z",
                "salesperson_id": null,
                "salesperson": null
            }
        """.trimIndent()

        val dto = gson.fromJson(json, CustomerDto::class.java)

        assertEquals(1, dto.id)
        assertEquals("NIT", dto.documentType)
        assertEquals("900123456-7", dto.documentNumber)
        assertEquals("Hospital San José", dto.businessName)
        assertEquals("HSJ", dto.tradeName)
        assertEquals("HOSPITAL", dto.customerType)
        assertEquals("Juan Pérez", dto.contactName)
        assertEquals("juan@hsj.com", dto.contactEmail)
        assertEquals(50000000.0, dto.creditLimit, 0.01)
        assertEquals(30, dto.creditDays)
        assertTrue(dto.isActive)
        assertNull(dto.salespersonId)
        assertNull(dto.salesperson)
    }

    @Test
    fun `deserialize with null optional fields`() {
        val json = """
            {
                "id": 2,
                "document_type": "NIT",
                "document_number": "800456789-0",
                "business_name": "Clínica Norte",
                "trade_name": null,
                "customer_type": "CLINIC",
                "contact_name": null,
                "contact_email": null,
                "contact_phone": null,
                "address": null,
                "city": null,
                "department": null,
                "country": "Colombia",
                "credit_limit": 20000000.0,
                "credit_days": 15,
                "is_active": true,
                "created_at": null,
                "updated_at": null,
                "salesperson_id": null,
                "salesperson": null
            }
        """.trimIndent()

        val dto = gson.fromJson(json, CustomerDto::class.java)

        assertNull(dto.tradeName)
        assertNull(dto.contactName)
        assertNull(dto.contactEmail)
        assertNull(dto.address)
        assertNull(dto.createdAt)
        assertNull(dto.salespersonId)
        assertNull(dto.salesperson)
    }

    @Test
    fun `toDomain converts to domain model correctly`() {
        val dto = CustomerDto(
            id = 1,
            documentType = "NIT",
            documentNumber = "900123456-7",
            businessName = "Hospital Test",
            tradeName = "HT",
            customerType = "HOSPITAL",
            contactName = "Test Contact",
            contactEmail = "test@hospital.com",
            contactPhone = "3001234567",
            address = "Address 123",
            city = "Bogotá",
            department = "Cundinamarca",
            country = "Colombia",
            creditLimit = 10000000.0,
            creditDays = 30,
            isActive = true,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            salespersonId = null,
            salesperson = null
        )

        val domain = dto.toDomain()

        assertEquals(1, domain.id)
        assertEquals("900123456-7", domain.documentNumber)
        assertEquals("Hospital Test", domain.businessName)
        assertEquals(10000000.0, domain.creditLimit, 0.01)
        assertEquals(30, domain.creditDays)
        assertTrue(domain.isActive)
        assertNull(domain.salespersonId)
        assertNull(domain.salesperson)
    }

    @Test
    fun `serialize to JSON correctly`() {
        val dto = CustomerDto(
            id = 1,
            documentType = "NIT",
            documentNumber = "900123456-7",
            businessName = "Hospital Test",
            tradeName = null,
            customerType = "HOSPITAL",
            contactName = null,
            contactEmail = null,
            contactPhone = null,
            address = null,
            city = null,
            department = null,
            country = "Colombia",
            creditLimit = 10000000.0,
            creditDays = 30,
            isActive = true,
            createdAt = null,
            updatedAt = null,
            salespersonId = null,
            salesperson = null
        )

        val json = gson.toJson(dto)

        assertTrue(json.contains("\"id\":1"))
        assertTrue(json.contains("\"document_number\":\"900123456-7\""))
        assertTrue(json.contains("\"business_name\":\"Hospital Test\""))
    }

    @Test
    fun `deserialize customer with salesperson correctly`() {
        val json = """
            {
                "id": 1,
                "document_type": "NIT",
                "document_number": "900123456-1",
                "business_name": "Farmacia San Rafael",
                "trade_name": "Farmacia SR",
                "customer_type": "FARMACIA",
                "contact_name": "Pedro López",
                "contact_email": "pedro@farmaciasr.com",
                "contact_phone": "3001234567",
                "address": "Carrera 10 #20-30",
                "city": "Bogotá",
                "department": "Cundinamarca",
                "country": "Colombia",
                "credit_limit": 5000000.0,
                "credit_days": 15,
                "is_active": true,
                "created_at": "2024-01-01T00:00:00Z",
                "updated_at": "2024-01-01T00:00:00Z",
                "salesperson_id": 2,
                "salesperson": {
                    "id": 2,
                    "employee_id": "SALES-TEST-002",
                    "full_name": "Maria Gonzalez",
                    "email": "maria.gonzalez@medisupply.com",
                    "phone": "+57 300 9876543",
                    "territory": "Bogota Sur"
                }
            }
        """.trimIndent()

        val dto = gson.fromJson(json, CustomerDto::class.java)

        assertEquals(1, dto.id)
        assertEquals("Farmacia San Rafael", dto.businessName)
        assertEquals(2, dto.salespersonId)
        assertNotNull(dto.salesperson)
        assertEquals(2, dto.salesperson?.id)
        assertEquals("Maria Gonzalez", dto.salesperson?.fullName)
        assertEquals("maria.gonzalez@medisupply.com", dto.salesperson?.email)
    }

    @Test
    fun `toDomain with salesperson converts correctly`() {
        val salespersonDto = com.misw.medisupply.data.remote.dto.salesperson.SalespersonDto(
            id = 2,
            employeeId = "SALES-002",
            fullName = "Maria Gonzalez",
            email = "maria.gonzalez@medisupply.com",
            phone = "+57 300 9876543",
            territory = "Bogota Sur"
        )

        val dto = CustomerDto(
            id = 1,
            documentType = "NIT",
            documentNumber = "900123456-1",
            businessName = "Farmacia Test",
            tradeName = "FT",
            customerType = "FARMACIA",
            contactName = "Test Contact",
            contactEmail = "test@farmacia.com",
            contactPhone = "3001234567",
            address = "Address 123",
            city = "Bogotá",
            department = "Cundinamarca",
            country = "Colombia",
            creditLimit = 5000000.0,
            creditDays = 15,
            isActive = true,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            salespersonId = 2,
            salesperson = salespersonDto
        )

        val domain = dto.toDomain()

        assertEquals(1, domain.id)
        assertEquals("Farmacia Test", domain.businessName)
        assertEquals(2, domain.salespersonId)
        assertNotNull(domain.salesperson)
        assertEquals(2, domain.salesperson?.id)
        assertEquals("Maria", domain.salesperson?.firstName)
        assertEquals("Gonzalez", domain.salesperson?.lastName)
        assertEquals("maria.gonzalez@medisupply.com", domain.salesperson?.email)
    }
}
