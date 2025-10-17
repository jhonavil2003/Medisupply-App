package com.misw.medisupply.presentation.salesforce.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.misw.medisupply.ui.theme.ButtonPrimaryBg
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderStatus
import java.text.SimpleDateFormat
import java.util.Locale
import com.misw.medisupply.ui.theme.ColorTertiary
import com.misw.medisupply.ui.theme.bgTertiary
import com.misw.medisupply.ui.theme.ColorPrimaryLight

/**
 * Card component to display order information in compact horizontal layout
 */
@Composable
fun OrderCard(
    order: Order,
    onDetailClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgTertiary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = ColorTertiary,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Order info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${order.orderNumber} - ${order.deliveryCity}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Color(0xFF212121)
                )
                Text(
                    text = "${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(order.orderDate)} Estado: ${getStatusText(order.status)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = Color(0xff8d9194)
                )
                Text(
                    text = "Total: ${order.getFormattedTotal()}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = Color(0xff8d9194)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Action button
            val buttonText = if (order.status == OrderStatus.CONFIRMED) "Detalle" else "Editar"
            val buttonAction = if (order.status == OrderStatus.CONFIRMED) onDetailClick else onEditClick
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ButtonPrimaryBg)
            ) {
                TextButton(
                    onClick = buttonAction,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = buttonText,
                        color = ColorPrimaryLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Get status text in Spanish
 */
private fun getStatusText(status: OrderStatus): String {
    return when (status) {
        OrderStatus.PENDING -> "Pendiente"
        OrderStatus.CONFIRMED -> "Confirmado"
        OrderStatus.PROCESSING -> "En proceso"
        OrderStatus.SHIPPED -> "Enviado"
        OrderStatus.DELIVERED -> "Entregado"
        OrderStatus.CANCELLED -> "Cancelado"
    }
}

/**
 * Badge component to display order status
 */
@Composable
fun OrderStatusBadge(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        OrderStatus.PENDING -> Pair(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
        OrderStatus.CONFIRMED -> Pair(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        OrderStatus.PROCESSING -> Pair(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        OrderStatus.SHIPPED -> Pair(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        OrderStatus.DELIVERED -> Pair(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        OrderStatus.CANCELLED -> Pair(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
