package com.misw.medisupply.data.remote.dto.product

import com.google.gson.Gson
import org.junit.Test
import org.junit.Assert.*

class ProductDtoTest {

    private val gson = Gson()

    @Test
    fun `deserialize from JSON correctly`() {
        val json = """
            {
                "id": 1,
                "sku": "MED-001",
                "name": "Medicamento Test",
                "description": "Descripción del medicamento",
                "category": "Medicamentos",
                "subcategory": "Antibióticos",
                "unit_price": 25000.0,
                "currency": "COP",
                "unit_of_measure": "UNIDAD",
                "supplier_id": 100,
                "supplier_name": "Proveedor Test",
                "requires_cold_chain": false,
                "storage_conditions": null,
                "regulatory_info": null,
                "physical_dimensions": null,
                "manufacturer": "Lab Test",
                "country_of_origin": "Colombia",
                "barcode": "7891234567890",
                "image_url": "http://example.com/image.jpg",
                "is_active": true,
                "is_discontinued": false,
                "created_at": "2024-01-01T00:00:00Z",
                "updated_at": "2024-01-01T00:00:00Z",
                "certifications": null,
                "regulatory_conditions": null
            }
        """.trimIndent()

        val dto = gson.fromJson(json, ProductDto::class.java)

        assertEquals(1, dto.id)
        assertEquals("MED-001", dto.sku)
        assertEquals("Medicamento Test", dto.name)
        assertEquals("Medicamentos", dto.category)
        assertEquals(25000.0f, dto.unitPrice, 0.01f)
        assertEquals("COP", dto.currency)
        assertTrue(dto.isActive)
        assertFalse(dto.isDiscontinued)
        assertFalse(dto.requiresColdChain)
    }

    @Test
    fun `deserialize with null optional fields`() {
        val json = """
            {
                "id": 2,
                "sku": "MED-002",
                "name": "Producto Simple",
                "description": null,
                "category": "Equipos",
                "subcategory": null,
                "unit_price": 15000.0,
                "currency": "COP",
                "unit_of_measure": "UNIDAD",
                "supplier_id": 200,
                "supplier_name": null,
                "requires_cold_chain": false,
                "storage_conditions": null,
                "regulatory_info": null,
                "physical_dimensions": null,
                "manufacturer": null,
                "country_of_origin": null,
                "barcode": null,
                "image_url": null,
                "is_active": true,
                "is_discontinued": false,
                "created_at": "2024-01-01T00:00:00Z",
                "updated_at": "2024-01-01T00:00:00Z",
                "certifications": null,
                "regulatory_conditions": null
            }
        """.trimIndent()

        val dto = gson.fromJson(json, ProductDto::class.java)

        assertNull(dto.description)
        assertNull(dto.subcategory)
        assertNull(dto.supplierName)
        assertNull(dto.manufacturer)
        assertNull(dto.imageUrl)
    }

    @Test
    fun `toDomain converts to domain model correctly`() {
        val dto = ProductDto(
            id = 1,
            sku = "TEST-001",
            name = "Test Product",
            description = "Test Description",
            category = "Test Category",
            subcategory = "Test Subcategory",
            unitPrice = 10000.0f,
            currency = "COP",
            unitOfMeasure = "UNIDAD",
            supplierId = 1,
            supplierName = "Test Supplier",
            requiresColdChain = false,
            storageConditions = null,
            regulatoryInfo = null,
            physicalDimensions = null,
            manufacturer = "Test Manufacturer",
            countryOfOrigin = "Colombia",
            barcode = "1234567890",
            imageUrl = "http://test.com/image.jpg",
            isActive = true,
            isDiscontinued = false,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            certifications = null,
            regulatoryConditions = null
        )

        val domain = dto.toDomain()

        assertEquals("TEST-001", domain.sku)
        assertEquals("Test Product", domain.name)
        assertEquals(10000.0f, domain.unitPrice, 0.01f)
        assertEquals("COP", domain.currency)
        assertTrue(domain.isActive)
        assertFalse(domain.isDiscontinued)
    }

    @Test
    fun `serialize to JSON correctly`() {
        val dto = ProductDto(
            id = 1,
            sku = "TEST-001",
            name = "Test Product",
            description = null,
            category = "Category",
            subcategory = null,
            unitPrice = 5000.0f,
            currency = "COP",
            unitOfMeasure = "UNIDAD",
            supplierId = 1,
            supplierName = null,
            requiresColdChain = false,
            storageConditions = null,
            regulatoryInfo = null,
            physicalDimensions = null,
            manufacturer = null,
            countryOfOrigin = null,
            barcode = null,
            imageUrl = null,
            isActive = true,
            isDiscontinued = false,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            certifications = null,
            regulatoryConditions = null
        )

        val json = gson.toJson(dto)

        assertTrue(json.contains("\"sku\":\"TEST-001\""))
        assertTrue(json.contains("\"name\":\"Test Product\""))
        assertTrue(json.contains("\"unit_price\":5000.0"))
    }
}
