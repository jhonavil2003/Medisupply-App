package com.misw.medisupply.domain.model.customer

import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class CustomerTest {

    private fun createTestCustomer(
        creditLimit: Double = 10000.0,
        tradeName: String? = "Trade Name",
        businessName: String = "Business Name",
        contactName: String? = "John Doe",
        contactEmail: String? = "john@example.com",
        contactPhone: String? = "123456789",
        address: String? = "123 Main St",
        city: String? = "Bogotá",
        department: String? = "Cundinamarca"
    ): Customer {
        return Customer(
            id = 1,
            documentType = DocumentType.NIT,
            documentNumber = "123456789",
            businessName = businessName,
            tradeName = tradeName,
            customerType = CustomerType.HOSPITAL,
            contactName = contactName,
            contactEmail = contactEmail,
            contactPhone = contactPhone,
            address = address,
            city = city,
            department = department,
            country = "Colombia",
            creditLimit = creditLimit,
            creditDays = 30,
            isActive = true,
            createdAt = Date(),
            updatedAt = Date()
        )
    }

    @Test
    fun `getFormattedCreditLimit formats credit limit correctly`() {
        val customer = createTestCustomer(creditLimit = 15000.50)
        
        val result = customer.getFormattedCreditLimit()
        
        assertTrue(result.contains("15,000.50") || result.contains("15.000,50"))
    }

    @Test
    fun `getFormattedCreditLimit handles zero credit limit`() {
        val customer = createTestCustomer(creditLimit = 0.0)
        
        val result = customer.getFormattedCreditLimit()
        
        assertTrue(result.contains("0.00") || result.contains("0,00"))
    }

    @Test
    fun `getDisplayName returns trade name when available`() {
        val customer = createTestCustomer(tradeName = "My Trade Name", businessName = "Official Business")
        
        val result = customer.getDisplayName()
        
        assertEquals("My Trade Name", result)
    }

    @Test
    fun `getDisplayName returns business name when trade name is null`() {
        val customer = createTestCustomer(tradeName = null, businessName = "Official Business")
        
        val result = customer.getDisplayName()
        
        assertEquals("Official Business", result)
    }

    @Test
    fun `getDisplayName returns business name when trade name is blank`() {
        val customer = createTestCustomer(tradeName = "   ", businessName = "Official Business")
        
        val result = customer.getDisplayName()
        
        assertEquals("Official Business", result)
    }

    @Test
    fun `getContactInfo combines all contact fields`() {
        val customer = createTestCustomer(
            contactName = "Jane Smith",
            contactEmail = "jane@example.com",
            contactPhone = "987654321"
        )
        
        val result = customer.getContactInfo()
        
        assertTrue(result.contains("Jane Smith"))
        assertTrue(result.contains("jane@example.com"))
        assertTrue(result.contains("987654321"))
    }

    @Test
    fun `getContactInfo handles null contact fields`() {
        val customer = createTestCustomer(
            contactName = null,
            contactEmail = null,
            contactPhone = "987654321"
        )
        
        val result = customer.getContactInfo()
        
        assertEquals("987654321", result)
    }

    @Test
    fun `getContactInfo returns empty string when all contact fields are null`() {
        val customer = createTestCustomer(
            contactName = null,
            contactEmail = null,
            contactPhone = null
        )
        
        val result = customer.getContactInfo()
        
        assertEquals("", result)
    }

    @Test
    fun `getFullAddress combines all address fields`() {
        val customer = createTestCustomer(
            address = "Calle 123",
            city = "Medellín",
            department = "Antioquia"
        )
        
        val result = customer.getFullAddress()
        
        assertTrue(result.contains("Calle 123"))
        assertTrue(result.contains("Medellín"))
        assertTrue(result.contains("Antioquia"))
    }

    @Test
    fun `getFullAddress handles null address fields`() {
        val customer = createTestCustomer(
            address = null,
            city = "Medellín",
            department = null
        )
        
        val result = customer.getFullAddress()
        
        assertEquals("Medellín", result)
    }

    @Test
    fun `getFullAddress returns empty string when all address fields are null`() {
        val customer = createTestCustomer(
            address = null,
            city = null,
            department = null
        )
        
        val result = customer.getFullAddress()
        
        assertEquals("", result)
    }
}
