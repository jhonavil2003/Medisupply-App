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
                "updated_at": "2024-01-01T00:00:00Z"
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
                "updated_at": null
            }
        """.trimIndent()

        val dto = gson.fromJson(json, CustomerDto::class.java)

        assertNull(dto.tradeName)
        assertNull(dto.contactName)
        assertNull(dto.contactEmail)
        assertNull(dto.address)
        assertNull(dto.createdAt)
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
            updatedAt = "2024-01-01T00:00:00Z"
        )

        val domain = dto.toDomain()

        assertEquals(1, domain.id)
        assertEquals("900123456-7", domain.documentNumber)
        assertEquals("Hospital Test", domain.businessName)
        assertEquals(10000000.0, domain.creditLimit, 0.01)
        assertEquals(30, domain.creditDays)
        assertTrue(domain.isActive)
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
            updatedAt = null
        )

        val json = gson.toJson(dto)

        assertTrue(json.contains("\"id\":1"))
        assertTrue(json.contains("\"document_number\":\"900123456-7\""))
        assertTrue(json.contains("\"business_name\":\"Hospital Test\""))
    }
}
