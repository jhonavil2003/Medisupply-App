package com.misw.medisupply.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * MediSupply Dimension System
*/

object Spacing {
    /** Extra small spacing - 4dp */
    val extraSmall: Dp = 4.dp
    
    /** Small spacing - 8dp (minimum between blocks) */
    val small: Dp = 8.dp
    
    /** Medium spacing - 16dp (standard between blocks) */
    val medium: Dp = 16.dp
    
    /** Large spacing - 24dp */
    val large: Dp = 24.dp
    
    /** Extra large spacing - 32dp */
    val extraLarge: Dp = 32.dp
    
    /** Huge spacing - 48dp */
    val huge: Dp = 48.dp
}

/**
 * Touch Target Sizes
 * Design Style: "Tamaño de toque ≥ 44×44 px"
 * Material Design: Minimum 48x48dp recommended
 */
object TouchTarget {
    /** Minimum touch target size (accessibility requirement) */
    val minimum: Dp = 44.dp
    
    /** Recommended touch target size (Material Design 3) */
    val recommended: Dp = 48.dp
    
    /** Compact touch target size (dense UIs) */
    val compact: Dp = 40.dp
}

/**
 * Button Dimensions
 * Design Style: "Altura: 48 px (compacto 40 px)"
 */
object ButtonSize {
    /** Standard button height */
    val height: Dp = 48.dp
    
    /** Compact button height */
    val heightCompact: Dp = 40.dp
    
    /** Minimum button width for text buttons */
    val minWidth: Dp = 64.dp
    
    /** Horizontal padding inside buttons */
    val horizontalPadding: Dp = 24.dp
    
    /** Horizontal padding for compact buttons */
    val horizontalPaddingCompact: Dp = 16.dp
}

/**
 * Icon Sizes
 * Design Style: "Tamaño iconos: 1.2rem a 1.5rem según contexto"
 * 1rem = 16px, so 1.2rem = 19.2px ≈ 20dp, 1.5rem = 24px
 */
object IconSize {
    /** Small icons - 16dp */
    val small: Dp = 16.dp
    
    /** Medium icons - 20dp (1.2rem) */
    val medium: Dp = 20.dp
    
    /** Standard icons - 24dp (1.5rem, default Material) */
    val standard: Dp = 24.dp
    
    /** Large icons - 28dp (navbar icons as implemented) */
    val large: Dp = 28.dp
    
    /** Extra large icons - 32dp */
    val extraLarge: Dp = 32.dp
    
    /** App bar icons */
    val appBar: Dp = 24.dp
    
    /** Navigation bar icons */
    val navigationBar: Dp = 28.dp
    
    /** FAB icons */
    val fab: Dp = 24.dp
}

/**
 * Border and Stroke Widths
 */
object BorderWidth {
    /** Thin border - 0.5dp */
    val thin: Dp = 0.5.dp
    
    /** Standard border - 1dp */
    val standard: Dp = 1.dp
    
    /** Thick border - 2dp */
    val thick: Dp = 2.dp
    
    /** Focus ring - 3dp (Design Style specification) */
    val focusRing: Dp = 3.dp
}

/**
 * Corner Radius
 * Design Style: "Radio: 12–16 px"
 */
object CornerRadius {
    /** None - 0dp */
    val none: Dp = 0.dp
    
    /** Small - 4dp (chips, tags) */
    val small: Dp = 4.dp
    
    /** Medium - 8dp (inputs) */
    val medium: Dp = 8.dp
    
    /** Large - 12dp (buttons, cards) */
    val large: Dp = 12.dp
    
    /** Extra large - 16dp (prominent components) */
    val extraLarge: Dp = 16.dp
    
    /** Huge - 20dp (modals, dialogs) */
    val huge: Dp = 20.dp
    
    /** Full - 50% (circular buttons) */
    val full: Dp = 999.dp
}

/**
 * Elevation
 * Material Design 3 elevation system
 */
object Elevation {
    /** Level 0 - No elevation */
    val level0: Dp = 0.dp
    
    /** Level 1 - 1dp (subtle lift) */
    val level1: Dp = 1.dp
    
    /** Level 2 - 3dp (cards) */
    val level2: Dp = 3.dp
    
    /** Level 3 - 6dp (floating elements) */
    val level3: Dp = 6.dp
    
    /** Level 4 - 8dp (dialogs) */
    val level4: Dp = 8.dp
    
    /** Level 5 - 12dp (navigation drawer) */
    val level5: Dp = 12.dp
}

/**
 * Input Field Dimensions
 */
object InputSize {
    /** Standard input height */
    val height: Dp = 56.dp
    
    /** Compact input height */
    val heightCompact: Dp = 48.dp
    
    /** Horizontal padding */
    val horizontalPadding: Dp = 16.dp
    
    /** Vertical padding */
    val verticalPadding: Dp = 12.dp
}

/**
 * Card Dimensions
 */
object CardSize {
    /** Minimum card height */
    val minHeight: Dp = 72.dp
    
    /** Standard padding inside cards */
    val padding: Dp = 16.dp
    
    /** Compact padding */
    val paddingCompact: Dp = 12.dp
}

/**
 * Navigation Bar Dimensions
 */
object NavBarSize {
    /** Standard navigation bar height */
    val height: Dp = 80.dp
    
    /** Compact navigation bar height */
    val heightCompact: Dp = 64.dp
    
    /** Icon size in navigation bar */
    val iconSize: Dp = IconSize.navigationBar
}

/**
 * App Bar Dimensions
 */
object AppBarSize {
    /** Standard top app bar height */
    val height: Dp = 64.dp
    
    /** Compact top app bar height */
    val heightCompact: Dp = 56.dp
    
    /** Icon size in app bar */
    val iconSize: Dp = IconSize.appBar
}

/**
 * List Item Dimensions
 */
object ListItemSize {
    /** One line list item height */
    val heightOneLine: Dp = 56.dp
    
    /** Two line list item height */
    val heightTwoLine: Dp = 72.dp
    
    /** Three line list item height */
    val heightThreeLine: Dp = 88.dp
    
    /** Horizontal padding */
    val horizontalPadding: Dp = 16.dp
    
    /** Vertical padding */
    val verticalPadding: Dp = 8.dp
}

/**
 * Chip Dimensions
 */
object ChipSize {
    /** Standard chip height */
    val height: Dp = 32.dp
    
    /** Horizontal padding */
    val horizontalPadding: Dp = 12.dp
    
    /** Icon size in chip */
    val iconSize: Dp = 18.dp
}

/**
 * Dialog Dimensions
 */
object DialogSize {
    /** Maximum width for dialogs on tablets */
    val maxWidth: Dp = 560.dp
    
    /** Padding inside dialogs */
    val padding: Dp = 24.dp
    
    /** Space between title and content */
    val titleSpacing: Dp = 16.dp
    
    /** Space between content and actions */
    val actionSpacing: Dp = 24.dp
}

/**
 * Animation Durations
 * Design Style: "Transiciones suaves en hover y focus (0.2s)"
 */
object AnimationDuration {
    /** Quick animations - 100ms */
    const val quick: Int = 100
    
    /** Standard animations - 200ms (Design Style spec) */
    const val standard: Int = 200
    
    /** Medium animations - 300ms */
    const val medium: Int = 300
    
    /** Slow animations - 500ms */
    const val slow: Int = 500
}
