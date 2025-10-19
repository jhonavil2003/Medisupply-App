package com.misw.medisupply.data.remote.dto.stock

import com.google.gson.Gson
import org.junit.Test
import org.junit.Assert.*

class StockLevelDtoTest {

    private val gson = Gson()

    @Test
    fun `deserialize from JSON correctly`() {
        val json = """
            {
                "product_sku": "MED-001",
                "total_available": 100,
                "total_reserved": 20,
                "total_in_transit": 50,
                "distribution_centers": [
                    {
                        "distribution_center_id": 1,
                        "distribution_center_code": "DC-BOG",
                        "distribution_center_name": "Centro Bogotá",
                        "city": "Bogotá",
                        "quantity_available": 100,
                        "quantity_reserved": 20,
                        "quantity_in_transit": 50,
                        "is_low_stock": false,
                        "is_out_of_stock": false
                    }
                ]
            }
        """.trimIndent()

        val dto = gson.fromJson(json, StockLevelDto::class.java)

        assertEquals("MED-001", dto.productSku)
        assertEquals(100, dto.totalAvailable)
        assertEquals(20, dto.totalReserved)
        assertEquals(50, dto.totalInTransit)
        assertEquals(1, dto.distributionCenters.size)
    }

    @Test
    fun `deserialize with null optional fields`() {
        val json = """
            {
                "product_sku": "MED-002",
                "total_available": 50,
                "total_reserved": null,
                "total_in_transit": null,
                "distribution_centers": []
            }
        """.trimIndent()

        val dto = gson.fromJson(json, StockLevelDto::class.java)

        assertNull(dto.totalReserved)
        assertNull(dto.totalInTransit)
        assertTrue(dto.distributionCenters.isEmpty())
    }

    @Test
    fun `toDomain converts to domain model correctly`() {
        val centerDto = DistributionCenterDto(
            distributionCenterId = 1,
            distributionCenterCode = "DC-TEST",
            distributionCenterName = "Test Center",
            city = "Test City",
            quantityAvailable = 100,
            quantityReserved = 10,
            quantityInTransit = 20,
            isLowStock = false,
            isOutOfStock = false
        )

        val dto = StockLevelDto(
            productSku = "TEST-SKU",
            totalAvailable = 100,
            totalReserved = 10,
            totalInTransit = 20,
            distributionCenters = listOf(centerDto)
        )

        val domain = dto.toDomain()

        assertEquals("TEST-SKU", domain.productSku)
        assertEquals(100, domain.totalAvailable)
        assertEquals(10, domain.totalReserved)
        assertEquals(20, domain.totalInTransit)
        assertEquals(1, domain.distributionCenters.size)
    }

    @Test
    fun `serialize to JSON correctly`() {
        val dto = StockLevelDto(
            productSku = "TEST-001",
            totalAvailable = 75,
            totalReserved = 25,
            totalInTransit = null,
            distributionCenters = emptyList()
        )

        val json = gson.toJson(dto)

        assertTrue(json.contains("\"product_sku\":\"TEST-001\""))
        assertTrue(json.contains("\"total_available\":75"))
        assertTrue(json.contains("\"total_reserved\":25"))
    }
}
