package com.misw.medisupply.core.session

import android.util.Log
import com.misw.medisupply.data.repository.auth.AuthRepository
import com.misw.medisupply.domain.model.salesperson.Salesperson
import com.misw.medisupply.domain.usecase.cart.ClearCartUseCase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

    private val TAG = "UserSessionManager"

    private val _currentSalesperson = MutableStateFlow<Salesperson?>(null)
    val currentSalesperson: StateFlow<Salesperson?> = _currentSalesperson.asStateFlow()

    private val _currentSalespersonId = MutableStateFlow<String?>(null)
    val currentSalespersonId: StateFlow<String?> = _currentSalespersonId.asStateFlow()

    private val _currentSalespersonSub = MutableStateFlow<String?>(null)
    val currentSalespersonSub: StateFlow<String?> = _currentSalespersonSub.asStateFlow()

    private val _currentRole = MutableStateFlow<UserRole?>(null)
    val currentRole: StateFlow<UserRole?> = _currentRole.asStateFlow()

    init {
        Log.d(TAG, "init - initializing default salesperson")

        GlobalScope.launch {
            try {
                Log.d(TAG, "init - fetching Cognito sub from AuthRepository")
                val userDbId = AuthRepository.getUserDbId()
                if (!userDbId.isNullOrBlank()) {
                    setSalespersonSub(userDbId)
                    Log.d(TAG, "🔐 Cognito sub detected: $userDbId")
                } else {
                    Log.i(TAG, "ℹ️ No se encontró sub en el token")
                }
                val userId = AuthRepository.getUserId()
                if (!userId.isNullOrBlank()) {
                    setSalespersonId(userId)
                    Log.d(TAG, "🔐 Cognito sub detected: $userId")
                } else {
                    Log.i(TAG, "ℹ️ No se encontró sub en el token")
                }
            } catch (e: Exception) {
                Log.e(TAG, "⚠️ Error obteniendo Cognito sub", e)
            }
        }
    }


    /**
     * Set current salesperson (for login/authentication)
     */
    fun setSalespersonId(id: String) {
        _currentSalespersonId.value = id
        Log.d(TAG, "setSalesperson -> id=${id}")
    }

    /**
     * Guarda el `sub` (Cognito user id) sin resolver a Salesperson.
     */
    fun setSalespersonSub(sub: String) {
        _currentSalespersonSub.value = sub
        Log.d(TAG, "setSalespersonSub -> $sub")
    }

    /**
     * Set current role
     */
    fun setRole(role: UserRole) {
        _currentRole.value = role
        Log.d(TAG, "setRole -> $role")
    }

    /**
     * Clear session (logout)
     * Also releases all cart reservations
     */
    fun clearSession() {
        Log.d(TAG, "clearSession - start: clearing cart reservations and session data")
        // Clear cart reservations BEFORE clearing session
        // Use GlobalScope since we're in a singleton and need guaranteed execution
        GlobalScope.launch {
            try {
                clearCartUseCase().collect { resource ->
                    // Best effort - don't block logout if it fails
                    Log.d(TAG, "🧹 Logout: Cart cleanup result = ${resource.javaClass.simpleName}")
                }
            } catch (e: Exception) {
                Log.w(TAG, "⚠️ Logout: Failed to clear cart reservations: ${e.message}", e)
                // Don't block logout - backend will auto-expire reservations
            }
        }

        // Clear session data
        _currentSalesperson.value = null
        _currentRole.value = null
        _currentSalespersonSub.value = null

        Log.i(TAG, "👋 Session cleared - User logged out")
    }

    /**
     * Get current salesperson or throw exception
     */
    fun requireSalesperson(): Salesperson {
        return _currentSalesperson.value
            ?: run {
                Log.e(TAG, "requireSalesperson - no salesperson logged in")
                throw IllegalStateException("No salesperson logged in")
            }
    }

    suspend fun requireSalespersonSub(): Int {
        return currentSalespersonId
            .filterNotNull()                // esperamos a que no sea null
            .map { it.toIntOrNull() }       // lo convertimos a Int?
            .filterNotNull()                // esperamos a que la conversión sea válida
            .first()                        // suspende hasta que haya valor
    }

    suspend fun requireSalespersonSubString(): String {
        return currentSalespersonId
            .filterNotNull()                // esperamos a que no sea null
            .first()                        // suspende hasta que haya valor
    }

    /**
     * Espera hasta tener un salespersonId válido.
     * Debes llamarla SIEMPRE desde una corrutina.
     */
    suspend fun requireSalespersonId(): Int {
        return currentSalespersonSub
            .filterNotNull()                // esperamos a que no sea null
            .map { it.toIntOrNull() }       // lo convertimos a Int?
            .filterNotNull()                // esperamos a que la conversión sea válida
            .first()                        // suspende hasta que haya valor
    }

    /**
     * Get current role or throw exception
     */
    fun requireRole(): UserRole {
        return _currentRole.value
            ?: run {
                Log.e(TAG, "requireRole - no role has been set")
                throw IllegalStateException("No role has been set")
            }
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        val loggedIn = _currentSalesperson.value != null && _currentRole.value != null
        Log.d(TAG, "isLoggedIn -> $loggedIn")
        return loggedIn
    }

    /**
     * Get list of available salespersons from database (IDs: 2, 6, 7, 8, 9, 10)
     * TODO: Replace this method when real authentication is implemented
     */
    fun getAvailableDemoSalespersons(): List<Salesperson> {
        val list = listOf(
            Salesperson(id = 2, firstName = "Test", lastName = "Vendedor", email = "test.vendedor@medisupply.com", phone = "+57 300 000 0002", territory = "Test - Pruebas"),
            Salesperson(id = 6, firstName = "Ana", lastName = "Rodríguez", email = "ana.rodriguez@medisupply.com", phone = "+57 300 123 4567", territory = "Bogotá - Cundinamarca"),
            Salesperson(id = 7, firstName = "Carlos", lastName = "Martínez", email = "carlos.martinez@medisupply.com", phone = "+57 300 234 5678", territory = "Medellín - Antioquia"),
            Salesperson(id = 8, firstName = "Laura", lastName = "González", email = "laura.gonzalez@medisupply.com", phone = "+57 300 345 6789", territory = "Cali - Valle del Cauca"),
            Salesperson(id = 9, firstName = "Diego", lastName = "Torres", email = "diego.torres@medisupply.com", phone = "+57 300 456 7890", territory = "Barranquilla - Atlántico"),
            Salesperson(id = 10, firstName = "Patricia", lastName = "Jiménez", email = "patricia.jimenez@medisupply.com", phone = "+57 300 567 8901", territory = "Bucaramanga - Santander")
        )
        Log.d(TAG, "getAvailableDemoSalespersons - count=${list.size}")
        return list
    }

    /**
     * Switch to different salesperson for testing
     * TODO: Remove this method when real authentication is implemented
     */
    fun switchToSalesperson(salespersonId: Int) {
        val salesperson = getAvailableDemoSalespersons().find { it.id == salespersonId }
        if (salesperson != null) {
            _currentSalesperson.value = salesperson
            Log.d(TAG, "switchToSalesperson -> switched to id=$salespersonId")
        } else {
            Log.w(TAG, "switchToSalesperson -> not found id=$salespersonId")
        }
    }

    fun getSalespersonId (): Int {
        return _currentSalespersonSub.value?.toIntOrNull() ?: 0
    }
}