package com.misw.medisupply.core.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages user session and role information
 * Provides role state across the application
 */
@Singleton
class SessionManager @Inject constructor() {
    
    private val _currentRole = MutableStateFlow<UserRole?>(null)
    val currentRole: StateFlow<UserRole?> = _currentRole.asStateFlow()
    
    /**
     * Set the current user role
     * @param role UserRole to set as current
     */
    fun setRole(role: UserRole) {
        _currentRole.value = role
    }
    
    /**
     * Clear current role (logout or role change)
     */
    fun clearRole() {
        _currentRole.value = null
    }
    
    /**
     * Check if user has selected a role
     * @return true if role is set
     */
    fun hasRole(): Boolean {
        return _currentRole.value != null
    }
    
    /**
     * Get current role or throw exception
     * @return Current UserRole
     * @throws IllegalStateException if no role is set
     */
    fun requireRole(): UserRole {
        return _currentRole.value 
            ?: throw IllegalStateException("No role has been set")
    }
}
