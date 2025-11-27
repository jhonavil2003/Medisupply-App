package com.misw.medisupply.presentation.salesforce.navigation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import com.misw.medisupply.presentation.salesforce.screens.visits.viewmodel.VisitHomeViewModel
import com.misw.medisupply.ui.theme.NavBarBackground
import com.misw.medisupply.ui.theme.NavBarIconBlue
import com.misw.medisupply.ui.theme.NavBarIconGreen

private const val TAG = "SalesForceNavigation"

/**
 * Main navigation container for Sales Force role
 * Integrates NavGraph and BottomNavigationBar
 */
@Composable
fun SalesForceNavigation(

) {
    Log.d(TAG, "Iniciando composición de SalesForceNavigation")
    val navController = rememberNavController()
    // Get LocaleManager from a ViewModel for navigation
    val context = LocalContext.current
    val localeManager = remember { LocaleManager(context) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            SalesForceBottomNavigationBar(
                navController = navController,
                localeManager = localeManager
            )
        }
    ) { paddingValues ->
        SalesForceNavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            localeManager = localeManager
        )
    }
}

/**
 * Bottom Navigation Bar for Sales Force
 * Shows navigation items specific to internal staff
 */
@Composable
private fun SalesForceBottomNavigationBar(
    navController: NavHostController,
    localeManager: LocaleManager
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentLanguage = localeManager.currentLanguage.collectAsState().value

    // Log para rastrear cambios de ruta
    LaunchedEffect(currentDestination) {
        Log.d(TAG, "Ruta actual detectada: ${currentDestination?.route}")
    }

    val navigationItems = listOf(
        NavigationItem(
            title = localizedStringResource(R.string.nav_home, localeManager),
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = SalesForceRoutes.HOME,
            iconColor = NavBarIconBlue
        ),
        NavigationItem(
            title = localizedStringResource(R.string.nav_visits, localeManager),
            selectedIcon = Icons.Filled.Route,
            unselectedIcon = Icons.Outlined.Route,
            route = SalesForceRoutes.VISITS,
            iconColor = NavBarIconGreen
        ),
        NavigationItem(
            title = localizedStringResource(R.string.nav_orders, localeManager),
            selectedIcon = Icons.Filled.Archive,
            unselectedIcon = Icons.Outlined.Archive,
            route = SalesForceRoutes.ORDERS,
            iconColor = NavBarIconBlue
        ),
        NavigationItem(
            title = localizedStringResource(R.string.nav_performance, localeManager),
            selectedIcon = Icons.Filled.BarChart,
            unselectedIcon = Icons.Outlined.BarChart,
            route = SalesForceRoutes.PERFORMANCE,
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
                SalesForceRoutes.ORDERS -> {
                    // Select Orders tab when in any order-related screen
                    currentDestination?.hierarchy?.any { destination ->
                        val route = destination.route
                        route == SalesForceRoutes.ORDERS ||
                                route == SalesForceRoutes.CUSTOMER_LIST ||
                                route == SalesForceRoutes.CREATE_CUSTOMER ||
                                route == SalesForceRoutes.CREATE_ORDER ||
                                route == SalesForceRoutes.MY_ORDERS ||
                                route == SalesForceRoutes.PRODUCT_SELECTION ||
                                route?.startsWith("${SalesForceRoutes.PRODUCT_SELECTION}/") == true ||
                                route == SalesForceRoutes.ORDER_REVIEW ||
                                route?.startsWith("${SalesForceRoutes.ORDER_REVIEW}/") == true ||
                                route == SalesForceRoutes.ORDER_DETAIL ||
                                route?.startsWith("${SalesForceRoutes.ORDER_DETAIL}/") == true ||
                                route == SalesForceRoutes.EDIT_ORDER_SELECT_PRODUCTS ||
                                route?.startsWith("${SalesForceRoutes.EDIT_ORDER_SELECT_PRODUCTS}/") == true ||
                                route == SalesForceRoutes.EDIT_ORDER_REVIEW ||
                                route?.startsWith("${SalesForceRoutes.EDIT_ORDER_REVIEW}/") == true ||
                                route?.contains("order") == true
                    } == true
                }
                SalesForceRoutes.VISITS -> {
                    // Select Visits tab when in any visit-related screen
                    currentDestination?.hierarchy?.any { destination ->
                        val route = destination.route
                        route == SalesForceRoutes.VISITS ||
                                route == SalesForceRoutes.VISIT_LIST ||
                                route == SalesForceRoutes.CREATE_VISIT ||
                                route == SalesForceRoutes.VISIT_DETAIL ||
                                route?.startsWith("${SalesForceRoutes.VISIT_DETAIL}/") == true ||
                                route == SalesForceRoutes.ROUTES ||
                                route == SalesForceRoutes.ROUTE_LIST ||
                                route == SalesForceRoutes.GENERATE_ROUTE ||
                                route == SalesForceRoutes.ROUTE_DETAIL ||
                                route?.startsWith("${SalesForceRoutes.ROUTE_DETAIL}/") == true ||
                                route == SalesForceRoutes.ROUTE_EXECUTION ||
                                route?.startsWith("${SalesForceRoutes.ROUTE_EXECUTION}/") == true ||
                                route?.contains("visit") == true ||
                                route?.contains("route") == true
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
                    Log.d(TAG, "Navegando desde BottomBar a: ${item.route}")
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