package com.misw.medisupply.domain.model.order

import org.junit.Assert.*
import org.junit.Test

class PaymentMethodTest {

    @Test
    fun `fromValue returns TRANSFER for transferencia`() {
        val result = PaymentMethod.fromValue("transferencia")
        
        assertEquals(PaymentMethod.TRANSFER, result)
    }

    @Test
    fun `fromValue returns CHECK for cheque`() {
        val result = PaymentMethod.fromValue("cheque")
        
        assertEquals(PaymentMethod.CHECK, result)
    }

    @Test
    fun `fromValue returns CASH for efectivo`() {
        val result = PaymentMethod.fromValue("efectivo")
        
        assertEquals(PaymentMethod.CASH, result)
    }

    @Test
    fun `fromValue returns CARD for tarjeta`() {
        val result = PaymentMethod.fromValue("tarjeta")
        
        assertEquals(PaymentMethod.CARD, result)
    }

    @Test
    fun `fromValue returns null for invalid value`() {
        val result = PaymentMethod.fromValue("invalid_method")
        
        assertNull(result)
    }

    @Test
    fun `fromValue returns null for null value`() {
        val result = PaymentMethod.fromValue(null)
        
        assertNull(result)
    }

    @Test
    fun `fromValue returns null for empty string`() {
        val result = PaymentMethod.fromValue("")
        
        assertNull(result)
    }

    @Test
    fun `enum values have correct properties`() {
        assertEquals("transferencia", PaymentMethod.TRANSFER.value)
        assertEquals("Transferencia Bancaria", PaymentMethod.TRANSFER.displayName)
        
        assertEquals("cheque", PaymentMethod.CHECK.value)
        assertEquals("Cheque", PaymentMethod.CHECK.displayName)
        
        assertEquals("efectivo", PaymentMethod.CASH.value)
        assertEquals("Efectivo", PaymentMethod.CASH.displayName)
        
        assertEquals("tarjeta", PaymentMethod.CARD.value)
        assertEquals("Tarjeta", PaymentMethod.CARD.displayName)
    }
}
