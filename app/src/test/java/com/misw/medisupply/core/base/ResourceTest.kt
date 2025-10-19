package com.misw.medisupply.core.base

import org.junit.Test
import org.junit.Assert.*

class ResourceTest {

    @Test
    fun `Success state holds data correctly`() {
        val data = "Test Data"
        val resource = Resource.Success(data)
        
        assertEquals(data, resource.data)
        assertNull(resource.message)
        assertTrue(resource is Resource.Success)
    }

    @Test
    fun `Error state holds message correctly`() {
        val message = "Error occurred"
        val resource = Resource.Error<String>(message)
        
        assertEquals(message, resource.message)
        assertNull(resource.data)
        assertTrue(resource is Resource.Error)
    }

    @Test
    fun `Error state can hold partial data`() {
        val message = "Error occurred"
        val data = "Partial data"
        val resource = Resource.Error(message, data)
        
        assertEquals(message, resource.message)
        assertEquals(data, resource.data)
        assertTrue(resource is Resource.Error)
    }

    @Test
    fun `Loading state with no data`() {
        val resource = Resource.Loading<String>()
        
        assertNull(resource.data)
        assertNull(resource.message)
        assertTrue(resource is Resource.Loading)
    }

    @Test
    fun `Loading state with cached data`() {
        val cachedData = "Cached data"
        val resource = Resource.Loading(cachedData)
        
        assertEquals(cachedData, resource.data)
        assertNull(resource.message)
        assertTrue(resource is Resource.Loading)
    }

    @Test
    fun `different Resource types are distinguishable`() {
        val success: Resource<String> = Resource.Success("data")
        val error: Resource<String> = Resource.Error("error")
        val loading: Resource<String> = Resource.Loading()
        
        assertTrue(success is Resource.Success)
        assertTrue(error is Resource.Error)
        assertTrue(loading is Resource.Loading)
        
        assertFalse(success is Resource.Error<*>)
        assertFalse(success is Resource.Loading<*>)
        assertFalse(error is Resource.Success<*>)
        assertFalse(error is Resource.Loading<*>)
        assertFalse(loading is Resource.Success<*>)
        assertFalse(loading is Resource.Error<*>)
    }

    @Test
    fun `Resource holds different data types correctly`() {
        val intResource = Resource.Success(42)
        val stringResource = Resource.Success("test")
        val listResource = Resource.Success(listOf(1, 2, 3))
        
        assertEquals(42, intResource.data)
        assertEquals("test", stringResource.data)
        assertEquals(listOf(1, 2, 3), listResource.data)
    }

    @Test
    fun `Error message is preserved across instances`() {
        val errorMessage = "Network error"
        val error1 = Resource.Error<String>(errorMessage)
        val error2 = Resource.Error<Int>(errorMessage)
        
        assertEquals(error1.message, error2.message)
    }
}
