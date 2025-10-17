package com.misw.medisupply.presentation.salesforce.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.misw.medisupply.presentation.salesforce.screens.orders.EditableOrderItem

/**
 * Card component for editable product item in order
 * Displays product info with quantity controls and delete button
 */
@Composable
fun ProductItemCard(
    item: EditableOrderItem,
    onIncrementQuantity: () -> Unit,
    onDecrementQuantity: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // SECCIÓN SUPERIOR - 3 columnas verticales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // 1. COLUMNA IZQUIERDA: Stock status
                Text(
                    text = if (item.isInStock) "Stock" else "Sin stock",
                    fontSize = 12.sp,
                    color = if (item.isInStock) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontWeight = FontWeight.Medium
                )
                
                // 2. COLUMNA MEDIO: Product info
                Column(
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = item.productName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF212121),
                        lineHeight = 14.sp
                    )
                    Text(
                        text = item.productCode,
                        fontSize = 11.sp,
                        color = Color(0xFF757575),
                        lineHeight = 12.sp
                    )
                    Text(
                        text = "Precio unitario",
                        fontSize = 10.sp,
                        color = Color(0xFF757575),
                        lineHeight = 11.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = item.getFormattedUnitPrice(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF212121),
                        lineHeight = 13.sp
                    )
                }
                
                // 3. COLUMNA DERECHA: Delete button
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar producto",
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            // SECCIÓN INFERIOR - 2 columnas verticales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // 1. COLUMNA IZQUIERDA: Available stock
                Column {
                    Text(
                        text = "Disponibles:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1976D2),
                        lineHeight = 12.sp
                    )
                    Text(
                        text = "${item.availableStock}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2),
                        lineHeight = 12.sp
                    )
                }
                
                // 2. COLUMNA CENTRO: Quantity controls (más centrados)
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                    // Decrement button
                    IconButton(
                        onClick = onDecrementQuantity,
                        enabled = item.canDecrement(),
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(
                                if (item.canDecrement()) Color(0xFFE3F2FD)
                                else Color(0xFFE0E0E0)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrementar",
                            tint = if (item.canDecrement()) Color(0xFF1976D2)
                            else Color(0xFF9E9E9E),
                            modifier = Modifier.size(10.dp)
                        )
                    }
                    
                    // Quantity display
                    Text(
                        text = item.quantity.toString(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121),
                        modifier = Modifier.width(30.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    // Increment button
                    IconButton(
                        onClick = onIncrementQuantity,
                        enabled = item.canIncrement(),
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(
                                if (item.canIncrement()) Color(0xFF1976D2)
                                else Color(0xFFE0E0E0)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Incrementar",
                            tint = if (item.canIncrement()) Color.White
                            else Color(0xFF9E9E9E),
                            modifier = Modifier.size(10.dp)
                        )
                    }
                    }
                }
                
                // 3. COLUMNA DERECHA: Subtotal (justificado a la derecha)
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Subtotal",
                        fontSize = 10.sp,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = item.getFormattedSubtotal(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                }
            }
        }
    }
}
