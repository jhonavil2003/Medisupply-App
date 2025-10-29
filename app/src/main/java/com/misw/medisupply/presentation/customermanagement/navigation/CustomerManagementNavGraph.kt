package com.misw.medisupply.presentation.customermanagement.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.misw.medisupply.domain.model.order.CartItem
import com.misw.medisupply.presentation.customermanagement.screens.account.CustomerAccountScreen
import com.misw.medisupply.presentation.customermanagement.screens.home.CustomerHomeScreen
import com.misw.medisupply.presentation.customermanagement.screens.orders.CustomerOrdersScreen
import com.misw.medisupply.presentation.customermanagement.screens.orders.OrderDetailScreen
import com.misw.medisupply.presentation.customermanagement.screens.shop.ShopScreen
import com.misw.medisupply.presentation.customermanagement.screens.shop.createorder.CustomerOrderReviewScreen
import com.misw.medisupply.presentation.customermanagement.screens.shop.createorder.CustomerProductSelectionScreen

/**
 * Navigation graph for Customer Management role
 * Defines all navigation destinations for self-service clients
 */
@Composable
fun CustomerManagementNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onNavigateToRoleSelection: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = CustomerManagementRoutes.HOME,
        modifier = modifier
    ) {
        // Home Screen - Bienvenida
        composable(route = CustomerManagementRoutes.HOME) {
            CustomerHomeScreen(
                onNavigateToRoleSelection = onNavigateToRoleSelection
            )
        }
        
        // Shop Screen - Compras (con funcionalidad completa de órdenes)
        composable(route = CustomerManagementRoutes.SHOP) {
            ShopScreen(
                onNavigateToCreateOrder = {
                    navController.navigate(CustomerManagementRoutes.CREATE_ORDER_PRODUCTS)
                }
            )
        }
        
        // Orders Screen - Mis pedidos
        composable(route = CustomerManagementRoutes.ORDERS) {
            CustomerOrdersScreen(
                onNavigateToOrderDetail = { orderId ->
                    android.util.Log.d("CustomerNavGraph", "Navegando a detalle del pedido con ID: $orderId")
                    navController.navigate("${CustomerManagementRoutes.ORDER_DETAIL}/$orderId")
                }
            )
        }
        
        // Order Detail Screen - Detalle del pedido
        composable(
            route = "${CustomerManagementRoutes.ORDER_DETAIL}/{orderId}",
            arguments = listOf(
                navArgument("orderId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            OrderDetailScreen(
                orderId = orderId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Create Order - Product Selection (reutiliza lógica de sales force)
        composable(route = CustomerManagementRoutes.CREATE_ORDER_PRODUCTS) {
            CustomerProductSelectionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onConfirmOrder = { cartItems ->
                    val cartItemsJson = Uri.encode(Gson().toJson(cartItems))
                    navController.navigate("${CustomerManagementRoutes.CREATE_ORDER_REVIEW}/$cartItemsJson")
                }
            )
        }
        
        // Create Order - Review Order (incluye selección de fecha de entrega)
        composable(
            route = "${CustomerManagementRoutes.CREATE_ORDER_REVIEW}/{cartItemsJson}",
            arguments = listOf(
                navArgument("cartItemsJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cartItemsJson = backStackEntry.arguments?.getString("cartItemsJson")
            val cartItems = cartItemsJson?.let {
                val mapType = object : com.google.gson.reflect.TypeToken<Map<String, CartItem>>() {}.type
                Gson().fromJson<Map<String, CartItem>>(Uri.decode(it), mapType)
            }
            
            if (cartItems != null) {
                CustomerOrderReviewScreen(
                    cartItems = cartItems,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onOrderSuccess = { orderNumber ->
                        // Navigate back to shop screen after successful order creation
                        navController.navigate(CustomerManagementRoutes.SHOP) {
                            // Clear back stack up to shop screen
                            popUpTo(CustomerManagementRoutes.SHOP) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
        
        // Account Screen - Mi cuenta
        composable(route = CustomerManagementRoutes.ACCOUNT) {
            CustomerAccountScreen()
        }
    }
}
