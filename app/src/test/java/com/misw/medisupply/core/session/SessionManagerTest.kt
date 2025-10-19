package com.misw.medisupply.core.session

import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class SessionManagerTest {

    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        sessionManager = SessionManager()
    }

    @Test
    fun `initial state has no role`() = runTest {
        assertNull(sessionManager.currentRole.value)
        assertFalse(sessionManager.hasRole())
    }

    @Test
    fun `setRole updates current role`() = runTest {
        sessionManager.setRole(UserRole.SALES_FORCE)
        
        assertEquals(UserRole.SALES_FORCE, sessionManager.currentRole.value)
        assertTrue(sessionManager.hasRole())
    }

    @Test
    fun `setRole can be called multiple times`() = runTest {
        sessionManager.setRole(UserRole.SALES_FORCE)
        assertEquals(UserRole.SALES_FORCE, sessionManager.currentRole.value)
        
        sessionManager.setRole(UserRole.CUSTOMER_MANAGEMENT)
        assertEquals(UserRole.CUSTOMER_MANAGEMENT, sessionManager.currentRole.value)
    }

    @Test
    fun `clearRole removes current role`() = runTest {
        sessionManager.setRole(UserRole.SALES_FORCE)
        assertTrue(sessionManager.hasRole())
        
        sessionManager.clearRole()
        
        assertNull(sessionManager.currentRole.value)
        assertFalse(sessionManager.hasRole())
    }

    @Test
    fun `hasRole returns true when role is set`() = runTest {
        assertFalse(sessionManager.hasRole())
        
        sessionManager.setRole(UserRole.CUSTOMER_MANAGEMENT)
        assertTrue(sessionManager.hasRole())
    }

    @Test
    fun `hasRole returns false when role is cleared`() = runTest {
        sessionManager.setRole(UserRole.SALES_FORCE)
        sessionManager.clearRole()
        
        assertFalse(sessionManager.hasRole())
    }

    @Test
    fun `requireRole returns current role when set`() = runTest {
        sessionManager.setRole(UserRole.SALES_FORCE)
        
        val role = sessionManager.requireRole()
        assertEquals(UserRole.SALES_FORCE, role)
    }

    @Test
    fun `requireRole throws exception when no role is set`() {
        val exception = assertThrows(IllegalStateException::class.java) {
            sessionManager.requireRole()
        }
        
        assertEquals("No role has been set", exception.message)
    }

    @Test
    fun `requireRole throws exception after clearRole`() = runTest {
        sessionManager.setRole(UserRole.CUSTOMER_MANAGEMENT)
        sessionManager.clearRole()
        
        assertThrows(IllegalStateException::class.java) {
            sessionManager.requireRole()
        }
    }

    @Test
    fun `currentRole StateFlow updates correctly`() = runTest {
        val role = UserRole.SALES_FORCE
        sessionManager.setRole(role)
        
        assertEquals(role, sessionManager.currentRole.value)
    }
}
