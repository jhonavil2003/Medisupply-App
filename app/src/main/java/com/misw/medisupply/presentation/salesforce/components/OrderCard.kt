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
import com.misw.medisupply.domain.model.order.Order
import com.misw.medisupply.domain.model.order.OrderStatus
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Order Card Component
 * Displays order information in a card format
 */
@Composable
fun OrderCard(
    order: Order,
    onDetailClick: () -> Unit,
    onEditClick: () -> Unit,
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
                .padding(16.dp)
        ) {
            // Header: Order number and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.orderNumber ?: "N/A",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Date
                    order.orderDate?.let { date ->
                        Text(
                            text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date),
                            fontSize = 12.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }
                
                // Status badge
                OrderStatusBadge(status = order.status)
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Customer info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cliente:",
                    fontSize = 13.sp,
                    color = Color(0xFF757575),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = order.customer?.getDisplayName() ?: "ID: ${order.customerId}",
                    fontSize = 13.sp,
                    color = Color(0xFF212121),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            
            // Delivery city
            order.deliveryCity?.let { city ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ciudad:",
                        fontSize = 13.sp,
                        color = Color(0xFF757575),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = city,
                        fontSize = 13.sp,
                        color = Color(0xFF212121)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            // Items count
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Productos:",
                    fontSize = 13.sp,
                    color = Color(0xFF757575),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${order.getTotalItems()} items • ${order.getTotalQuantity()} unidades",
                    fontSize = 13.sp,
                    color = Color(0xFF212121)
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Divider line
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE0E0E0))
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Footer: Total and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = order.getFormattedTotal(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Edit button (only for pending orders)
                if (order.status == OrderStatus.PENDING) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar orden",
                            tint = MaterialTheme.colorScheme.primary
                        )
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
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = status.displayName,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}
