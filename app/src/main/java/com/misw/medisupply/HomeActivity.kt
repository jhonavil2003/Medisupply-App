package com.misw.medisupply

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.misw.medisupply.presentation.navigation.CustomerNavGraph
import com.misw.medisupply.presentation.navigation.MainNavGraph
import com.misw.medisupply.presentation.navigation.SalesNavGraph
import com.misw.medisupply.presentation.salesforce.navigation.SalesForceNavGraph
import com.misw.medisupply.presentation.salesforce.screens.home.SalesForceHomeScreen
import com.misw.medisupply.ui.theme.MedisupplyTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val role = intent.getStringExtra("ROLE") ?: "guest"
        setContent {
            // 3. Envuelve la lógica en el tema de tu aplicación para un estilo consistente.
            MedisupplyTheme {
                // 4. Usa 'when' para decidir qué Composable mostrar, DENTRO de Compose.
                when (role) {
                    "seller" -> {
                        // Si el rol es "seller", muestra el grafo de navegación de ventas.
                        SalesForceNavGraph()
                    }
                    else -> {
                        // Para cualquier otro caso ("customer", "guest", etc.), muestra la pantalla de cliente.
                        CustomerNavGraph()
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(role: String) {
    Column(Modifier.padding(24.dp)) {
        Text("Bienvenido 👋", style = MaterialTheme.typography.headlineSmall)
        Text("Rol: $role", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun HomeCustomer() {
    Column(Modifier.padding(24.dp)) {
        Text("Bienvenido 👋", style = MaterialTheme.typography.headlineSmall)
        Text("Rol: Soy un cliente", style = MaterialTheme.typography.bodyLarge)
    }
}