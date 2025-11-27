package com.misw.medisupply

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.misw.medisupply.presentation.customermanagement.navigation.CustomerManagementNavigation
import com.misw.medisupply.presentation.salesforce.navigation.SalesForceNavigation
import com.misw.medisupply.ui.theme.MedisupplyTheme
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent

private const val TAG = "Login HomeActivity"

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    companion object {
        const val ACTION_LOGOUT = "com.misw.medisupply.LOGOUT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: HomeActivity iniciada.")

        val role = intent.getStringExtra("ROLE") ?: "guest"
        Log.i(TAG, "onCreate: Rol recibido del intent -> '$role'")

        setContent {
            // 3. Envuelve la lógica en el tema de tu aplicación para un estilo consistente.
            MedisupplyTheme {
                // 4. Usa 'when' para decidir qué Composable mostrar, DENTRO de Compose.
                when (role) {
                    "SELLER" -> {
                        Log.d(TAG, "Mostrando UI para rol: seller")
                        // Si el rol es "seller", muestra el grafo de navegación de ventas.
                        SalesForceNavigation()
                    }

                    else -> {
                        Log.d(TAG, "Mostrando UI para rol por defecto (customer/guest): $role")
                        // Para cualquier otro caso ("customer", "guest", etc.), muestra la pantalla de cliente.
                        CustomerManagementNavigation()
                    }
                }
            }
        }
    }
}