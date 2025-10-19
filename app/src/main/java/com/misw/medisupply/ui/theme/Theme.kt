package com.misw.medisupply.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * MediSupply Design System - Theme Configuration
 */
private val MediSupplyLightColorScheme = lightColorScheme(
    // Primary colors - Professional healthcare blue
    primary = ColorPrimaryLight,              // #004D99 - Main brand color
    onPrimary = ColorOnPrimaryLight,          // #FFFFFF - Text on primary
    primaryContainer = ColorAccent,           // #2B9AF3 - Lighter container
    onPrimaryContainer = ColorTextPrimary,    // #0F1724 - Text on container
    
    // Secondary colors - Neutral professional
    secondary = ColorSecondary,               // #4B6173 - Supporting brand color
    onSecondary = ColorWhite,                 // #FFFFFF - Text on secondary
    secondaryContainer = ColorSurfaceVariant, // #F7FAFC - Light container
    onSecondaryContainer = ColorTextPrimary,  // #0F1724 - Text on container
    
    // Tertiary colors - Healthcare green
    tertiary = ColorTertiary,                 // #006B5F - Accent green
    onTertiary = ColorWhite,                  // #FFFFFF - Text on tertiary
    tertiaryContainer = ColorSuccess,         // #16A34A - Success green
    onTertiaryContainer = ColorTextPrimary,   // #0F1724 - Text on container
    
    // Error colors - Alert states
    error = ColorError,                       // #B4271F - Error red
    onError = ColorWhite,                     // #FFFFFF - Text on error
    errorContainer = ButtonDangerBg,          // #DC2626 - Error container
    onErrorContainer = ColorTextPrimary,      // #0F1724 - Text on error container
    
    // Background colors - App foundation
    background = ColorBackgroundLight,        // #F9F9FF - Main background
    onBackground = ColorOnBackgroundLight,    // #191C21 - Text on background
    
    // Surface colors - Cards and components
    surface = ColorSurfaceLight,              // #FCF8F8 - Surface color
    onSurface = ColorOnSurfaceLight,          // #1C1B1C - Text on surface
    surfaceVariant = ColorSurfaceVariant,     // #F7FAFC - Variant surface
    onSurfaceVariant = ColorTextSecondary,    // #6B7280 - Text on variant
    
    // Outline colors - Borders and dividers
    outline = ColorOutlineLight,              // #73787B - Default outline
    outlineVariant = ColorBorder,             // #E6EEF9 - Subtle borders
    
    // Other colors
    scrim = ColorBlack,                       // #000000 - Modal overlay
    inverseSurface = ColorTextPrimary,        // #0F1724 - Inverse surface
    inverseOnSurface = ColorWhite,            // #FFFFFF - Text on inverse
    inversePrimary = ColorAccent,             // #2B9AF3 - Inverse primary
    
    // Surface containers - For elevation hierarchy
    surfaceDim = ColorSurfaceVariant,         // Dimmed surface
    surfaceBright = ColorWhite,               // Bright surface
    surfaceContainerLowest = ColorWhite,      // Lowest elevation
    surfaceContainerLow = ColorSurfaceVariant,// Low elevation
    surfaceContainer = ColorSurfaceLight,     // Standard elevation
    surfaceContainerHigh = ColorBorder,       // High elevation
    surfaceContainerHighest = ColorOutlineLight, // Highest elevation
)

private val MediSupplyDarkColorScheme = darkColorScheme(
    primary = ColorAccent,                    // #2B9AF3 - Brighter for dark mode
    onPrimary = ColorBlack,                   // #000000 - Text on primary
    primaryContainer = ColorPrimaryDark,      // #1E63A8 - Container
    onPrimaryContainer = ColorWhite,          // #FFFFFF - Text on container
    
    secondary = ColorSecondary,
    onSecondary = ColorWhite,
    secondaryContainer = ColorTextPrimary,
    onSecondaryContainer = ColorWhite,
    
    tertiary = ColorTertiary,
    onTertiary = ColorWhite,
    tertiaryContainer = ColorSuccess,
    onTertiaryContainer = ColorWhite,
    
    error = ButtonDangerBg,
    onError = ColorWhite,
    errorContainer = ColorError,
    onErrorContainer = ColorWhite,
    
    background = ColorTextPrimary,
    onBackground = ColorWhite,
    
    surface = ColorBlack,
    onSurface = ColorWhite,
    surfaceVariant = ColorTextPrimary,
    onSurfaceVariant = ColorTextSecondary,
    
    outline = ColorOutline,
    outlineVariant = ColorTextSecondary,
)
@Composable
fun MedisupplyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,  // Deshabilitado por defecto para mantener identidad de marca
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Dynamic color solo si el usuario lo habilita explícitamente
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Tema oscuro de MediSupply
        darkTheme -> MediSupplyDarkColorScheme
        // Tema claro de MediSupply (por defecto)
        else -> MediSupplyLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}