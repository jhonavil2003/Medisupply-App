package com.misw.medisupply.core.session

import org.junit.Test
import org.junit.Assert.*

class UserRoleTest {

    @Test
    fun `enum has correct number of values`() {
        val values = UserRole.values()
        assertEquals(2, values.size)
    }

    @Test
    fun `enum contains SALES_FORCE`() {
        val role = UserRole.SALES_FORCE
        assertNotNull(role)
        assertEquals("SALES_FORCE", role.name)
    }

    @Test
    fun `enum contains CUSTOMER_MANAGEMENT`() {
        val role = UserRole.CUSTOMER_MANAGEMENT
        assertNotNull(role)
        assertEquals("CUSTOMER_MANAGEMENT", role.name)
    }

    @Test
    fun `fromString converts salesforce correctly`() {
        assertEquals(UserRole.SALES_FORCE, UserRole.fromString("salesforce"))
        assertEquals(UserRole.SALES_FORCE, UserRole.fromString("SALESFORCE"))
        assertEquals(UserRole.SALES_FORCE, UserRole.fromString("sales_force"))
        assertEquals(UserRole.SALES_FORCE, UserRole.fromString("fuerza_ventas"))
    }

    @Test
    fun `fromString converts customer correctly`() {
        assertEquals(UserRole.CUSTOMER_MANAGEMENT, UserRole.fromString("customer"))
        assertEquals(UserRole.CUSTOMER_MANAGEMENT, UserRole.fromString("CUSTOMER"))
        assertEquals(UserRole.CUSTOMER_MANAGEMENT, UserRole.fromString("customer_management"))
        assertEquals(UserRole.CUSTOMER_MANAGEMENT, UserRole.fromString("cliente"))
    }

    @Test
    fun `fromString defaults to CUSTOMER_MANAGEMENT for unknown values`() {
        assertEquals(UserRole.CUSTOMER_MANAGEMENT, UserRole.fromString("unknown"))
        assertEquals(UserRole.CUSTOMER_MANAGEMENT, UserRole.fromString(""))
        assertEquals(UserRole.CUSTOMER_MANAGEMENT, UserRole.fromString("random"))
    }

    @Test
    fun `getDisplayName returns correct Spanish names`() {
        assertEquals("Fuerza de Ventas", UserRole.SALES_FORCE.getDisplayName())
        assertEquals("Cliente", UserRole.CUSTOMER_MANAGEMENT.getDisplayName())
    }

    @Test
    fun `toRouteString returns correct route strings`() {
        assertEquals("salesforce", UserRole.SALES_FORCE.toRouteString())
        assertEquals("customer", UserRole.CUSTOMER_MANAGEMENT.toRouteString())
    }

    @Test
    fun `valueOf works correctly`() {
        assertEquals(UserRole.SALES_FORCE, UserRole.valueOf("SALES_FORCE"))
        assertEquals(UserRole.CUSTOMER_MANAGEMENT, UserRole.valueOf("CUSTOMER_MANAGEMENT"))
    }

    @Test
    fun `valueOf throws exception for invalid value`() {
        assertThrows(IllegalArgumentException::class.java) {
            UserRole.valueOf("INVALID_ROLE")
        }
    }

    @Test
    fun `enum comparison works correctly`() {
        val role1 = UserRole.SALES_FORCE
        val role2 = UserRole.SALES_FORCE
        val role3 = UserRole.CUSTOMER_MANAGEMENT
        
        assertEquals(role1, role2)
        assertNotEquals(role1, role3)
    }

    @Test
    fun `enum can be used in when expression`() {
        val role = UserRole.SALES_FORCE
        
        val result = when (role) {
            UserRole.SALES_FORCE -> "Sales"
            UserRole.CUSTOMER_MANAGEMENT -> "Customer"
        }
        
        assertEquals("Sales", result)
    }
}
