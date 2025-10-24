package com.misw.medisupply.presentation.salesforce.screens.visits

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
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
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
    onNavigateBack: (() -> Unit)? = null
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
                    VisitListItem(visit = visit, onEdit = { onEditVisit(visit) })
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

@Composable
private fun VisitListItem(
    visit: MockVisit,
    onEdit: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = visit.customerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Fecha: ${visit.dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Hora: ${visit.dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Button(onClick = onEdit) {
                Text("Editar")
            }
        }
    }
}
