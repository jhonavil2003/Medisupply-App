package com.misw.medisupply.core.base

import org.junit.Test
import org.junit.Assert.*

class NetworkResultTest {

    @Test
    fun `Success state holds data correctly`() {
        val data = "Test Data"
        val result = NetworkResult.Success(data)
        
        assertEquals(data, result.data)
        assertNull(result.message)
        assertNull(result.code)
    }

    @Test
    fun `Error state holds message and code correctly`() {
        val message = "Error occurred"
        val code = 404
        val result = NetworkResult.Error<String>(message, code)
        
        assertEquals(message, result.message)
        assertEquals(code, result.code)
        assertNull(result.data)
    }

    @Test
    fun `Error state can hold partial data`() {
        val message = "Partial error"
        val code = 500
        val data = "Partial data"
        val result = NetworkResult.Error(message, code, data)
        
        assertEquals(message, result.message)
        assertEquals(code, result.code)
        assertEquals(data, result.data)
    }

    @Test
    fun `Error state with default code`() {
        val message = "Error without code"
        val result = NetworkResult.Error<String>(message)
        
        assertEquals(message, result.message)
        assertNull(result.code)
        assertNull(result.data)
    }

    @Test
    fun `Loading state has no data`() {
        val result = NetworkResult.Loading<String>()
        
        assertNull(result.data)
        assertNull(result.message)
        assertNull(result.code)
    }

    @Test
    fun `toResource converts Success correctly`() {
        val data = "Test Data"
        val networkResult = NetworkResult.Success(data)
        val resource = networkResult.toResource()
        
        assertTrue(resource is Resource.Success)
        assertEquals(data, resource.data)
    }

    @Test
    fun `toResource converts Error correctly`() {
        val message = "Error message"
        val networkResult = NetworkResult.Error<String>(message, 400)
        val resource = networkResult.toResource()
        
        assertTrue(resource is Resource.Error)
        assertEquals(message, resource.message)
    }

    @Test
    fun `toResource converts Loading correctly`() {
        val networkResult = NetworkResult.Loading<String>()
        val resource = networkResult.toResource()
        
        assertTrue(resource is Resource.Loading)
    }

    @Test
    fun `toResource handles Error with partial data`() {
        val message = "Error message"
        val data = "Partial data"
        val networkResult = NetworkResult.Error(message, 500, data)
        val resource = networkResult.toResource()
        
        assertTrue(resource is Resource.Error)
        assertEquals(message, resource.message)
        assertEquals(data, resource.data)
    }
}
