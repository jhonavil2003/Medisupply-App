package com.misw.medisupply.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * MediSupply Color System
 */

// ============================================================================
// BRAND COLORS (Colores de marca y funcionales)
// ============================================================================

/** Primary brand color */
val ColorPrimary = Color(0xFF005DB7)  // rgba(0, 93, 183, 1)

/** Secondary brand color */
val ColorSecondary = Color(0xFF4B6173)  // rgba(75, 97, 115, 1)

/** Tertiary brand color */
val ColorTertiary = Color(0xff008678)  // rgba(0, 107, 95, 1)

/** Error color */
val ColorError = Color(0xFFB4271F)  // rgba(180, 39, 31, 1)

// ============================================================================
// NEUTRAL COLORS (Colores neutros y de texto)
// ============================================================================

/** Pure white */
val ColorWhite = Color(0xFFFFFFFF)  // rgba(255, 255, 255, 1)

/** Pure black */
val ColorBlack = Color(0xFF000000)  // rgba(0, 0, 0, 1)

/** Background color  */
val ColorBackground = Color(0xFFF9F9FF)  // rgba(249, 249, 255, 1)

/** Surface color  */
val ColorSurface = Color(0xFFFCF8F8)  // rgba(252, 248, 248, 1)

/** Text on surface */
val ColorTextOnSurface = Color(0xFF1C1B1C)  // rgba(28, 27, 28, 1)

/** Text on primary */
val ColorTextOnPrimary = Color(0xFFFFFFFF)  // rgba(255, 255, 255, 1)

/** Outline color */
val ColorOutline = Color(0xFF73787B)  // rgba(115, 120, 123, 1)

// ============================================================================
// LIGHT THEME COLORS (Tema Claro)
// ============================================================================

/** Primary color for light theme */
val ColorPrimaryLight = Color(0xFF004D99)  // rgba(0, 77, 153, 1)

/** Text on primary for light theme */
val ColorOnPrimaryLight = Color(0xFFFFFFFF)  // rgba(255, 255, 255, 1)

/** Background for light theme */
val ColorBackgroundLight = Color(0xFFF9F9FF)  // rgba(249, 249, 255, 1)

/** Text on background for light theme */
val ColorOnBackgroundLight = Color(0xFF191C21)  // rgba(25, 28, 33, 1)

/** Surface for light theme */
val ColorSurfaceLight = Color(0xFFFCF8F8)  // rgba(252, 248, 248, 1)

/** Text on surface for light theme */
val ColorOnSurfaceLight = Color(0xFF1C1B1C)  // rgba(28, 27, 28, 1)

/** Outline for light theme */
val ColorOutlineLight = Color(0xFF73787B)  // rgba(115, 120, 123, 1)

// ============================================================================
// EXTENDED COLORS (Colores adicionales del sistema)
// ============================================================================

/** Accent color - Bright blue for CTAs and interactive elements */
val ColorAccent = Color(0xFF2B9AF3)  // rgba(43, 154, 243, 1) - From design justification

/** Primary dark variant - Deeper blue */
val ColorPrimaryDark = Color(0xFF1E63A8)  // rgba(30, 99, 168, 1) - From design justification

/** Success color - Green for positive states */
val ColorSuccess = Color(0xFF16A34A)  // rgba(22, 163, 74, 1)

/** Warning color - Amber for caution states */
val ColorWarning = Color(0xFFF59E0B)  // rgba(245, 158, 11, 1)

/** Surface variant - Subtle blue-gray */
val ColorSurfaceVariant = Color(0xFFF7FAFC)  // rgba(247, 250, 252, 1)

/** Border color - Light blue-gray */
val ColorBorder = Color(0xFFE6EEF9)  // rgba(230, 238, 249, 1)

/** Text secondary - Medium gray for less important text */
val ColorTextSecondary = Color(0xFF6B7280)  // rgba(107, 114, 128, 1)

/** Text primary - Very dark blue-gray */
val ColorTextPrimary = Color(0xFF0F1724)  // rgba(15, 23, 36, 1)

// ============================================================================
// BUTTON COLORS
// ============================================================================

/** Primary button background */
val ButtonPrimaryBg = Color(0xffdae5ff)  // --btn-primary-bg

/** Primary button foreground/text */
val ButtonPrimaryFg = Color(0xFFFFFFFF)  // --btn-primary-fg

/** Ghost/Outlined button background */
val ButtonGhostBg = Color(0xFFFFFFFF)  // --btn-ghost-bg

/** Ghost/Outlined button border */
val ButtonGhostBorder = Color(0xFFE6EEF9)  // --btn-ghost-bd

/** Ghost/Outlined button text */
val ButtonGhostFg = Color(0xFF0B3A66)  // --btn-ghost-fg

/** Danger/Destructive button background */
val ButtonDangerBg = Color(0xFFDC2626)  // --btn-danger-bg

/** Danger/Destructive button text */
val ButtonDangerFg = Color(0xFFFFFFFF)  // --btn-danger-fg

// ============================================================================
// FOCUS AND INTERACTION STATES
// ============================================================================

/** Focus ring color - Accessible blue with transparency */
val FocusRing = Color(0x522B9AF3)  // rgba(43, 154, 243, 0.32) - 3px ring

/** Hover overlay - Subtle darkening */
val HoverOverlay = Color(0x14000000)  // rgba(0, 0, 0, 0.08)

/** Pressed overlay - More prominent darkening */
val PressedOverlay = Color(0x1F000000)  // rgba(0, 0, 0, 0.12)

// ============================================================================
// NAVIGATION BAR (Configuración específica del navbar)
// ============================================================================

/** Navigation bar background */
val NavBarBackground = Color(0xFFECF0FF)

/** Navigation bar icon blue - for Inicio and Pedidos */
val NavBarIconBlue = Color(0xFF1565C0)  // rgba(21, 101, 192, 1)

/** Navigation bar icon green - for Visitas and Cuenta */
val NavBarIconGreen = Color(0xFF008678)  // rgba(0, 134, 120, 1)

// ============================================================================
// STATUS COLORS (Para chips y estados)
// ============================================================================

/** Stock available - Success state */
val StatusStockAvailable = Color(0xFF16A34A)

/** Stock unavailable - Error state */
val StatusStockUnavailable = Color(0xFFDC2626)

/** Order pending - Warning state */
val StatusOrderPending = Color(0xFFF59E0B)

/** Order confirmed - Success state */
val StatusOrderConfirmed = Color(0xFF16A34A)

/** Visit scheduled - Info state */
val StatusVisitScheduled = Color(0xFF2B9AF3)

/** Visit completed - Success state */
val StatusVisitCompleted = Color(0xFF16A34A)

// ============================================================================
// LEGACY COLORS (Material Theme defaults)
// ============================================================================

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val bgTertiary = Color(0xffb4fff1)

val primaryLight = Color(0xFF405F90)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFD6E3FF)
val onPrimaryContainerLight = Color(0xFF274777)
val secondaryLight = Color(0xFF28638A)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFCAE6FF)
val onSecondaryContainerLight = Color(0xFF004B70)
val tertiaryLight = Color(0xFF006B5F)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFF9EF2E3)
val onTertiaryContainerLight = Color(0xFF005048)
val errorLight = Color(0xFF904A42)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD5)
val onErrorContainerLight = Color(0xFF73342C)
val backgroundLight = Color(0xFFF9F9FF)
val onBackgroundLight = Color(0xFF191C20)
val surfaceLight = Color(0xFFF7F9FF)
val onSurfaceLight = Color(0xFF181C20)
val surfaceVariantLight = Color(0xFFDCE3E9)
val onSurfaceVariantLight = Color(0xFF40484C)
val outlineLight = Color(0xFF70787D)
val outlineVariantLight = Color(0xFFC0C8CD)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2D3135)
val inverseOnSurfaceLight = Color(0xFFEEF1F6)
val inversePrimaryLight = Color(0xFFA9C7FF)
val surfaceDimLight = Color(0xFFD7DADF)
val surfaceBrightLight = Color(0xFFF7F9FF)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF1F4F9)
val surfaceContainerLight = Color(0xFFEBEEF3)
val surfaceContainerHighLight = Color(0xFFE6E8EE)
val surfaceContainerHighestLight = Color(0xFFE0E2E8)

val primaryLightMediumContrast = Color(0xFF123665)
val onPrimaryLightMediumContrast = Color(0xFFFFFFFF)
val primaryContainerLightMediumContrast = Color(0xFF4F6EA0)
val onPrimaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val secondaryLightMediumContrast = Color(0xFF003A58)
val onSecondaryLightMediumContrast = Color(0xFFFFFFFF)
val secondaryContainerLightMediumContrast = Color(0xFF3A7299)
val onSecondaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val tertiaryLightMediumContrast = Color(0xFF003E37)
val onTertiaryLightMediumContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightMediumContrast = Color(0xFF1E7A6E)
val onTertiaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val errorLightMediumContrast = Color(0xFF5E241D)
val onErrorLightMediumContrast = Color(0xFFFFFFFF)
val errorContainerLightMediumContrast = Color(0xFFA2594F)
val onErrorContainerLightMediumContrast = Color(0xFFFFFFFF)
val backgroundLightMediumContrast = Color(0xFFF9F9FF)
val onBackgroundLightMediumContrast = Color(0xFF191C20)
val surfaceLightMediumContrast = Color(0xFFF7F9FF)
val onSurfaceLightMediumContrast = Color(0xFF0E1215)
val surfaceVariantLightMediumContrast = Color(0xFFDCE3E9)
val onSurfaceVariantLightMediumContrast = Color(0xFF30373C)
val outlineLightMediumContrast = Color(0xFF4C5458)
val outlineVariantLightMediumContrast = Color(0xFF666E73)
val scrimLightMediumContrast = Color(0xFF000000)
val inverseSurfaceLightMediumContrast = Color(0xFF2D3135)
val inverseOnSurfaceLightMediumContrast = Color(0xFFEEF1F6)
val inversePrimaryLightMediumContrast = Color(0xFFA9C7FF)
val surfaceDimLightMediumContrast = Color(0xFFC4C7CC)
val surfaceBrightLightMediumContrast = Color(0xFFF7F9FF)
val surfaceContainerLowestLightMediumContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightMediumContrast = Color(0xFFF1F4F9)
val surfaceContainerLightMediumContrast = Color(0xFFE6E8EE)
val surfaceContainerHighLightMediumContrast = Color(0xFFDADDE2)
val surfaceContainerHighestLightMediumContrast = Color(0xFFCFD2D7)

val primaryLightHighContrast = Color(0xFF022B5B)
val onPrimaryLightHighContrast = Color(0xFFFFFFFF)
val primaryContainerLightHighContrast = Color(0xFF29497A)
val onPrimaryContainerLightHighContrast = Color(0xFFFFFFFF)
val secondaryLightHighContrast = Color(0xFF002F49)
val onSecondaryLightHighContrast = Color(0xFFFFFFFF)
val secondaryContainerLightHighContrast = Color(0xFF044E73)
val onSecondaryContainerLightHighContrast = Color(0xFFFFFFFF)
val tertiaryLightHighContrast = Color(0xFF00332D)
val onTertiaryLightHighContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightHighContrast = Color(0xFF00534A)
val onTertiaryContainerLightHighContrast = Color(0xFFFFFFFF)
val errorLightHighContrast = Color(0xFF511A14)
val onErrorLightHighContrast = Color(0xFFFFFFFF)
val errorContainerLightHighContrast = Color(0xFF76362E)
val onErrorContainerLightHighContrast = Color(0xFFFFFFFF)
val backgroundLightHighContrast = Color(0xFFF9F9FF)
val onBackgroundLightHighContrast = Color(0xFF191C20)
val surfaceLightHighContrast = Color(0xFFF7F9FF)
val onSurfaceLightHighContrast = Color(0xFF000000)
val surfaceVariantLightHighContrast = Color(0xFFDCE3E9)
val onSurfaceVariantLightHighContrast = Color(0xFF000000)
val outlineLightHighContrast = Color(0xFF262D31)
val outlineVariantLightHighContrast = Color(0xFF434A4F)
val scrimLightHighContrast = Color(0xFF000000)
val inverseSurfaceLightHighContrast = Color(0xFF2D3135)
val inverseOnSurfaceLightHighContrast = Color(0xFFFFFFFF)
val inversePrimaryLightHighContrast = Color(0xFFA9C7FF)
val surfaceDimLightHighContrast = Color(0xFFB6B9BE)
val surfaceBrightLightHighContrast = Color(0xFFF7F9FF)
val surfaceContainerLowestLightHighContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightHighContrast = Color(0xFFEEF1F6)
val surfaceContainerLightHighContrast = Color(0xFFE0E2E8)
val surfaceContainerHighLightHighContrast = Color(0xFFD2D4DA)
val surfaceContainerHighestLightHighContrast = Color(0xFFC4C7CC)

val primaryDark = Color(0xFFA9C7FF)
val onPrimaryDark = Color(0xFF08305F)
val primaryContainerDark = Color(0xFF274777)
val onPrimaryContainerDark = Color(0xFFD6E3FF)
val secondaryDark = Color(0xFF96CCF8)
val onSecondaryDark = Color(0xFF00344F)
val secondaryContainerDark = Color(0xFF004B70)
val onSecondaryContainerDark = Color(0xFFCAE6FF)
val tertiaryDark = Color(0xFF82D5C7)
val onTertiaryDark = Color(0xFF003731)
val tertiaryContainerDark = Color(0xFF005048)
val onTertiaryContainerDark = Color(0xFF9EF2E3)
val errorDark = Color(0xFFFFB4AA)
val onErrorDark = Color(0xFF561E18)
val errorContainerDark = Color(0xFF73342C)
val onErrorContainerDark = Color(0xFFFFDAD5)
val backgroundDark = Color(0xFF111318)
val onBackgroundDark = Color(0xFFE2E2E9)
val surfaceDark = Color(0xFF101418)
val onSurfaceDark = Color(0xFFE0E2E8)
val surfaceVariantDark = Color(0xFF40484C)
val onSurfaceVariantDark = Color(0xFFC0C8CD)
val outlineDark = Color(0xFF8A9297)
val outlineVariantDark = Color(0xFF40484C)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE0E2E8)
val inverseOnSurfaceDark = Color(0xFF2D3135)
val inversePrimaryDark = Color(0xFF405F90)
val surfaceDimDark = Color(0xFF101418)
val surfaceBrightDark = Color(0xFF363A3E)
val surfaceContainerLowestDark = Color(0xFF0B0F12)
val surfaceContainerLowDark = Color(0xFF181C20)
val surfaceContainerDark = Color(0xFF1C2024)
val surfaceContainerHighDark = Color(0xFF272A2E)
val surfaceContainerHighestDark = Color(0xFF313539)

val primaryDarkMediumContrast = Color(0xFFCCDDFF)
val onPrimaryDarkMediumContrast = Color(0xFF002550)
val primaryContainerDarkMediumContrast = Color(0xFF7391C6)
val onPrimaryContainerDarkMediumContrast = Color(0xFF000000)
val secondaryDarkMediumContrast = Color(0xFFBEE0FF)
val onSecondaryDarkMediumContrast = Color(0xFF00283F)
val secondaryContainerDarkMediumContrast = Color(0xFF6096BF)
val onSecondaryContainerDarkMediumContrast = Color(0xFF000000)
val tertiaryDarkMediumContrast = Color(0xFF98ECDD)
val onTertiaryDarkMediumContrast = Color(0xFF002B26)
val tertiaryContainerDarkMediumContrast = Color(0xFF4A9E92)
val onTertiaryContainerDarkMediumContrast = Color(0xFF000000)
val errorDarkMediumContrast = Color(0xFFFFD2CC)
val onErrorDarkMediumContrast = Color(0xFF48130E)
val errorContainerDarkMediumContrast = Color(0xFFCC7B70)
val onErrorContainerDarkMediumContrast = Color(0xFF000000)
val backgroundDarkMediumContrast = Color(0xFF111318)
val onBackgroundDarkMediumContrast = Color(0xFFE2E2E9)
val surfaceDarkMediumContrast = Color(0xFF101418)
val onSurfaceDarkMediumContrast = Color(0xFFFFFFFF)
val surfaceVariantDarkMediumContrast = Color(0xFF40484C)
val onSurfaceVariantDarkMediumContrast = Color(0xFFD6DDE3)
val outlineDarkMediumContrast = Color(0xFFABB3B8)
val outlineVariantDarkMediumContrast = Color(0xFF8A9196)
val scrimDarkMediumContrast = Color(0xFF000000)
val inverseSurfaceDarkMediumContrast = Color(0xFFE0E2E8)
val inverseOnSurfaceDarkMediumContrast = Color(0xFF272A2E)
val inversePrimaryDarkMediumContrast = Color(0xFF284878)
val surfaceDimDarkMediumContrast = Color(0xFF101418)
val surfaceBrightDarkMediumContrast = Color(0xFF414549)
val surfaceContainerLowestDarkMediumContrast = Color(0xFF05080B)
val surfaceContainerLowDarkMediumContrast = Color(0xFF1A1E22)
val surfaceContainerDarkMediumContrast = Color(0xFF24282C)
val surfaceContainerHighDarkMediumContrast = Color(0xFF2F3337)
val surfaceContainerHighestDarkMediumContrast = Color(0xFF3A3E42)

val primaryDarkHighContrast = Color(0xFFEBF0FF)
val onPrimaryDarkHighContrast = Color(0xFF000000)
val primaryContainerDarkHighContrast = Color(0xFFA5C3FB)
val onPrimaryContainerDarkHighContrast = Color(0xFF000B20)
val secondaryDarkHighContrast = Color(0xFFE5F1FF)
val onSecondaryDarkHighContrast = Color(0xFF000000)
val secondaryContainerDarkHighContrast = Color(0xFF93C8F4)
val onSecondaryContainerDarkHighContrast = Color(0xFF000D18)
val tertiaryDarkHighContrast = Color(0xFFAFFFF0)
val onTertiaryDarkHighContrast = Color(0xFF000000)
val tertiaryContainerDarkHighContrast = Color(0xFF7ED1C3)
val onTertiaryContainerDarkHighContrast = Color(0xFF000E0B)
val errorDarkHighContrast = Color(0xFFFFECE9)
val onErrorDarkHighContrast = Color(0xFF000000)
val errorContainerDarkHighContrast = Color(0xFFFFAEA3)
val onErrorContainerDarkHighContrast = Color(0xFF220000)
val backgroundDarkHighContrast = Color(0xFF111318)
val onBackgroundDarkHighContrast = Color(0xFFE2E2E9)
val surfaceDarkHighContrast = Color(0xFF101418)
val onSurfaceDarkHighContrast = Color(0xFFFFFFFF)
val surfaceVariantDarkHighContrast = Color(0xFF40484C)
val onSurfaceVariantDarkHighContrast = Color(0xFFFFFFFF)
val outlineDarkHighContrast = Color(0xFFEAF1F6)
val outlineVariantDarkHighContrast = Color(0xFFBCC4C9)
val scrimDarkHighContrast = Color(0xFF000000)
val inverseSurfaceDarkHighContrast = Color(0xFFE0E2E8)
val inverseOnSurfaceDarkHighContrast = Color(0xFF000000)
val inversePrimaryDarkHighContrast = Color(0xFF284878)
val surfaceDimDarkHighContrast = Color(0xFF101418)
val surfaceBrightDarkHighContrast = Color(0xFF4D5055)
val surfaceContainerLowestDarkHighContrast = Color(0xFF000000)
val surfaceContainerLowDarkHighContrast = Color(0xFF1C2024)
val surfaceContainerDarkHighContrast = Color(0xFF2D3135)
val surfaceContainerHighDarkHighContrast = Color(0xFF383C40)
val surfaceContainerHighestDarkHighContrast = Color(0xFF43474B)







