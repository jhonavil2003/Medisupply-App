package com.misw.medisupply.presentation.salesforce.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.misw.medisupply.ui.theme.NavBarBackground
import com.misw.medisupply.ui.theme.NavBarIconBlue
import com.misw.medisupply.ui.theme.NavBarIconGreen

/**
 * Main navigation container for Sales Force role
 * Integrates NavGraph and BottomNavigationBar
 */
@Composable
fun SalesForceNavigation(
    onNavigateToRoleSelection: () -> Unit = {}
) {
    val navController = rememberNavController()
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            SalesForceBottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        SalesForceNavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            onNavigateToRoleSelection = onNavigateToRoleSelection
        )
    }
}

/**
 * Bottom Navigation Bar for Sales Force
 * Shows navigation items specific to internal staff
 */
@Composable
private fun SalesForceBottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val navigationItems = listOf(
        NavigationItem(
            title = "Inicio",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = SalesForceRoutes.HOME,
            iconColor = NavBarIconBlue
        ),
        NavigationItem(
            title = "Visitas",
            selectedIcon = Icons.Filled.Route,
            unselectedIcon = Icons.Outlined.Route,
            route = SalesForceRoutes.VISITS,
            iconColor = NavBarIconGreen
        ),
        NavigationItem(
            title = "Pedidos",
            selectedIcon = Icons.Filled.Archive,
            unselectedIcon = Icons.Outlined.Archive,
            route = SalesForceRoutes.ORDERS,
            iconColor = NavBarIconBlue
        ),
        NavigationItem(
            title = "Desempeño",
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
            val isSelected = currentDestination?.hierarchy?.any { 
                it.route == item.route 
            } == true
            
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
