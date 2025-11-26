package com.misw.medisupply.core.utils.auth

import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject

object AuthClaims {

    /** Decodifica el payload (2da parte) del JWT sin validar firma (solo lectura local) */
    fun decodePayload(jwt: String): JSONObject? = try {
        val payloadB64 = jwt.split(".").getOrNull(1) ?: return null
        val bytes = Base64.decode(payloadB64, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        JSONObject(String(bytes))
    } catch (_: Exception) { null }

    /** Intenta obtener el rol desde el ID token:
     *  1) custom:role (atributo personalizado)
     *  2) cognito:groups (si usas grupos de Cognito) */
    fun extractRoleFromIdToken(idToken: String): String? {
        val json = decodePayload(idToken) ?: return null
        // 1) custom attribute
        json.optString("custom:role")?.let { if (it.isNotBlank()) return it }
        // 2) grupos (opcional)
        (json.opt("cognito:groups") as? JSONArray)?.let { arr ->
            if (arr.length() > 0) return arr.optString(0)
        }
        return null
    }
}