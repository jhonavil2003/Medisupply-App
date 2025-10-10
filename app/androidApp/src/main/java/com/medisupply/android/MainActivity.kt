package com.medisupply.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medisupply.android.ui.theme.MedisupplyTheme
import kotlinx.coroutines.*
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedisupplyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BackendMessageScreen()
                }
            }
        }
    }
}

@Composable
fun BackendMessageScreen() {
    var message by remember { mutableStateOf("Cargando...") }

    // Llama al backend cuando se inicie la pantalla
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = URL("http://10.0.2.2:8080/api/hello").readText()
                message = response
            } catch (e: Exception) {
                message = "Error al conectar con el backend: ${e.message}"
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
