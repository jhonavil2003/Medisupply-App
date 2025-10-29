package com.misw.medisupply.presentation.salesforce.screens.visits.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
import com.misw.medisupply.presentation.salesforce.screens.visits.components.VisitCard
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Modelo de datos mockeado
 data class MockVisit(
    val id: Int,
    val customerName: String,
    val dateTime: LocalDateTime
)

private val mockVisits = listOf(
    MockVisit(1, "Clínica Santa Fe", LocalDateTime.of(2025, 10, 24, 9, 30)),
    MockVisit(2, "Hospital San José", LocalDateTime.of(2025, 10, 24, 11, 0)),
    MockVisit(3, "IPS Salud Total", LocalDateTime.of(2025, 10, 25, 14, 15)),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitListScreen(
    onEditVisit: (MockVisit) -> Unit = {},
    onNavigateBack: (() -> Unit)? = null,
    onNavigateToCreateVisit: (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = "Registrar visita",
                subtitle = "Visitas - Medisupply",
                onNavigateBack = {
                    onNavigateBack?.invoke()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToCreateVisit?.invoke() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear visita"
                )
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            CustomerSearchBar()
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(mockVisits) { visit ->
                    VisitCard(visit = visit, onEdit = { onEditVisit(visit) })
                }
            }
        }
    }
}

@Composable
private fun CustomerSearchBar(modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = modifier
            .fillMaxWidth(),
        placeholder = { Text("Buscar cliente...") },
        enabled = false,
        singleLine = true
    )
}