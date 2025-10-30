package com.misw.medisupply.domain.model.product

import org.junit.Assert.*
import org.junit.Test

class ProductTest {

    private fun createTestProduct(
        name: String = "Test Product",
        sku: String = "SKU123",
        unitPrice: Float = 100.0f,
        currency: String = "USD",
        requiresColdChain: Boolean = false,
        regulatoryInfo: RegulatoryInfo? = null
    ): Product {
        return Product(
            id = 1,
            sku = sku,
            name = name,
            description = "Test description",
            category = "Medical",
            subcategory = "Equipment",
            unitPrice = unitPrice,
            currency = currency,
            unitOfMeasure = "Unit",
            supplierId = 1,
            supplierName = "Test Supplier",
            requiresColdChain = requiresColdChain,
            storageConditions = null,
            regulatoryInfo = regulatoryInfo,
            physicalDimensions = null,
            manufacturer = "Test Manufacturer",
            countryOfOrigin = "USA",
            barcode = "123456789",
            imageUrl = null,
            isActive = true,
            isDiscontinued = false,
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01"
        )
    }

    @Test
    fun `getDisplayName returns name with SKU`() {
        val product = createTestProduct(name = "Medical Device", sku = "MD-001")
        
        val result = product.getDisplayName()
        
        assertEquals("Medical Device (MD-001)", result)
    }

    @Test
    fun `requiresSpecialHandling returns true when requiresColdChain is true`() {
        val product = createTestProduct(requiresColdChain = true)
        
        val result = product.requiresSpecialHandling()
        
        assertTrue(result)
    }

    @Test
    fun `requiresSpecialHandling returns true when requires prescription`() {
        val regulatoryInfo = RegulatoryInfo(
            sanitaryRegistration = "INVIMA-123",
            requiresPrescription = true,
            regulatoryClass = "Clase II"
        )
        val product = createTestProduct(
            requiresColdChain = false,
            regulatoryInfo = regulatoryInfo
        )
        
        val result = product.requiresSpecialHandling()
        
        assertTrue(result)
    }

    @Test
    fun `requiresSpecialHandling returns true when regulatory class is Clase III`() {
        val regulatoryInfo = RegulatoryInfo(
            sanitaryRegistration = "INVIMA-456",
            requiresPrescription = false,
            regulatoryClass = "Clase III"
        )
        val product = createTestProduct(
            requiresColdChain = false,
            regulatoryInfo = regulatoryInfo
        )
        
        val result = product.requiresSpecialHandling()
        
        assertTrue(result)
    }

    @Test
    fun `requiresSpecialHandling returns false when no special conditions`() {
        val regulatoryInfo = RegulatoryInfo(
            sanitaryRegistration = "INVIMA-789",
            requiresPrescription = false,
            regulatoryClass = "Clase I"
        )
        val product = createTestProduct(
            requiresColdChain = false,
            regulatoryInfo = regulatoryInfo
        )
        
        val result = product.requiresSpecialHandling()
        
        assertFalse(result)
    }

    @Test
    fun `requiresSpecialHandling returns false when regulatoryInfo is null`() {
        val product = createTestProduct(
            requiresColdChain = false,
            regulatoryInfo = null
        )
        
        val result = product.requiresSpecialHandling()
        
        assertFalse(result)
    }

    @Test
    fun `getFormattedPrice returns price with currency`() {
        val product = createTestProduct(unitPrice = 250.50f, currency = "USD")
        
        val result = product.getFormattedPrice()
        
        // FormatUtils uses Colombian format: $ 250,50 (comma for decimals)
        assertEquals("$ 250,50", result)
    }

    @Test
    fun `getFormattedPrice handles different currencies`() {
        val product = createTestProduct(unitPrice = 1000.0f, currency = "COP")
        
        val result = product.getFormattedPrice()
        
        // FormatUtils uses Colombian format: $ 1.000,00 (dot for thousands, comma for decimals)
        assertEquals("$ 1.000,00", result)
    }

    @Test
    fun `getFormattedPrice formats with two decimal places`() {
        val product = createTestProduct(unitPrice = 99.9f, currency = "USD")
        
        val result = product.getFormattedPrice()
        
        // FormatUtils uses Colombian format: $ 99,90 (comma for decimals)
        assertEquals("$ 99,90", result)
    }
}
