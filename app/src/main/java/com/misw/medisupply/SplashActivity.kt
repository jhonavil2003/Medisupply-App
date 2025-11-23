package com.misw.medisupply

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.misw.medisupply.core.utils.auth.AuthRoleProvider
import com.misw.medisupply.core.utils.auth.isUserSignedIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log

private const val TAG = "Login Initial Page"

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            Log.d(TAG, "Iniciando verificación de sesión de usuario...")
            val signedIn = withContext(Dispatchers.IO) { isUserSignedIn() }

            Log.d(TAG, "Usuario autenticado: $signedIn")

            if (signedIn) {
                Log.d(TAG, "Usuario autenticado. Obteniendo rol...")
                val role = withContext(Dispatchers.IO) { AuthRoleProvider.getRole() } ?: "guest"
                Log.d(TAG, "Rol obtenido: $role. Navegando a HomeActivity...")
                startActivity(Intent(this@SplashActivity, HomeActivity::class.java).putExtra("ROLE", role))
            } else {
                Log.d(TAG, "Usuario no autenticado. Navegando a MainActivity (pantalla de login)...")
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            finish() // Cierra la SplashActivity para que no se pueda volver a ella
        }
    }
}
