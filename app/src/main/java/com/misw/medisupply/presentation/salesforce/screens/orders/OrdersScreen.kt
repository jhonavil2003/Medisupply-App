package com.misw.medisupply.presentation.salesforce.screens.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar

@Composable
fun OrdersScreen(
    onNavigateToCustomerList: () -> Unit = {},
    onNavigateToCreateOrder: () -> Unit = {},
    onNavigateToMyOrders: () -> Unit = {},
    onNavigateBack: (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = "Pedidos",
                subtitle = "Fuerza de ventas - Medisupply",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            OrdersOptionCard(
                icon = Icons.Filled.PersonSearch,
                title = "Consulta de Clientes",
                subtitle = "Busca tu cartera y abre el perfil de cada cliente",
                onClick = onNavigateToCustomerList
            )
            
            OrdersOptionCard(
                icon = Icons.Filled.ShoppingCart,
                title = "Crear Pedido",
                subtitle = "Arma el pedido con disponibilidad en tiempo real",
                onClick = onNavigateToCreateOrder
            )
            
            OrdersOptionCard(
                icon = Icons.Filled.Assignment,
                title = "Mis Pedidos",
                subtitle = "Estados, filtros, y detalle de órdenes",
                onClick = onNavigateToMyOrders
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrdersOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.size(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}
