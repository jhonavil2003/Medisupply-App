package com.misw.medisupply.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.R
import com.misw.medisupply.core.i18n.LocaleManager

/**
 * Language toggle button component
 * Shows current language and allows switching between Spanish and English
 */
@Composable
fun LanguageToggleButton(
    localeManager: LocaleManager,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    showText: Boolean = true,
    compact: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val currentLanguage by localeManager.currentLanguage.collectAsState()

    Box(modifier = modifier) {
        // Main button
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { 
                    if (compact) {
                        localeManager.toggleLanguage()
                    } else {
                        expanded = !expanded
                    }
                },
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = if (compact) 8.dp else 12.dp,
                    vertical = if (compact) 4.dp else 8.dp
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (showIcon) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = stringResource(R.string.language_toggle_description),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(if (compact) 16.dp else 20.dp)
                    )
                }
                
                if (showText) {
                    Text(
                        text = localeManager.getCurrentLanguageDisplayName(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = if (compact) 11.sp else 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Dropdown menu for non-compact mode
        if (!compact) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(8.dp)
                )
            ) {
                localeManager.getAvailableLanguages().forEach { languageOption ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = languageOption.flag,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = languageOption.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (languageOption.code == currentLanguage) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        },
                        onClick = {
                            localeManager.switchLanguage(languageOption.code)
                            expanded = false
                        },
                        modifier = Modifier.background(
                            if (languageOption.code == currentLanguage) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            } else {
                                Color.Transparent
                            }
                        )
                    )
                }
            }
        }
    }
}

/**
 * Compact version of the language toggle button
 * Just shows flag and toggles on click
 */
@Composable
fun CompactLanguageToggle(
    localeManager: LocaleManager,
    modifier: Modifier = Modifier
) {
    val currentLanguage by localeManager.currentLanguage.collectAsState()
    val languageFlag = when (currentLanguage) {
        LocaleManager.LANG_SPANISH -> "🇪🇸"
        LocaleManager.LANG_ENGLISH -> "🇺🇸"
        else -> "🇪🇸"
    }

    Surface(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { localeManager.toggleLanguage() },
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        tonalElevation = 1.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = languageFlag,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}