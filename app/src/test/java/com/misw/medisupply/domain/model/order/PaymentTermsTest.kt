package com.misw.medisupply.domain.model.order

import org.junit.Assert.*
import org.junit.Test

class PaymentTermsTest {

    @Test
    fun `fromValue returns CASH for contado`() {
        val result = PaymentTerms.fromValue("contado")
        
        assertEquals(PaymentTerms.CASH, result)
        assertEquals(0, result.days)
    }

    @Test
    fun `fromValue returns CREDIT_30 for credito_30`() {
        val result = PaymentTerms.fromValue("credito_30")
        
        assertEquals(PaymentTerms.CREDIT_30, result)
        assertEquals(30, result.days)
    }

    @Test
    fun `fromValue returns CREDIT_45 for credito_45`() {
        val result = PaymentTerms.fromValue("credito_45")
        
        assertEquals(PaymentTerms.CREDIT_45, result)
        assertEquals(45, result.days)
    }

    @Test
    fun `fromValue returns CREDIT_60 for credito_60`() {
        val result = PaymentTerms.fromValue("credito_60")
        
        assertEquals(PaymentTerms.CREDIT_60, result)
        assertEquals(60, result.days)
    }

    @Test
    fun `fromValue returns CREDIT_90 for credito_90`() {
        val result = PaymentTerms.fromValue("credito_90")
        
        assertEquals(PaymentTerms.CREDIT_90, result)
        assertEquals(90, result.days)
    }

    @Test
    fun `fromValue returns CASH for invalid value`() {
        val result = PaymentTerms.fromValue("invalid_payment")
        
        assertEquals(PaymentTerms.CASH, result)
    }

    @Test
    fun `fromValue returns CASH for empty string`() {
        val result = PaymentTerms.fromValue("")
        
        assertEquals(PaymentTerms.CASH, result)
    }

    @Test
    fun `enum values have correct properties`() {
        assertEquals("contado", PaymentTerms.CASH.value)
        assertEquals("Contado", PaymentTerms.CASH.displayName)
        assertEquals(0, PaymentTerms.CASH.days)
        
        assertEquals("credito_30", PaymentTerms.CREDIT_30.value)
        assertEquals("Crédito 30 días", PaymentTerms.CREDIT_30.displayName)
        assertEquals(30, PaymentTerms.CREDIT_30.days)
    }
}
