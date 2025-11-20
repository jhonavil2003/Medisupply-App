package com.misw.medisupply.presentation.salesforce.screens.orders.review.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.misw.medisupply.R
import com.misw.medisupply.presentation.components.localizedStringResource
import com.misw.medisupply.core.i18n.LocaleManager

/**
 * Confirmation dialog
 * Asks user to confirm before creating or updating the order
 */
@Composable
fun ConfirmOrderDialog(
    isEditMode: Boolean = false,
    localeManager: LocaleManager,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = if (isEditMode) localizedStringResource(R.string.update_order_title, localeManager) else localizedStringResource(R.string.confirm_order_title, localeManager),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = if (isEditMode) 
                    localizedStringResource(R.string.confirm_update_order_message, localeManager)
                else 
                    localizedStringResource(R.string.confirm_order_message, localeManager),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(localizedStringResource(R.string.confirm_button, localeManager))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(localizedStringResource(R.string.button_cancel, localeManager))
            }
        }
    )
}
