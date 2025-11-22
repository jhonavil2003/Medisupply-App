package com.misw.medisupply.presentation.roleselection

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.R
import com.misw.medisupply.core.session.UserRole
import com.misw.medisupply.presentation.components.CompactLanguageToggle
import com.misw.medisupply.presentation.components.localizedStringResource

/**
 * Role Selection Screen
 * Allows user to choose between Sales Force or Customer Management role
 */
@Composable
fun RoleSelectionScreen(
    onRoleSelected: (UserRole) -> Unit,
    onRegisterClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: RoleSelectionViewModel = hiltViewModel()
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Language toggle button in top-right corner
            CompactLanguageToggle(
                localeManager = viewModel.localeManager,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 32.dp, end = 16.dp)
            )
            
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
            // App Logo or Title
            Text(
                text = localizedStringResource(R.string.app_name, viewModel.localeManager),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = localizedStringResource(R.string.role_selection_title, viewModel.localeManager),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 48.dp)
            )
            
            // Sales Force Card
            RoleCard(
                title = localizedStringResource(R.string.role_sales_force_title, viewModel.localeManager),
                description = localizedStringResource(R.string.role_sales_force_description, viewModel.localeManager),
                icon = Icons.Filled.Business,
                onClick = { onRoleSelected(UserRole.SALES_FORCE) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Customer Management Card
            RoleCard(
                title = localizedStringResource(R.string.role_customer_title, viewModel.localeManager),
                description = localizedStringResource(R.string.role_customer_description, viewModel.localeManager),
                icon = Icons.Filled.Person,
                onClick = { onRoleSelected(UserRole.CUSTOMER_MANAGEMENT) },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Register Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${localizedStringResource(R.string.register_new_customer, viewModel.localeManager)} ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(
                    onClick = onRegisterClick
                ) {
                    Text(
                        text = localizedStringResource(R.string.register_button, viewModel.localeManager),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Help text
            Text(
                text = localizedStringResource(R.string.help_text, viewModel.localeManager),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        }
    }
}

/**
 * Role selection card component
 */
@Composable
private fun RoleCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
