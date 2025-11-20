package com.misw.medisupply.presentation.salesforce.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.R
import com.misw.medisupply.presentation.common.components.MedisupplyAppBar
import com.misw.medisupply.presentation.components.localizedStringResource
import com.misw.medisupply.presentation.salesforce.screens.orders.viewmodel.OrdersViewModel

@Composable
fun OrdersScreen(
    onNavigateToCustomerList: () -> Unit = {},
    onNavigateToCreateOrder: () -> Unit = {},
    onNavigateToMyOrders: () -> Unit = {},
    onNavigateBack: (() -> Unit)? = null,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    // Obtener LocaleManager del ViewModel
    val localeManager = viewModel.localeManager
    val currentLanguage = localeManager.currentLanguage.collectAsState().value
    Scaffold(
        topBar = {
            MedisupplyAppBar(
                title = localizedStringResource(R.string.orders_title, localeManager),
                subtitle = localizedStringResource(R.string.sales_force_subtitle, localeManager),
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
                icon = Icons.Filled.Person,
                avatarBackgroundColor = Color(0xFFB4FFF1),
                iconTint = Color(0xFF008678),
                title = localizedStringResource(R.string.customer_consultation_title, localeManager),
                subtitle = localizedStringResource(R.string.customer_consultation_subtitle, localeManager),
                onClick = onNavigateToCustomerList
            )
            
            OrdersOptionCard(
                icon = Icons.Filled.ShoppingCart,
                avatarBackgroundColor = Color(0xFFD6E3FF),
                iconTint = Color(0xFF3C5BAA),
                title = localizedStringResource(R.string.create_order_title, localeManager),
                subtitle = localizedStringResource(R.string.create_order_subtitle, localeManager),
                onClick = onNavigateToCreateOrder
            )
            
            OrdersOptionCard(
                icon = Icons.Filled.Assignment,
                avatarBackgroundColor = Color(0xFFFFE5B4),
                iconTint = Color(0xFFE67E00),
                title = localizedStringResource(R.string.my_orders_title, localeManager),
                subtitle = localizedStringResource(R.string.my_orders_subtitle, localeManager),
                onClick = onNavigateToMyOrders
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrdersOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    avatarBackgroundColor: Color,
    iconTint: Color,
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
            // Avatar circular con colores personalizados
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(avatarBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(28.dp),
                    tint = iconTint
                )
            }
            
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
