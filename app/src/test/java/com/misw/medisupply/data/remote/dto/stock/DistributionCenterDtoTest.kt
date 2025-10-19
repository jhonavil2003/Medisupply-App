package com.misw.medisupply.data.remote.dto.stock

import com.google.gson.Gson
import org.junit.Test
import org.junit.Assert.*

class DistributionCenterDtoTest {

    private val gson = Gson()

    @Test
    fun `deserialize from JSON correctly`() {
        val json = """
            {
                "distribution_center_id": 1,
                "distribution_center_code": "DC-BOG",
                "distribution_center_name": "Centro Bogotá",
                "city": "Bogotá",
                "quantity_available": 100,
                "quantity_reserved": 20,
                "quantity_in_transit": 30,
                "is_low_stock": false,
                "is_out_of_stock": false
            }
        """.trimIndent()

        val dto = gson.fromJson(json, DistributionCenterDto::class.java)

        assertEquals(1, dto.distributionCenterId)
        assertEquals("DC-BOG", dto.distributionCenterCode)
        assertEquals("Centro Bogotá", dto.distributionCenterName)
        assertEquals("Bogotá", dto.city)
        assertEquals(100, dto.quantityAvailable)
        assertEquals(20, dto.quantityReserved)
        assertEquals(30, dto.quantityInTransit)
        assertFalse(dto.isLowStock)
        assertFalse(dto.isOutOfStock)
    }

    @Test
    fun `deserialize with null optional fields`() {
        val json = """
            {
                "distribution_center_id": 2,
                "distribution_center_code": "DC-MED",
                "distribution_center_name": "Centro Medellín",
                "city": "Medellín",
                "quantity_available": 50,
                "quantity_reserved": null,
                "quantity_in_transit": null,
                "is_low_stock": true,
                "is_out_of_stock": false
            }
        """.trimIndent()

        val dto = gson.fromJson(json, DistributionCenterDto::class.java)

        assertEquals(2, dto.distributionCenterId)
        assertEquals("DC-MED", dto.distributionCenterCode)
        assertNull(dto.quantityReserved)
        assertNull(dto.quantityInTransit)
        assertTrue(dto.isLowStock)
    }

    @Test
    fun `toDomain converts to domain model correctly`() {
        val dto = DistributionCenterDto(
            distributionCenterId = 3,
            distributionCenterCode = "DC-CALI",
            distributionCenterName = "Centro Cali",
            city = "Cali",
            quantityAvailable = 75,
            quantityReserved = 5,
            quantityInTransit = 10,
            isLowStock = false,
            isOutOfStock = false
        )

        val domain = dto.toDomain()

        assertEquals(3, domain.id)
        assertEquals("DC-CALI", domain.code)
        assertEquals("Centro Cali", domain.name)
        assertEquals("Cali", domain.city)
        assertEquals(75, domain.quantityAvailable)
        assertEquals(5, domain.quantityReserved)
        assertEquals(10, domain.quantityInTransit)
        assertFalse(domain.isLowStock)
        assertFalse(domain.isOutOfStock)
    }

    @Test
    fun `serialize to JSON correctly`() {
        val dto = DistributionCenterDto(
            distributionCenterId = 4,
            distributionCenterCode = "DC-BARR",
            distributionCenterName = "Centro Barranquilla",
            city = "Barranquilla",
            quantityAvailable = 200,
            quantityReserved = 15,
            quantityInTransit = 25,
            isLowStock = false,
            isOutOfStock = false
        )

        val json = gson.toJson(dto)

        assertTrue(json.contains("\"distribution_center_id\":4"))
        assertTrue(json.contains("\"distribution_center_code\":\"DC-BARR\""))
        assertTrue(json.contains("\"distribution_center_name\":\"Centro Barranquilla\""))
        assertTrue(json.contains("\"quantity_available\":200"))
        assertTrue(json.contains("\"is_low_stock\":false"))
    }
}
