package com.misw.medisupply.core.utils.auth

import com.misw.medisupply.data.repository.auth.AuthRepository
import com.misw.medisupply.data.repository.auth.AuthRepository.getRoleFromUserAttributes

object AuthRoleProvider {
    /** Intenta primero ID token; si no, fetchUserAttributes */
    suspend fun getRole(): String? {
        val idToken = AuthRepository.getIdToken()
        val roleFromToken = idToken?.let { AuthClaims.extractRoleFromIdToken(it) }
        if (!roleFromToken.isNullOrBlank()) return roleFromToken
        return getRoleFromUserAttributes()
    }
}