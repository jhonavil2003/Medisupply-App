package com.misw.medisupply.presentation.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Medisupply XR/XR App Bar Component
 * Reusable Material Design 3 top app bar with title, subtitle, and back navigation
 * 
 * @param title Main title of the screen
 * @param subtitle Secondary text (e.g., "Fuerza de ventas - Medisupply")
 * @param onNavigateBack Callback for back button click, null to hide back button
 * @param modifier Optional modifier for customization
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedisupplyAppBar(
    title: String,
    subtitle: String,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color(0xFFDAE5FF)
    val textColor = Color(0xFF1565C0)
    
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.7f)
                )
            }
        },
        navigationIcon = {
            onNavigateBack?.let { callback ->
                IconButton(onClick = callback) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = textColor
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
            titleContentColor = textColor,
            navigationIconContentColor = textColor
        ),
        modifier = modifier
    )
}
