package com.misw.medisupply.presentation.salesforce.screens.visits.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.R
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
import com.misw.medisupply.presentation.components.localizedStringResource
import com.misw.medisupply.presentation.salesforce.screens.visits.viewmodel.VisitHomeViewModel

@Composable
fun VisitHomeScreen(
    onNavigateToRegisterVisit: () -> Unit = {},
    onNavigateToVisitRoute: () -> Unit = {},
    onNavigateBack: (() -> Unit)? = null,
    viewModel: VisitHomeViewModel = hiltViewModel()
) {
    // Obtener LocaleManager del ViewModel
    val localeManager = viewModel.localeManager
    val currentLanguage = localeManager.currentLanguage.collectAsState().value
    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = localizedStringResource(R.string.visits_title, localeManager),
                subtitle = localizedStringResource(R.string.sales_force_subtitle, localeManager),
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            VisitOptionCard(
                icon = Icons.Filled.LocationOn,
                avatarBackgroundColor = Color(0xFFD6E3FF),
                iconTint = Color(0xFF3C5BAA),
                title = localizedStringResource(R.string.register_visit_title, localeManager),
                subtitle = localizedStringResource(R.string.register_visit_subtitle, localeManager),
                onClick = onNavigateToRegisterVisit
            )

            VisitOptionCard(
                icon = Icons.Filled.Map,
                avatarBackgroundColor = Color(0xFFB4FFF1),
                iconTint = Color(0xFF008678),
                title = localizedStringResource(R.string.visit_route_title, localeManager),
                subtitle = localizedStringResource(R.string.visit_route_subtitle, localeManager),
                onClick = onNavigateToVisitRoute
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VisitOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    avatarBackgroundColor: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(avatarBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(28.dp),
                    tint = iconTint
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}