package com.misw.medisupply.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * MediSupply Typography System
 */

val Typography = Typography(
    // ============================================================================
    // DISPLAY - Large headers (rarely used in mobile, more for tablets/marketing)
    // ============================================================================
    
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,  // Inter/Roboto
        fontWeight = FontWeight.Normal,    // 400
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    
    // ============================================================================
    // HEADLINE - Section headers
    // ============================================================================
    
    /**
     * Large section headers
     * Example: Screen titles, major sections
     */
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,    // 500
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    
    /**
     * Medium section headers
     * Example: Section dividers, category headers
     */
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,    // 500
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    
    /**
     * Small section headers
     * Example: Sub-sections, grouped content headers
     */
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,    // 500
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    
    // ============================================================================
    // TITLE - Component titles (cards, lists, dialogs)
    // Design Style: "Títulos en cards = title-md Emphasized"
    // ============================================================================
    
    /**
     * Large titles
     * Example: Dialog titles, important card headers
     */
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,    // 500
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    
    /**
     * Medium titles (EMPHASIZED for cards)
     * Example: Card titles, list section headers
     * Design Style: Use fontWeight.SemiBold (600) for emphasis
     */
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,  // 600 - Emphasized
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    
    /**
     * Small titles
     * Example: Compact card titles, list item headers
     */
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,    // 500
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // ============================================================================
    // BODY - Main content text
    // Design Style: "Descripciones = body-md (o body-sm si el espacio es crítico)"
    // Base size: 16sp as specified
    // ============================================================================
    
    /**
     * Large body text (base size)
     * Example: Primary content, descriptions, paragraphs
     * Design Style: 16px base size for optimal mobile readability
     */
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,    // 400
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    
    /**
     * Medium body text
     * Example: Secondary content, list item descriptions
     */
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,    // 400
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    
    /**
     * Small body text
     * Example: Captions, helper text, compact descriptions
     * Design Style: Use when space is critical
     */
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,    // 400
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    
    // ============================================================================
    // LABEL - UI elements (buttons, chips, tabs)
    // ============================================================================
    
    /**
     * Large labels
     * Example: Primary button text, important CTAs
     * Design Style: Use for primary action buttons
     */
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,    // 500
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    /**
     * Medium labels
     * Example: Secondary button text, tabs, chips
     * Design Style: Use for secondary action buttons
     */
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,    // 500
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    
    /**
     * Small labels
     * Example: Status chips, tags, metadata
     * Design Style: "Estados/etiquetas (Stock, Pendiente, Confirmado)"
     */
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,    // 500
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)