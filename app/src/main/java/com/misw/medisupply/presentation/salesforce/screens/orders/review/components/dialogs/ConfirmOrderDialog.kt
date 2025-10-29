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

/**
 * Confirmation dialog
 * Asks user to confirm before creating or updating the order
 */
@Composable
fun ConfirmOrderDialog(
    isEditMode: Boolean = false,
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
                text = if (isEditMode) "Actualizar Pedido" else "Confirmar Pedido",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = if (isEditMode) 
                    "¿Está seguro que desea actualizar este pedido? Una vez confirmada, se procesará inmediatamente." 
                else 
                    "¿Está seguro que desea crear este pedido? Una vez confirmado, se procesará inmediatamente.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
