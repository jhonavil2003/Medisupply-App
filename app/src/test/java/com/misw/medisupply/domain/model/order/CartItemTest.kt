package com.misw.medisupply.domain.model.order

import org.junit.Assert.*
import org.junit.Test

class CartItemTest {

    @Test
    fun `calculateSubtotal returns correct amount`() {
        val cartItem = CartItem(
            productSku = "MED-001",
            productName = "Test Product",
            quantity = 5,
            unitPrice = 10000f,
            stockAvailable = 100
        )
        
        val subtotal = cartItem.calculateSubtotal()
        
        assertEquals(50000f, subtotal, 0.01f)
    }

    @Test
    fun `calculateSubtotal handles zero quantity`() {
        val cartItem = CartItem(
            productSku = "MED-001",
            productName = "Test Product",
            quantity = 0,
            unitPrice = 10000f,
            stockAvailable = 100
        )
        
        val subtotal = cartItem.calculateSubtotal()
        
        assertEquals(0f, subtotal, 0.01f)
    }

    @Test
    fun `getFormattedSubtotal returns formatted currency`() {
        val cartItem = CartItem(
            productSku = "MED-001",
            productName = "Test Product",
            quantity = 5,
            unitPrice = 10000f,
            stockAvailable = 100
        )
        
        val formatted = cartItem.getFormattedSubtotal()
        
        assertTrue(formatted.contains("50"))
        assertTrue(formatted.startsWith("$"))
    }

    @Test
    fun `exceedsStock returns true when quantity exceeds stock`() {
        val cartItem = CartItem(
            productSku = "MED-001",
            productName = "Test Product",
            quantity = 150,
            unitPrice = 10000f,
            stockAvailable = 100
        )
        
        val exceeds = cartItem.exceedsStock()
        
        assertTrue(exceeds)
    }

    @Test
    fun `exceedsStock returns false when quantity within stock`() {
        val cartItem = CartItem(
            productSku = "MED-001",
            productName = "Test Product",
            quantity = 50,
            unitPrice = 10000f,
            stockAvailable = 100
        )
        
        val exceeds = cartItem.exceedsStock()
        
        assertFalse(exceeds)
    }

    @Test
    fun `exceedsStock returns false when quantity equals stock`() {
        val cartItem = CartItem(
            productSku = "MED-001",
            productName = "Test Product",
            quantity = 100,
            unitPrice = 10000f,
            stockAvailable = 100
        )
        
        val exceeds = cartItem.exceedsStock()
        
        assertFalse(exceeds)
    }

    @Test
    fun `exceedsStock returns false when stockAvailable is null`() {
        val cartItem = CartItem(
            productSku = "MED-001",
            productName = "Test Product",
            quantity = 100,
            unitPrice = 10000f,
            stockAvailable = null
        )
        
        val exceeds = cartItem.exceedsStock()
        
        assertFalse(exceeds)
    }

    @Test
    fun `hasSufficientStock returns true when stock is sufficient`() {
        val cartItem = CartItem(
            productSku = "MED-001",
            productName = "Test Product",
            quantity = 50,
            unitPrice = 10000f,
            stockAvailable = 100
        )
        
        val sufficient = cartItem.hasSufficientStock()
        
        assertTrue(sufficient)
    }

    @Test
    fun `hasSufficientStock returns false when stock is insufficient`() {
        val cartItem = CartItem(
            productSku = "MED-001",
            productName = "Test Product",
            quantity = 150,
            unitPrice = 10000f,
            stockAvailable = 100
        )
        
        val sufficient = cartItem.hasSufficientStock()
        
        assertFalse(sufficient)
    }

    @Test
    fun `hasSufficientStock returns true when quantity equals stock`() {
        val cartItem = CartItem(
            productSku = "MED-001",
            productName = "Test Product",
            quantity = 100,
            unitPrice = 10000f,
            stockAvailable = 100
        )
        
        val sufficient = cartItem.hasSufficientStock()
        
        assertTrue(sufficient)
    }

    @Test
    fun `hasSufficientStock returns false when stockAvailable is null`() {
        val cartItem = CartItem(
            productSku = "MED-001",
            productName = "Test Product",
            quantity = 50,
            unitPrice = 10000f,
            stockAvailable = null
        )
        
        val sufficient = cartItem.hasSufficientStock()
        
        assertFalse(sufficient)
    }

    @Test
    fun `getMaxQuantity returns stockAvailable value`() {
        val cartItem = CartItem(
            productSku = "MED-001",
            productName = "Test Product",
            quantity = 50,
            unitPrice = 10000f,
            stockAvailable = 100
        )
        
        val maxQuantity = cartItem.getMaxQuantity()
        
        assertEquals(100, maxQuantity)
    }

    @Test
    fun `getMaxQuantity returns null when stockAvailable is null`() {
        val cartItem = CartItem(
            productSku = "MED-001",
            productName = "Test Product",
            quantity = 50,
            unitPrice = 10000f,
            stockAvailable = null
        )
        
        val maxQuantity = cartItem.getMaxQuantity()
        
        assertNull(maxQuantity)
    }

    @Test
    fun `data class properties are correctly set`() {
        val cartItem = CartItem(
            productSku = "MED-001",
            productName = "Test Product",
            quantity = 10,
            unitPrice = 5000f,
            stockAvailable = 50,
            requiresColdChain = true,
            category = "Medicines"
        )
        
        assertEquals("MED-001", cartItem.productSku)
        assertEquals("Test Product", cartItem.productName)
        assertEquals(10, cartItem.quantity)
        assertEquals(5000f, cartItem.unitPrice, 0.01f)
        assertEquals(50, cartItem.stockAvailable)
        assertTrue(cartItem.requiresColdChain)
        assertEquals("Medicines", cartItem.category)
    }

    @Test
    fun `default values are correctly applied`() {
        val cartItem = CartItem(
            productSku = "MED-001",
            productName = "Test Product",
            quantity = 10,
            unitPrice = 5000f,
            stockAvailable = 50
        )
        
        assertFalse(cartItem.requiresColdChain)
        assertEquals("", cartItem.category)
    }
}
