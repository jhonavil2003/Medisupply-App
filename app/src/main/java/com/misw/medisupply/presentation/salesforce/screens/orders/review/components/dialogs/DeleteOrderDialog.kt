package com.misw.medisupply.presentation.salesforce.screens.orders.review.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.misw.medisupply.R
import com.misw.medisupply.core.i18n.LocaleManager

/**
 * Delete order confirmation dialog
 * Asks user to confirm before deleting the order
 */
@Composable
fun DeleteOrderDialog(
    orderNumber: String?,
    localeManager: LocaleManager,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = localeManager.getLocalizedString(R.string.delete_order_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = String.format(
                    localeManager.getLocalizedString(R.string.delete_order_message),
                    orderNumber ?: ""
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(localeManager.getLocalizedString(R.string.delete_order_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(localeManager.getLocalizedString(R.string.button_cancel))
            }
        }
    )
}
