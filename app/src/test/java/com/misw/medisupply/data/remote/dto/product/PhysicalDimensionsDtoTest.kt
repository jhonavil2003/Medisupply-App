package com.misw.medisupply.data.remote.dto.product

import com.google.gson.Gson
import org.junit.Test
import org.junit.Assert.*

class PhysicalDimensionsDtoTest {

    private val gson = Gson()

    @Test
    fun `deserialize from JSON correctly`() {
        val json = """
            {
                "weight_kg": 1.5,
                "length_cm": 10.5,
                "width_cm": 5.2,
                "height_cm": 3.8
            }
        """.trimIndent()

        val dto = gson.fromJson(json, PhysicalDimensionsDto::class.java)

        assertNotNull(dto.weightKg)
        assertNotNull(dto.lengthCm)
        assertNotNull(dto.widthCm)
        assertNotNull(dto.heightCm)
        assertEquals(1.5f, dto.weightKg!!, 0.01f)
        assertEquals(10.5f, dto.lengthCm!!, 0.01f)
        assertEquals(5.2f, dto.widthCm!!, 0.01f)
        assertEquals(3.8f, dto.heightCm!!, 0.01f)
    }

    @Test
    fun `deserialize with null fields`() {
        val json = """
            {
                "weight_kg": null,
                "length_cm": null,
                "width_cm": null,
                "height_cm": null
            }
        """.trimIndent()

        val dto = gson.fromJson(json, PhysicalDimensionsDto::class.java)

        assertNull(dto.weightKg)
        assertNull(dto.lengthCm)
        assertNull(dto.widthCm)
        assertNull(dto.heightCm)
    }

    @Test
    fun `serialize to JSON correctly`() {
        val dto = PhysicalDimensionsDto(
            weightKg = 3.0f,
            lengthCm = 20.0f,
            widthCm = 15.0f,
            heightCm = 10.0f
        )

        val json = gson.toJson(dto)

        assertTrue(json.contains("\"weight_kg\":3.0"))
        assertTrue(json.contains("\"length_cm\":20.0"))
        assertTrue(json.contains("\"width_cm\":15.0"))
        assertTrue(json.contains("\"height_cm\":10.0"))
    }

    @Test
    fun `serialize with null values`() {
        val dto = PhysicalDimensionsDto(
            weightKg = null,
            lengthCm = null,
            widthCm = null,
            heightCm = null
        )

        val json = gson.toJson(dto)

        assertNotNull(json)
    }
}
