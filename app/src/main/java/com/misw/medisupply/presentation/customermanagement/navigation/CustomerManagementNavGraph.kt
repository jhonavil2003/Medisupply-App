package com.misw.medisupply.presentation.customermanagement.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.misw.medisupply.presentation.customermanagement.screens.account.CustomerAccountScreen
import com.misw.medisupply.presentation.customermanagement.screens.home.CustomerHomeScreen
import com.misw.medisupply.presentation.customermanagement.screens.orders.CustomerOrdersScreen
import com.misw.medisupply.presentation.customermanagement.screens.shop.ShopScreen

/**
 * Navigation graph for Customer Management role
 * Defines all navigation destinations for self-service clients
 */
@Composable
fun CustomerManagementNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = CustomerManagementRoutes.HOME,
        modifier = modifier
    ) {
        // Home Screen - Bienvenida
        composable(route = CustomerManagementRoutes.HOME) {
            CustomerHomeScreen()
        }
        
        // Shop Screen - Catálogo de productos
        composable(route = CustomerManagementRoutes.SHOP) {
            ShopScreen()
        }
        
        // Orders Screen - Mis pedidos
        composable(route = CustomerManagementRoutes.ORDERS) {
            CustomerOrdersScreen()
        }
        
        // Account Screen - Mi cuenta
        composable(route = CustomerManagementRoutes.ACCOUNT) {
            CustomerAccountScreen()
        }
    }
}
