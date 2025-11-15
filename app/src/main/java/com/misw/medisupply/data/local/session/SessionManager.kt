package com.misw.medisupply.data.local.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

/**
 * SessionManager
 * Manages user session data including user_id and session_id for cart operations
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.sessionDataStore
    
    companion object {
        private val SESSION_ID_KEY = stringPreferencesKey("session_id")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }
    
    private var cachedSessionId: String? = null
    private var cachedUserId: String? = null
    
    /**
     * Get or generate a unique session ID
     * Session ID persists across app restarts until explicitly cleared
     */
    suspend fun getSessionId(): String {
        // Return cached value if available
        if (cachedSessionId != null) return cachedSessionId!!
        
        // Try to get from DataStore
        val storedSessionId = dataStore.data.first()[SESSION_ID_KEY]
        
        return if (storedSessionId != null) {
            cachedSessionId = storedSessionId
            storedSessionId
        } else {
            // Generate new session ID
            val newSessionId = "session-${UUID.randomUUID()}"
            dataStore.edit { prefs ->
                prefs[SESSION_ID_KEY] = newSessionId
            }
            cachedSessionId = newSessionId
            newSessionId
        }
    }
    
    /**
     * Get user ID
     * Must be set after login
     */
    suspend fun getUserId(): String {
        // Return cached value if available
        if (cachedUserId != null) return cachedUserId!!
        
        // Try to get from DataStore
        val storedUserId = dataStore.data.first()[USER_ID_KEY]
        
        return if (storedUserId != null) {
            cachedUserId = storedUserId
            storedUserId
        } else {
            // TODO: In production, this should throw an exception
            // For now, generate a temporary user ID for testing
            val tempUserId = "user-${UUID.randomUUID()}"
            setUserId(tempUserId)
            tempUserId
        }
    }
    
    /**
     * Set user ID (called after successful login)
     */
    suspend fun setUserId(userId: String) {
        dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
        cachedUserId = userId
    }
    
    /**
     * Generate a new session ID
     * Call this when starting a new shopping session
     */
    suspend fun renewSessionId(): String {
        val newSessionId = "session-${UUID.randomUUID()}"
        dataStore.edit { prefs ->
            prefs[SESSION_ID_KEY] = newSessionId
        }
        cachedSessionId = newSessionId
        return newSessionId
    }
    
    /**
     * Clear session data
     * Call this on logout to clear session and user IDs
     */
    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(SESSION_ID_KEY)
            prefs.remove(USER_ID_KEY)
        }
        cachedSessionId = null
        cachedUserId = null
    }
    
    /**
     * Clear only the session ID (keep user logged in)
     * Useful for starting a fresh cart without logging out
     */
    suspend fun clearSessionIdOnly() {
        dataStore.edit { prefs ->
            prefs.remove(SESSION_ID_KEY)
        }
        cachedSessionId = null
    }
}
