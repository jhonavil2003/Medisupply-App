package com.misw.medisupply.data.remote.dto.product

import com.google.gson.Gson
import org.junit.Test
import org.junit.Assert.*

class PaginationDtoTest {

    private val gson = Gson()

    @Test
    fun `deserialize from JSON correctly`() {
        val json = """
            {
                "page": 1,
                "per_page": 10,
                "total_pages": 10,
                "total_items": 100,
                "has_next": true,
                "has_prev": false
            }
        """.trimIndent()

        val dto = gson.fromJson(json, PaginationDto::class.java)

        assertEquals(1, dto.page)
        assertEquals(10, dto.perPage)
        assertEquals(10, dto.totalPages)
        assertEquals(100, dto.totalItems)
        assertTrue(dto.hasNext)
        assertFalse(dto.hasPrev)
    }

    @Test
    fun `serialize to JSON correctly`() {
        val dto = PaginationDto(
            page = 3,
            perPage = 10,
            totalPages = 7,
            totalItems = 70,
            hasNext = true,
            hasPrev = true
        )

        val json = gson.toJson(dto)

        assertTrue(json.contains("\"page\":3"))
        assertTrue(json.contains("\"per_page\":10"))
        assertTrue(json.contains("\"total_pages\":7"))
        assertTrue(json.contains("\"total_items\":70"))
        assertTrue(json.contains("\"has_next\":true"))
        assertTrue(json.contains("\"has_prev\":true"))
    }

    @Test
    fun `verify first page pagination`() {
        val dto = PaginationDto(
            page = 1,
            perPage = 10,
            totalPages = 1,
            totalItems = 5,
            hasNext = false,
            hasPrev = false
        )

        assertEquals(1, dto.page)
        assertEquals(1, dto.totalPages)
        assertFalse(dto.hasNext)
        assertFalse(dto.hasPrev)
    }

    @Test
    fun `verify last page pagination`() {
        val dto = PaginationDto(
            page = 5,
            perPage = 10,
            totalPages = 5,
            totalItems = 50,
            hasNext = false,
            hasPrev = true
        )

        assertEquals(5, dto.page)
        assertEquals(5, dto.totalPages)
        assertFalse(dto.hasNext)
        assertTrue(dto.hasPrev)
    }
}
