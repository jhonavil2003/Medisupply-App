package com.misw.medisupply.domain.model.customer

import org.junit.Assert.*
import org.junit.Test

class CustomerTypeTest {

    @Test
    fun `fromString returns HOSPITAL for hospital`() {
        val result = CustomerType.fromString("HOSPITAL")
        
        assertEquals(CustomerType.HOSPITAL, result)
    }

    @Test
    fun `fromString returns CLINICA for clinica`() {
        val result = CustomerType.fromString("CLINICA")
        
        assertEquals(CustomerType.CLINICA, result)
    }

    @Test
    fun `fromString returns FARMACIA for farmacia`() {
        val result = CustomerType.fromString("FARMACIA")
        
        assertEquals(CustomerType.FARMACIA, result)
    }

    @Test
    fun `fromString returns DISTRIBUIDOR for distribuidor`() {
        val result = CustomerType.fromString("DISTRIBUIDOR")
        
        assertEquals(CustomerType.DISTRIBUIDOR, result)
    }

    @Test
    fun `fromString returns IPS for ips`() {
        val result = CustomerType.fromString("IPS")
        
        assertEquals(CustomerType.IPS, result)
    }

    @Test
    fun `fromString returns EPS for eps`() {
        val result = CustomerType.fromString("EPS")
        
        assertEquals(CustomerType.EPS, result)
    }

    @Test
    fun `fromString is case insensitive`() {
        assertEquals(CustomerType.HOSPITAL, CustomerType.fromString("hospital"))
        assertEquals(CustomerType.CLINICA, CustomerType.fromString("ClInIcA"))
        assertEquals(CustomerType.FARMACIA, CustomerType.fromString("FaRmAcIa"))
    }

    @Test
    fun `fromString returns HOSPITAL for invalid value`() {
        val result = CustomerType.fromString("INVALID_TYPE")
        
        assertEquals(CustomerType.HOSPITAL, result)
    }

    @Test
    fun `enum values have correct display names`() {
        assertEquals("Hospital", CustomerType.HOSPITAL.displayName)
        assertEquals("Clínica", CustomerType.CLINICA.displayName)
        assertEquals("Farmacia", CustomerType.FARMACIA.displayName)
        assertEquals("Distribuidor", CustomerType.DISTRIBUIDOR.displayName)
        assertEquals("IPS", CustomerType.IPS.displayName)
        assertEquals("EPS", CustomerType.EPS.displayName)
    }
}
