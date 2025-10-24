package com.misw.medisupply.presentation.salesforce.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
// OrderDetailScreen moved to ui/archived/ - no longer used in salesforce flow
// import com.misw.medisupply.presentation.salesforce.screens.orders.detail.OrderDetailScreen
import com.misw.medisupply.presentation.salesforce.screens.orders.list.MyOrdersScreen
import com.misw.medisupply.presentation.salesforce.screens.orders.Mode
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
            SalesForceHomeScreen(
                onNavigateToVisits = {
                    navController.navigate(SalesForceRoutes.VISITS)
                }
            )
        }
        
        // Visits Screen - Gestión de visitas
        composable(route = SalesForceRoutes.VISITS) {
            com.misw.medisupply.presentation.salesforce.screens.visits.VisitScreen(
                onNavigateToRegisterVisit = {
                    navController.navigate(SalesForceRoutes.VISIT_LIST)
                }
            )
        }

        // Visit List Screen - Lista de visitas
        composable(route = SalesForceRoutes.VISIT_LIST) {
            com.misw.medisupply.presentation.salesforce.screens.visits.VisitListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreateVisit = { navController.navigate(SalesForceRoutes.CREATE_VISIT) }
            )
        }

        // Create Visit Screen
        composable(route = SalesForceRoutes.CREATE_VISIT) {
            com.misw.medisupply.presentation.salesforce.screens.visits.CreateVisitScreen(
                onNavigateBack = { navController.popBackStack() }
            )
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
                    mode = Mode.CREATE,
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
                    mode = Mode.CREATE,
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
                },
                onNavigateToEditOrder = { orderId ->
                    // Navigate to edit order product selection
                    navController.navigate(OrderRoute.editSelectProducts(orderId))
                }
            )
        }
        
        // Order Detail Screen - ARCHIVED (no longer used in salesforce flow)
        // Orders now navigate directly to edit/{orderId}/select-products for editing
        // Detail view may be re-implemented later if needed
        /*
        composable(
            route = "${SalesForceRoutes.ORDER_DETAIL}/{orderId}",
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(
                orderId = orderId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        */
        
        // Edit Order - Product Selection Screen
        composable(
            route = "${SalesForceRoutes.EDIT_ORDER_SELECT_PRODUCTS}/{orderId}",
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            // Use parent NavBackStackEntry to share ViewModel between edit screens
            val parentEntry = navController.getBackStackEntry(SalesForceRoutes.ORDERS)
            val viewModel: OrdersViewModel = hiltViewModel(parentEntry)
            val state = viewModel.state.collectAsState().value
            
            // Load order data when screen is first displayed
            LaunchedEffect(orderId) {
                viewModel.loadOrderForEdit(orderId)
            }
            
            // Show loading, error, or content
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = state.error ?: "Error al cargar la orden",
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { viewModel.loadOrderForEdit(orderId) }) {
                                Text("Reintentar")
                            }
                            OutlinedButton(onClick = { navController.popBackStack() }) {
                                Text("Volver")
                            }
                        }
                    }
                }
                state.selectedCustomer != null -> {
                    ProductSelectionScreen(
                        customer = state.selectedCustomer!!,
                        mode = Mode.EDIT,
                        orderId = orderId,
                        initialCartItems = state.cartItems,
                        onNavigateBack = { navController.popBackStack() },
                        onConfirmOrder = { cartItems ->
                            // Update cart items in OrdersViewModel before navigating
                            android.util.Log.d("SalesForceNavGraph", "Updating cartItems: ${cartItems.size} items")
                            viewModel.updateCartItems(cartItems)
                            // Navigate to edit review with orderId
                            navController.navigate(OrderRoute.editReview(orderId))
                        }
                    )
                }
            }
        }
        
        // Edit Order - Review Screen
        composable(
            route = "${SalesForceRoutes.EDIT_ORDER_REVIEW}/{orderId}",
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            // Use parent NavBackStackEntry to share ViewModel between edit screens
            val parentEntry = navController.getBackStackEntry(SalesForceRoutes.ORDERS)
            val viewModel: OrdersViewModel = hiltViewModel(parentEntry)
            val state = viewModel.state.value
            
            android.util.Log.d("SalesForceNavGraph", "EDIT_ORDER_REVIEW: orderId=$orderId")
            android.util.Log.d("SalesForceNavGraph", "selectedCustomer: ${state.selectedCustomer?.businessName}")
            android.util.Log.d("SalesForceNavGraph", "cartItems size: ${state.cartItems.size}")
            
            // Get customer and cart items from state
            if (state.selectedCustomer != null && state.cartItems.isNotEmpty()) {
                android.util.Log.d("SalesForceNavGraph", "Showing OrderReviewScreen in EDIT mode")
                OrderReviewScreen(
                    customer = state.selectedCustomer!!,
                    cartItems = state.cartItems,
                    mode = Mode.EDIT,
                    orderId = orderId,
                    orderStatus = state.orderStatus,
                    editViewModel = viewModel, // Pass the OrdersViewModel for edit mode
                    onNavigateBack = { navController.popBackStack() },
                    onOrderSuccess = { orderNumber ->
                        // Navigate back to orders list
                        navController.navigate(SalesForceRoutes.MY_ORDERS) {
                            popUpTo(SalesForceRoutes.MY_ORDERS) { inclusive = true }
                        }
                    }
                )
            } else {
                android.util.Log.e("SalesForceNavGraph", "ERROR: State not ready - customer or cartItems missing!")
                // Show loading or error state
                Box(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    androidx.compose.material3.Text("Error: Datos no disponibles")
                }
            }
        }
        
        // Performance Screen - Métricas y desempeño
        composable(route = SalesForceRoutes.PERFORMANCE) {
            PerformanceScreen()
        }
    }
}
