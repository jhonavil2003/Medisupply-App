package com.misw.medisupply.presentation.salesforce.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.misw.medisupply.R
import com.misw.medisupply.core.i18n.LocaleManager
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderStatus
import com.misw.medisupply.presentation.components.localizedStringResource
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Order Card Component
 * Displays order information in a card format
 */
@Composable
fun OrderCard(
    order: Order,
    localeManager: LocaleManager,
    onDetailClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onDetailClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Order number, date and status in one compact row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.orderNumber ?: "N/A",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Date inline with order number section
                    order.orderDate?.let { date ->
                        Text(
                            text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date),
                            fontSize = 11.sp,
                            color = Color(0xFF757575),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                
                // Status badge - more compact
                OrderStatusBadge(
                    status = order.status,
                    localeManager = localeManager
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Customer and city in a more compact format
            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Cliente:",
                            fontSize = 12.sp,
                            color = Color(0xFF757575),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = order.customer?.getDisplayName() ?: "ID: ${order.customerId}",
                            fontSize = 12.sp,
                            color = Color(0xFF212121),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    // Products info on the same row to save space
                    order.deliveryCity?.let { city ->
                        Text(
                            text = "• $city",
                            fontSize = 12.sp,
                            color = Color(0xFF757575),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                
                // Items and total in one compact row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${order.getTotalItems()} items • ${order.getTotalQuantity()} uds",
                        fontSize = 12.sp,
                        color = Color(0xFF757575),
                        modifier = Modifier.weight(1f)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = order.getFormattedTotal(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        // Edit and Delete buttons (only for pending orders)
                        if (order.status == OrderStatus.PENDING) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = onEditClick,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar orden",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                
                                IconButton(
                                    onClick = onDeleteClick,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = localizedStringResource(R.string.order_delete_button, localeManager),
                                        tint = Color(0xFFD32F2F),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Order Status Badge
 * Displays a colored badge with the order status
 */
@Composable
fun OrderStatusBadge(
    status: OrderStatus,
    localeManager: LocaleManager,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        OrderStatus.PENDING -> Color(0xFFFFF4E6) to Color(0xFFE65100)
        OrderStatus.CONFIRMED -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        OrderStatus.PROCESSING -> Color(0xFFF3E5F5) to Color(0xFF6A1B9A)
        OrderStatus.SHIPPED -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        OrderStatus.DELIVERED -> Color(0xFFC8E6C9) to Color(0xFF1B5E20)
        OrderStatus.CANCELLED -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    }
    
    Row(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = getStatusDisplayName(status, localeManager),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Get localized display name for order status
 */
@Composable
private fun getStatusDisplayName(status: OrderStatus, localeManager: LocaleManager): String {
    return when (status) {
        OrderStatus.PENDING -> localizedStringResource(R.string.order_status_pending, localeManager)
        OrderStatus.CONFIRMED -> localizedStringResource(R.string.order_status_confirmed, localeManager)
        OrderStatus.PROCESSING -> localizedStringResource(R.string.order_status_processing, localeManager)
        OrderStatus.SHIPPED -> localizedStringResource(R.string.order_status_shipped, localeManager)
        OrderStatus.DELIVERED -> localizedStringResource(R.string.order_status_delivered, localeManager)
        OrderStatus.CANCELLED -> localizedStringResource(R.string.order_status_cancelled, localeManager)
    }
}
