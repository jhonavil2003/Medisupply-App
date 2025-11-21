package com.misw.medisupply.presentation.customermanagement.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.medisupply.R
import com.misw.medisupply.core.i18n.LocaleManager
import com.misw.medisupply.presentation.components.localizedStringResource
import com.misw.medisupply.presentation.customermanagement.viewmodel.CustomerShopViewModel
import com.misw.medisupply.ui.theme.NavBarBackground
import com.misw.medisupply.ui.theme.NavBarIconBlue
import com.misw.medisupply.ui.theme.NavBarIconGreen

/**
 * Main navigation container for Customer Management role
 * Integrates NavGraph and BottomNavigationBar
 */
@Composable
fun CustomerManagementNavigation(
    onNavigateToRoleSelection: () -> Unit = {}
) {
    val navController = rememberNavController()
    // Get LocaleManager from a ViewModel for navigation
    val viewModel: CustomerShopViewModel = hiltViewModel()
    val localeManager = viewModel.localeManager
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            CustomerManagementBottomNavigationBar(
                navController = navController,
                localeManager = localeManager
            )
        }
    ) { paddingValues ->
        CustomerManagementNavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            onNavigateToRoleSelection = onNavigateToRoleSelection
        )
    }
}

/**
 * Bottom Navigation Bar for Customer Management
 * Shows navigation items specific to self-service clients
 */
@Composable
private fun CustomerManagementBottomNavigationBar(
    navController: NavHostController,
    localeManager: LocaleManager
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentLanguage = localeManager.currentLanguage.collectAsState().value
    
    val navigationItems = listOf(
        NavigationItem(
            title = localizedStringResource(R.string.nav_home, localeManager),
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = CustomerManagementRoutes.HOME,
            iconColor = NavBarIconBlue
        ),
        NavigationItem(
            title = localizedStringResource(R.string.nav_shop, localeManager),
            selectedIcon = Icons.Filled.ShoppingCart,
            unselectedIcon = Icons.Outlined.ShoppingCart,
            route = CustomerManagementRoutes.SHOP,
            iconColor = NavBarIconGreen
        ),
        NavigationItem(
            title = localizedStringResource(R.string.nav_orders, localeManager),
            selectedIcon = Icons.Filled.Receipt,
            unselectedIcon = Icons.Outlined.Receipt,
            route = CustomerManagementRoutes.ORDERS,
            iconColor = NavBarIconBlue
        ),
        NavigationItem(
            title = localizedStringResource(R.string.nav_account, localeManager),
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            route = CustomerManagementRoutes.ACCOUNT,
            iconColor = NavBarIconGreen
        )
    )
    
    NavigationBar(
        containerColor = NavBarBackground,
        tonalElevation = 8.dp
    ) {
        navigationItems.forEach { item ->
            // Enhanced selection logic to handle sub-routes
            val isSelected = when (item.route) {
                CustomerManagementRoutes.SHOP -> {
                    // Select Shop tab when in any shop-related screen (including order creation from shop)
                    currentDestination?.hierarchy?.any { destination ->
                        val route = destination.route
                        route == CustomerManagementRoutes.SHOP ||
                        route == CustomerManagementRoutes.CREATE_ORDER_PRODUCTS ||
                        route == CustomerManagementRoutes.CREATE_ORDER_REVIEW ||
                        route == CustomerManagementRoutes.PRODUCT_DETAIL ||
                        route?.startsWith("${CustomerManagementRoutes.PRODUCT_DETAIL}/") == true ||
                        route?.startsWith("customer_create_order") == true ||
                        route?.contains("shop") == true
                    } == true
                }
                CustomerManagementRoutes.ORDERS -> {
                    // Select Orders tab when in any order-related screen (excluding creation flow from shop)
                    currentDestination?.hierarchy?.any { destination ->
                        val route = destination.route
                        (route == CustomerManagementRoutes.ORDERS ||
                        route == CustomerManagementRoutes.ORDER_DETAIL ||
                        route?.startsWith("${CustomerManagementRoutes.ORDER_DETAIL}/") == true ||
                        route == CustomerManagementRoutes.ORDER_TRACKING ||
                        route?.startsWith("customer_order") == true) &&
                        // Exclude order creation flow which belongs to Shop
                        route != CustomerManagementRoutes.CREATE_ORDER_PRODUCTS &&
                        route != CustomerManagementRoutes.CREATE_ORDER_REVIEW &&
                        !route?.startsWith("customer_create_order")!!
                    } == true
                }
                else -> {
                    // Default behavior for other tabs
                    currentDestination?.hierarchy?.any { 
                        it.route == item.route 
                    } == true
                }
            }
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(item.title) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = item.iconColor,
                    selectedTextColor = item.iconColor,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = item.iconColor.copy(alpha = 0.1f)
                )
            )
        }
    }
}

/**
 * Navigation item data class
 */
private data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
    val iconColor: Color
)
