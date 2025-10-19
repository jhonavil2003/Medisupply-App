package com.misw.medisupply.core.base

import org.junit.Test
import org.junit.Assert.*

class UiStateTest {

    @Test
    fun `Idle state is singleton`() {
        val idle1 = UiState.Idle
        val idle2 = UiState.Idle
        
        assertSame(idle1, idle2)
        assertTrue(idle1 is UiState.Idle)
    }

    @Test
    fun `Loading state is singleton`() {
        val loading1 = UiState.Loading
        val loading2 = UiState.Loading
        
        assertSame(loading1, loading2)
        assertTrue(loading1 is UiState.Loading)
    }

    @Test
    fun `Success state holds data correctly`() {
        val data = "Test Data"
        val state = UiState.Success(data)
        
        assertEquals(data, state.data)
        assertTrue(state is UiState.Success)
    }

    @Test
    fun `Error state holds message correctly`() {
        val message = "Error occurred"
        val state = UiState.Error(message)
        
        assertEquals(message, state.message)
        assertNull(state.exception)
        assertTrue(state is UiState.Error)
    }

    @Test
    fun `Error state holds message and exception`() {
        val message = "Error occurred"
        val exception = RuntimeException("Test exception")
        val state = UiState.Error(message, exception)
        
        assertEquals(message, state.message)
        assertEquals(exception, state.exception)
    }

    @Test
    fun `isLoading extension returns true for Loading state`() {
        val state: UiState<String> = UiState.Loading
        assertTrue(state.isLoading())
    }

    @Test
    fun `isLoading extension returns false for non-Loading states`() {
        assertFalse(UiState.Idle.isLoading())
        assertFalse(UiState.Success("data").isLoading())
        assertFalse(UiState.Error("error").isLoading())
    }

    @Test
    fun `isSuccess extension returns true for Success state`() {
        val state = UiState.Success("data")
        assertTrue(state.isSuccess())
    }

    @Test
    fun `isSuccess extension returns false for non-Success states`() {
        assertFalse(UiState.Idle.isSuccess())
        assertFalse(UiState.Loading.isSuccess())
        assertFalse(UiState.Error("error").isSuccess())
    }

    @Test
    fun `isError extension returns true for Error state`() {
        val state = UiState.Error("error")
        assertTrue(state.isError())
    }

    @Test
    fun `isError extension returns false for non-Error states`() {
        assertFalse(UiState.Idle.isError())
        assertFalse(UiState.Loading.isError())
        assertFalse(UiState.Success("data").isError())
    }

    @Test
    fun `different UiState types are distinguishable`() {
        val idle: UiState<String> = UiState.Idle
        val loading: UiState<String> = UiState.Loading
        val success: UiState<String> = UiState.Success("data")
        val error: UiState<String> = UiState.Error("error")
        
        assertTrue(idle is UiState.Idle)
        assertTrue(loading is UiState.Loading)
        assertTrue(success is UiState.Success)
        assertTrue(error is UiState.Error)
        
        assertFalse(idle is UiState.Loading)
        assertFalse(idle is UiState.Success<*>)
        assertFalse(idle is UiState.Error)
        
        assertFalse(loading is UiState.Idle)
        assertFalse(loading is UiState.Success<*>)
        assertFalse(loading is UiState.Error)
        
        assertFalse(success is UiState.Idle)
        assertFalse(success is UiState.Loading)
        assertFalse(success is UiState.Error)
        
        assertFalse(error is UiState.Idle)
        assertFalse(error is UiState.Loading)
        assertFalse(error is UiState.Success<*>)
    }

    @Test
    fun `Success state holds different data types correctly`() {
        val intState = UiState.Success(42)
        val stringState = UiState.Success("test")
        val listState = UiState.Success(listOf(1, 2, 3))
        
        assertEquals(42, intState.data)
        assertEquals("test", stringState.data)
        assertEquals(listOf(1, 2, 3), listState.data)
    }
}
