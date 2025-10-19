package com.misw.medisupply.data.remote.dto.order

import com.google.gson.Gson
import org.junit.Test
import org.junit.Assert.*

class CreateOrderRequestTest {

    private val gson = Gson()

    @Test
    fun `serialize to JSON correctly`() {
        val items = listOf(
            CreateOrderItemRequest(
                productSku = "MED-001",
                quantity = 10,
                discountPercentage = 5.0,
                taxPercentage = 19.0
            )
        )

        val request = CreateOrderRequest(
            customerId = 1,
            sellerId = "SELLER-001",
            items = items,
            paymentTerms = "NET_30",
            paymentMethod = "BANK_TRANSFER",
            deliveryAddress = "Calle 123",
            deliveryCity = "Bogotá",
            deliveryDepartment = "Cundinamarca",
            preferredDistributionCenter = "DC-BOG",
            notes = "Entrega urgente"
        )

        val json = gson.toJson(request)

        assertTrue(json.contains("\"customer_id\":1"))
        assertTrue(json.contains("\"seller_id\":\"SELLER-001\""))
        assertTrue(json.contains("\"payment_terms\":\"NET_30\""))
        assertTrue(json.contains("\"delivery_city\":\"Bogotá\""))
    }

    @Test
    fun `serialize with null optional fields`() {
        val items = listOf(
            CreateOrderItemRequest(
                productSku = "MED-001",
                quantity = 5,
                discountPercentage = 0.0,
                taxPercentage = 19.0
            )
        )

        val request = CreateOrderRequest(
            customerId = 2,
            sellerId = "SELLER-002",
            items = items,
            paymentTerms = "NET_15",
            paymentMethod = null,
            deliveryAddress = null,
            deliveryCity = null,
            deliveryDepartment = null,
            preferredDistributionCenter = null,
            notes = null
        )

        val json = gson.toJson(request)

        assertTrue(json.contains("\"customer_id\":2"))
        assertTrue(json.contains("\"seller_id\":\"SELLER-002\""))
    }

    @Test
    fun `create request with multiple items`() {
        val items = listOf(
            CreateOrderItemRequest("SKU-001", 10),
            CreateOrderItemRequest("SKU-002", 5, 5.0),
            CreateOrderItemRequest("SKU-003", 3, 10.0, 16.0)
        )

        val request = CreateOrderRequest(
            customerId = 1,
            sellerId = "SELLER-001",
            items = items,
            paymentTerms = "NET_30",
            paymentMethod = "CREDIT_CARD",
            deliveryAddress = "Address",
            deliveryCity = "City",
            deliveryDepartment = "Department",
            preferredDistributionCenter = "DC-001",
            notes = "Test notes"
        )

        assertEquals(3, request.items.size)
        assertEquals(1, request.customerId)
    }
}
