package com.misw.medisupply.presentation.salesforce.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.model.order.CartItem
import com.misw.medisupply.presentation.salesforce.screens.home.SalesForceHomeScreen
import com.misw.medisupply.presentation.salesforce.screens.orders.OrdersScreen
import com.misw.medisupply.presentation.salesforce.screens.orders.create.CreateOrderScreen
import com.misw.medisupply.presentation.salesforce.screens.orders.create.CustomerListScreen
import com.misw.medisupply.presentation.salesforce.screens.orders.list.MyOrdersScreen
import com.misw.medisupply.presentation.salesforce.screens.orders.products.ProductSelectionScreen
import com.misw.medisupply.presentation.salesforce.screens.orders.review.OrderReviewScreen
import com.misw.medisupply.presentation.salesforce.screens.performance.PerformanceScreen
import com.misw.medisupply.presentation.salesforce.screens.visits.VisitsScreen
import com.misw.medisupply.presentation.salesforce.viewmodel.orders.OrdersViewModel

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
            val viewModel: OrdersViewModel = hiltViewModel()
            CreateOrderScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProductSelection = { customer ->
                    val customerJson = Uri.encode(Gson().toJson(customer))
                    navController.navigate("${SalesForceRoutes.PRODUCT_SELECTION}/$customerJson")
                },
                viewModel = viewModel
            )
        }
        
        // Product Selection Screen - Seleccionar productos para pedido
        composable(
            route = "${SalesForceRoutes.PRODUCT_SELECTION}/{customerJson}",
            arguments = listOf(
                navArgument("customerJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val customerJson = backStackEntry.arguments?.getString("customerJson")
            val customer = customerJson?.let { 
                Gson().fromJson(Uri.decode(it), Customer::class.java) 
            }
            
            customer?.let {
                ProductSelectionScreen(
                    customer = it,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onConfirmOrder = { cartItems ->
                        val customerJson = Uri.encode(Gson().toJson(it))
                        val cartItemsJson = Uri.encode(Gson().toJson(cartItems))
                        navController.navigate("${SalesForceRoutes.ORDER_REVIEW}/$customerJson/$cartItemsJson")
                    }
                )
            }
        }
        
        // Order Review Screen - Revisar pedido antes de confirmar
        composable(
            route = "${SalesForceRoutes.ORDER_REVIEW}/{customerJson}/{cartItemsJson}",
            arguments = listOf(
                navArgument("customerJson") { type = NavType.StringType },
                navArgument("cartItemsJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val customerJson = backStackEntry.arguments?.getString("customerJson")
            val cartItemsJson = backStackEntry.arguments?.getString("cartItemsJson")
            
            val customer = customerJson?.let { 
                Gson().fromJson(Uri.decode(it), Customer::class.java) 
            }
            val cartItems = cartItemsJson?.let {
                val mapType = object : com.google.gson.reflect.TypeToken<Map<String, CartItem>>() {}.type
                Gson().fromJson<Map<String, CartItem>>(Uri.decode(it), mapType)
            }
            
            if (customer != null && cartItems != null) {
                OrderReviewScreen(
                    customer = customer,
                    cartItems = cartItems,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onOrderSuccess = { orderNumber ->
                        // Navigate back to orders screen
                        navController.navigate(SalesForceRoutes.MY_ORDERS) {
                            // Clear back stack up to orders screen
                            popUpTo(SalesForceRoutes.ORDERS) {
                                inclusive = false
                            }
                        }
                    }
                )
            }
        }
        
        // My Orders Screen - Mis pedidos
        composable(route = SalesForceRoutes.MY_ORDERS) {
            MyOrdersScreen(
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
