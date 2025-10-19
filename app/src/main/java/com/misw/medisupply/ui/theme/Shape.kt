package com.misw.medisupply.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * MediSupply Shape System
 */

val Shapes = Shapes(
    /**
     * Extra Small shapes - For chips, tags, and small components
     */
    extraSmall = RoundedCornerShape(4.dp),
    
    /**
     * Small shapes - For input fields and subtle containers
     */
    small = RoundedCornerShape(8.dp),
    
    /**
     * Medium shapes - For buttons and standard cards
     */
    medium = RoundedCornerShape(12.dp),
    
    /**
     * Large shapes - For prominent components
     * Examples: Bottom sheets, featured cards, navigation drawers
     */
    large = RoundedCornerShape(16.dp),
    
    /**
     * Extra Large shapes - For modals and dialogs
     */
    extraLarge = RoundedCornerShape(20.dp)
)

/**
 * Custom shape extensions for specific use cases
 * These can be used directly when the standard shapes don't fit
 */

/** Shape for bottom navigation bar - top corners only */
val NavBarShape = RoundedCornerShape(
    topStart = 16.dp,
    topEnd = 16.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

/** Shape for top app bar - bottom corners only */
val TopBarShape = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = 0.dp,
    bottomStart = 16.dp,
    bottomEnd = 16.dp
)

/** Shape for floating action button - fully rounded */
val FabShape = RoundedCornerShape(16.dp)

/** Shape for text fields - subtle rounded corners */
val InputFieldShape = RoundedCornerShape(8.dp)

/** Shape for buttons - as specified in Design Style (12-16dp) */
val ButtonShape = RoundedCornerShape(12.dp)

/** Shape for cards - consistent with buttons */
val CardShape = RoundedCornerShape(12.dp)

/** Shape for bottom sheets and modals */
val ModalShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 20.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

/** Shape for dialogs */
val DialogShape = RoundedCornerShape(20.dp)

/** Shape for chips and tags */
val ChipShape = RoundedCornerShape(8.dp)
