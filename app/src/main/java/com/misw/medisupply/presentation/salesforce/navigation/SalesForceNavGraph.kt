package com.misw.medisupply.presentation.salesforce.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.misw.medisupply.presentation.salesforce.screens.home.SalesForceHomeScreen
import com.misw.medisupply.presentation.salesforce.screens.orders.CreateOrderScreen
import com.misw.medisupply.presentation.salesforce.screens.orders.CustomerListScreen
import com.misw.medisupply.presentation.salesforce.screens.orders.EditOrderScreen
import com.misw.medisupply.presentation.salesforce.screens.orders.MyOrdersScreen
import com.misw.medisupply.presentation.salesforce.screens.orders.MyOrdersViewModel
import com.misw.medisupply.presentation.salesforce.screens.orders.OrdersScreen
import com.misw.medisupply.presentation.salesforce.screens.orders.OrdersViewModel
import com.misw.medisupply.presentation.salesforce.screens.performance.PerformanceScreen
import com.misw.medisupply.presentation.salesforce.screens.visits.VisitsScreen

/**
 * Navigation graph for Sales Force role
 * Defines all navigation destinations for internal staff
 */
@Composable
fun SalesForceNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = SalesForceRoutes.HOME,
        modifier = modifier
    ) {
        // Home Screen - Bienvenida
        composable(route = SalesForceRoutes.HOME) {
            SalesForceHomeScreen()
        }
        
        // Visits Screen - Gestión de visitas
        composable(route = SalesForceRoutes.VISITS) {
            VisitsScreen()
        }
        
        // Orders Screen - Gestión de pedidos (Menu)
        composable(route = SalesForceRoutes.ORDERS) {
            OrdersScreen(
                onNavigateToCustomerList = {
                    navController.navigate(SalesForceRoutes.CUSTOMER_LIST)
                },
                onNavigateToCreateOrder = {
                    navController.navigate(SalesForceRoutes.CREATE_ORDER)
                },
                onNavigateToMyOrders = {
                    navController.navigate(SalesForceRoutes.MY_ORDERS)
                }
            )
        }
        
        // Customer List Screen - Consulta de clientes
        composable(route = SalesForceRoutes.CUSTOMER_LIST) {
            val viewModel: OrdersViewModel = hiltViewModel()
            CustomerListScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Create Order Screen - Crear pedido
        composable(route = SalesForceRoutes.CREATE_ORDER) {
            CreateOrderScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // My Orders Screen - Mis pedidos
        composable(route = SalesForceRoutes.MY_ORDERS) {
            val viewModel: MyOrdersViewModel = hiltViewModel()
            MyOrdersScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEditOrder = { orderId ->
                    navController.navigate("${SalesForceRoutes.EDIT_ORDER}/$orderId")
                }
            )
        }
        
        // Edit Order Screen - Editar pedido
        composable(
            route = "${SalesForceRoutes.EDIT_ORDER}/{orderId}",
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            EditOrderScreen(
                orderId = orderId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Performance Screen - Métricas y desempeño
        composable(route = SalesForceRoutes.PERFORMANCE) {
            PerformanceScreen()
        }
    }
}
