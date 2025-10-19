package com.misw.medisupply.data.remote.dto.order

import com.google.gson.Gson
import org.junit.Test
import org.junit.Assert.*

class CreateOrderItemRequestTest {

    private val gson = Gson()

    @Test
    fun `serialize to JSON correctly`() {
        val item = CreateOrderItemRequest(
            productSku = "MED-001",
            quantity = 10,
            discountPercentage = 5.0,
            taxPercentage = 19.0
        )

        val json = gson.toJson(item)

        assertTrue(json.contains("\"product_sku\":\"MED-001\""))
        assertTrue(json.contains("\"quantity\":10"))
        assertTrue(json.contains("\"discount_percentage\":5.0"))
        assertTrue(json.contains("\"tax_percentage\":19.0"))
    }

    @Test
    fun `deserialize from JSON correctly`() {
        val json = """
            {
                "product_sku": "MED-001",
                "quantity": 10,
                "discount_percentage": 5.0,
                "tax_percentage": 19.0
            }
        """.trimIndent()

        val item = gson.fromJson(json, CreateOrderItemRequest::class.java)

        assertEquals("MED-001", item.productSku)
        assertEquals(10, item.quantity)
        assertEquals(5.0, item.discountPercentage, 0.01)
        assertEquals(19.0, item.taxPercentage, 0.01)
    }

    @Test
    fun `create item with zero discount`() {
        val item = CreateOrderItemRequest(
            productSku = "EQUIP-001",
            quantity = 5,
            discountPercentage = 0.0,
            taxPercentage = 19.0
        )

        assertEquals("EQUIP-001", item.productSku)
        assertEquals(5, item.quantity)
        assertEquals(0.0, item.discountPercentage, 0.01)
        assertEquals(19.0, item.taxPercentage, 0.01)
    }

    @Test
    fun `create item with default values`() {
        val item = CreateOrderItemRequest(
            productSku = "TEST-001",
            quantity = 10
        )

        assertEquals("TEST-001", item.productSku)
        assertEquals(10, item.quantity)
        assertEquals(0.0, item.discountPercentage, 0.01)
        assertEquals(19.0, item.taxPercentage, 0.01)
    }
}
