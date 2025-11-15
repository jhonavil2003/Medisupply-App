package com.misw.medisupply.core.session

import com.misw.medisupply.domain.model.salesperson.Salesperson
import com.misw.medisupply.domain.usecase.cart.ClearCartUseCase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages user session including role and salesperson information
 */
@Singleton
class UserSessionManager @Inject constructor(
    private val clearCartUseCase: ClearCartUseCase
) {
    
    private val _currentSalesperson = MutableStateFlow<Salesperson?>(null)
    val currentSalesperson: StateFlow<Salesperson?> = _currentSalesperson.asStateFlow()
    
    private val _currentRole = MutableStateFlow<UserRole?>(null)
    val currentRole: StateFlow<UserRole?> = _currentRole.asStateFlow()
    
    init {
        // TODO: En el futuro, cargar de SharedPreferences o sistema de autenticación
        // Por ahora, simulamos un vendedor logueado
        initDefaultSalesperson()
    }
    
    /**
     * Initialize with default salesperson for demo purposes
     * TODO: Replace with real authentication system
     */
    private fun initDefaultSalesperson() {
        // Lista de vendedores que EXISTEN en la base de datos (IDs: 2, 6, 7, 8, 9, 10)
        val availableSalespersons = listOf(
            Salesperson(id = 2, firstName = "Test", lastName = "Vendedor", email = "test.vendedor@medisupply.com", phone = "+57 300 000 0002", territory = "Test - Pruebas"),
            Salesperson(id = 6, firstName = "Ana", lastName = "Rodríguez", email = "ana.rodriguez@medisupply.com", phone = "+57 300 123 4567", territory = "Bogotá - Cundinamarca"),
            Salesperson(id = 7, firstName = "Carlos", lastName = "Martínez", email = "carlos.martinez@medisupply.com", phone = "+57 300 234 5678", territory = "Medellín - Antioquia"),
            Salesperson(id = 8, firstName = "Laura", lastName = "González", email = "laura.gonzalez@medisupply.com", phone = "+57 300 345 6789", territory = "Cali - Valle del Cauca"),
            Salesperson(id = 9, firstName = "Diego", lastName = "Torres", email = "diego.torres@medisupply.com", phone = "+57 300 456 7890", territory = "Barranquilla - Atlántico"),
            Salesperson(id = 10, firstName = "Patricia", lastName = "Jiménez", email = "patricia.jimenez@medisupply.com", phone = "+57 300 567 8901", territory = "Bucaramanga - Santander")
        )
        
        // Por defecto usar el vendedor con ID=2 para pruebas
        _currentSalesperson.value = availableSalespersons.first()  // ID=2
    }
    
    /**
     * Set current salesperson (for login/authentication)
     */
    fun setSalesperson(salesperson: Salesperson) {
        _currentSalesperson.value = salesperson
    }
    
    /**
     * Set current role
     */
    fun setRole(role: UserRole) {
        _currentRole.value = role
    }
    
    /**
     * Clear session (logout)
     * Also releases all cart reservations
     */
    fun clearSession() {
        // Clear cart reservations BEFORE clearing session
        // Use GlobalScope since we're in a singleton and need guaranteed execution
        GlobalScope.launch {
            try {
                clearCartUseCase().collect { resource ->
                    // Best effort - don't block logout if it fails
                    println("🧹 Logout: Cart cleanup result = ${resource.javaClass.simpleName}")
                }
            } catch (e: Exception) {
                println("⚠️ Logout: Failed to clear cart reservations: ${e.message}")
                // Don't block logout - backend will auto-expire reservations
            }
        }
        
        // Clear session data
        _currentSalesperson.value = null
        _currentRole.value = null
        
        println("👋 Session cleared - User logged out")
    }
    
    /**
     * Get current salesperson or throw exception
     */
    fun requireSalesperson(): Salesperson {
        return _currentSalesperson.value 
            ?: throw IllegalStateException("No salesperson logged in")
    }
    
    /**
     * Get current role or throw exception
     */
    fun requireRole(): UserRole {
        return _currentRole.value 
            ?: throw IllegalStateException("No role has been set")
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return _currentSalesperson.value != null && _currentRole.value != null
    }
    
    /**
     * Get list of available salespersons from database (IDs: 2, 6, 7, 8, 9, 10)
     * TODO: Replace this method when real authentication is implemented
     */
    fun getAvailableDemoSalespersons(): List<Salesperson> {
        return listOf(
            Salesperson(id = 2, firstName = "Test", lastName = "Vendedor", email = "test.vendedor@medisupply.com", phone = "+57 300 000 0002", territory = "Test - Pruebas"),
            Salesperson(id = 6, firstName = "Ana", lastName = "Rodríguez", email = "ana.rodriguez@medisupply.com", phone = "+57 300 123 4567", territory = "Bogotá - Cundinamarca"),
            Salesperson(id = 7, firstName = "Carlos", lastName = "Martínez", email = "carlos.martinez@medisupply.com", phone = "+57 300 234 5678", territory = "Medellín - Antioquia"),
            Salesperson(id = 8, firstName = "Laura", lastName = "González", email = "laura.gonzalez@medisupply.com", phone = "+57 300 345 6789", territory = "Cali - Valle del Cauca"),
            Salesperson(id = 9, firstName = "Diego", lastName = "Torres", email = "diego.torres@medisupply.com", phone = "+57 300 456 7890", territory = "Barranquilla - Atlántico"),
            Salesperson(id = 10, firstName = "Patricia", lastName = "Jiménez", email = "patricia.jimenez@medisupply.com", phone = "+57 300 567 8901", territory = "Bucaramanga - Santander")
        )
    }
    
    /**
     * Switch to different salesperson for testing
     * TODO: Remove this method when real authentication is implemented
     */
    fun switchToSalesperson(salespersonId: Int) {
        val salesperson = getAvailableDemoSalespersons().find { it.id == salespersonId }
        if (salesperson != null) {
            _currentSalesperson.value = salesperson
        }
    }
}