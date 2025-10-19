package com.misw.medisupply.domain.model.order

import org.junit.Assert.*
import org.junit.Test

class OrderStatusTest {

    @Test
    fun `fromValue returns PENDING for pending`() {
        val result = OrderStatus.fromValue("pending")
        
        assertEquals(OrderStatus.PENDING, result)
    }

    @Test
    fun `fromValue returns CONFIRMED for confirmed`() {
        val result = OrderStatus.fromValue("confirmed")
        
        assertEquals(OrderStatus.CONFIRMED, result)
    }

    @Test
    fun `fromValue returns PROCESSING for processing`() {
        val result = OrderStatus.fromValue("processing")
        
        assertEquals(OrderStatus.PROCESSING, result)
    }

    @Test
    fun `fromValue returns SHIPPED for shipped`() {
        val result = OrderStatus.fromValue("shipped")
        
        assertEquals(OrderStatus.SHIPPED, result)
    }

    @Test
    fun `fromValue returns DELIVERED for delivered`() {
        val result = OrderStatus.fromValue("delivered")
        
        assertEquals(OrderStatus.DELIVERED, result)
    }

    @Test
    fun `fromValue returns CANCELLED for cancelled`() {
        val result = OrderStatus.fromValue("cancelled")
        
        assertEquals(OrderStatus.CANCELLED, result)
    }

    @Test
    fun `fromValue returns PENDING for invalid value`() {
        val result = OrderStatus.fromValue("invalid_status")
        
        assertEquals(OrderStatus.PENDING, result)
    }

    @Test
    fun `fromValue returns PENDING for empty string`() {
        val result = OrderStatus.fromValue("")
        
        assertEquals(OrderStatus.PENDING, result)
    }

    @Test
    fun `enum values have correct properties`() {
        assertEquals("pending", OrderStatus.PENDING.value)
        assertEquals("Pendiente", OrderStatus.PENDING.displayName)
        
        assertEquals("confirmed", OrderStatus.CONFIRMED.value)
        assertEquals("Confirmada", OrderStatus.CONFIRMED.displayName)
        
        assertEquals("cancelled", OrderStatus.CANCELLED.value)
        assertEquals("Cancelada", OrderStatus.CANCELLED.displayName)
    }
}
