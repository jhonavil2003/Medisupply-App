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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.R
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
import com.misw.medisupply.presentation.components.localizedStringResource
import com.misw.medisupply.presentation.salesforce.screens.visits.components.VisitCard
import com.misw.medisupply.presentation.salesforce.screens.visits.viewmodel.VisitListViewModel
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
    onNavigateToCreateVisit: (() -> Unit)? = null,
    viewModel: VisitListViewModel = hiltViewModel()
) {
    // Obtener LocaleManager del ViewModel
    val localeManager = viewModel.localeManager
    val currentLanguage = localeManager.currentLanguage.collectAsState().value
    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = localizedStringResource(R.string.register_visit_title, localeManager),
                subtitle = localizedStringResource(R.string.visit_list_subtitle, localeManager),
                onNavigateBack = {
                    onNavigateBack?.invoke()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToCreateVisit?.invoke() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = localizedStringResource(R.string.create_visit_fab, localeManager)
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
            CustomerSearchBar(localeManager = localeManager)
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
private fun CustomerSearchBar(
    localeManager: com.misw.medisupply.core.i18n.LocaleManager,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = modifier
            .fillMaxWidth(),
        placeholder = { 
            Text(localizedStringResource(R.string.search_customer_placeholder, localeManager))
        },
        enabled = false,
        singleLine = true
    )
}