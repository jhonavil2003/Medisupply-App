package com.misw.medisupply

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.misw.medisupply.core.utils.auth.AuthRoleProvider
import com.misw.medisupply.presentation.login.navigation.AppNavHost
import com.misw.medisupply.presentation.login.viewmodel.LoginViewModel
import com.misw.medisupply.ui.theme.MedisupplyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "Login MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: LoginViewModel by lazy { LoginViewModel() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d(TAG, "onCreate: Activity está siendo creada.")

        setContent {
            MedisupplyTheme {
                // MainNavGraph handles role selection and multi-role navigation
                AppNavHost(vm = vm)
            }
        }

        Log.d(TAG, "onCreate: Iniciando la observación del estado del ViewModel.")
        lifecycleScope.launch {
            vm.state.collectLatest { state ->
                // Este log puede ser útil para ver cada cambio de estado, pero puede ser muy verboso.
                // Log.v(TAG, "Nuevo estado de UI recibido: $state")

                if (state.loginSuccess) {
                    Log.i(TAG, "Estado: loginSuccess es true. Inicio de sesión exitoso.")

                    // Obtenemos el rol en un hilo secundario
                    val role = withContext(Dispatchers.IO) {
                        Log.d(TAG, "Obteniendo el rol del usuario...")
                        AuthRoleProvider.getRole()
                    } ?: "guest" // Rol por defecto si es nulo

                    Log.i(TAG, "Rol obtenido: '$role'. Preparando para navegar a HomeActivity.")

                    // Creamos el intent y navegamos
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java).putExtra("ROLE", role))
                    finish() // opcional: cierra la pantalla de login
                }
            }
            Log.d(TAG, "Observación del estado del ViewModel finalizada.")
        }
    }
}