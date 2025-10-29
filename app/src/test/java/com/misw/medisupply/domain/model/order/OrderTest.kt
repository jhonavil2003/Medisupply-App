package com.misw.medisupply.domain.model.order

import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class OrderTest {

    private fun createTestOrder(
        subtotal: Double = 1000.0,
        discountAmount: Double = 100.0,
        taxAmount: Double = 50.0,
        totalAmount: Double = 950.0,
        items: List<OrderItem> = emptyList()
    ): Order {
        return Order(
            id = 1,
            orderNumber = "ORD-001",
            customerId = 1,
            sellerId = "seller1",
            sellerName = "Test Seller",
            orderDate = Date(),
            status = OrderStatus.PENDING,
            subtotal = subtotal,
            discountAmount = discountAmount,
            taxAmount = taxAmount,
            totalAmount = totalAmount,
            paymentTerms = PaymentTerms.CASH,
            paymentMethod = PaymentMethod.CASH,
            deliveryAddress = "Test Address",
            deliveryCity = "Test City",
            deliveryDepartment = "Test Department",
            preferredDistributionCenter = "DC-001",
            notes = null,
            createdAt = Date(),
            updatedAt = Date(),
            customer = null,
            items = items,
            deliveryDate = null
        )
    }

    private fun createTestOrderItem(quantity: Int = 1): OrderItem {
        return OrderItem(
            id = 1,
            orderId = 1,
            productSku = "SKU-001",
            productName = "Test Product",
            quantity = quantity,
            unitPrice = 100.0,
            discountPercentage = 0.0,
            discountAmount = 0.0,
            taxPercentage = 0.0,
            taxAmount = 0.0,
            subtotal = 100.0,
            total = 100.0,
            distributionCenterCode = "DC-001",
            stockConfirmed = true,
            stockConfirmationDate = Date(),
            createdAt = Date()
        )
    }

    @Test
    fun `getFormattedTotal formats total amount correctly`() {
        val order = createTestOrder(totalAmount = 15000.50)
        
        val result = order.getFormattedTotal()
        
        // FormatUtils uses Colombian format: $ 15.000,50
        assertEquals("$ 15.000,50", result)
    }

    @Test
    fun `getFormattedSubtotal formats subtotal correctly`() {
        val order = createTestOrder(subtotal = 8500.25)
        
        val result = order.getFormattedSubtotal()
        
        // FormatUtils uses Colombian format: $ 8.500,25
        assertEquals("$ 8.500,25", result)
    }

    @Test
    fun `getFormattedDiscount formats discount correctly`() {
        val order = createTestOrder(discountAmount = 250.75)
        
        val result = order.getFormattedDiscount()
        
        // FormatUtils uses Colombian format: $ 250,75
        assertEquals("$ 250,75", result)
    }

    @Test
    fun `getFormattedTax formats tax correctly`() {
        val order = createTestOrder(taxAmount = 1900.00)
        
        val result = order.getFormattedTax()
        
        // FormatUtils uses Colombian format: $ 1.900,00
        assertEquals("$ 1.900,00", result)
    }

    @Test
    fun `getTotalItems returns correct count when order has items`() {
        val items = listOf(
            createTestOrderItem(),
            createTestOrderItem(),
            createTestOrderItem()
        )
        val order = createTestOrder(items = items)
        
        val result = order.getTotalItems()
        
        assertEquals(3, result)
    }

    @Test
    fun `getTotalItems returns zero when order has no items`() {
        val order = createTestOrder(items = emptyList())
        
        val result = order.getTotalItems()
        
        assertEquals(0, result)
    }

    @Test
    fun `getTotalQuantity sums all item quantities`() {
        val items = listOf(
            createTestOrderItem(quantity = 5),
            createTestOrderItem(quantity = 3),
            createTestOrderItem(quantity = 7)
        )
        val order = createTestOrder(items = items)
        
        val result = order.getTotalQuantity()
        
        assertEquals(15, result)
    }

    @Test
    fun `getTotalQuantity returns zero when order has no items`() {
        val order = createTestOrder(items = emptyList())
        
        val result = order.getTotalQuantity()
        
        assertEquals(0, result)
    }

    @Test
    fun `getTotalQuantity handles items with quantity one`() {
        val items = listOf(
            createTestOrderItem(quantity = 1),
            createTestOrderItem(quantity = 1)
        )
        val order = createTestOrder(items = items)
        
        val result = order.getTotalQuantity()
        
        assertEquals(2, result)
    }
}
