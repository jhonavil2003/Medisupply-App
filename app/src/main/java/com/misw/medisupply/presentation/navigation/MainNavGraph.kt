package com.misw.medisupply.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.misw.medisupply.core.session.UserRole
import com.misw.medisupply.presentation.customermanagement.navigation.CustomerManagementNavigation
import com.misw.medisupply.presentation.registration.screens.CustomerRegistrationScreen
import com.misw.medisupply.presentation.roleselection.RoleSelectionScreen
import com.misw.medisupply.presentation.salesforce.navigation.SalesForceNavigation

/**
 * Main Routes for the application
 * Defines top-level navigation destinations
 */
object MainRoutes {
    const val ROLE_SELECTION = "role_selection"
    const val SALES_FORCE_FLOW = "salesforce_flow"
    const val CUSTOMER_MANAGEMENT_FLOW = "customer_management_flow"
    const val CUSTOMER_REGISTRATION = "customer_registration"
}

/**
 * Main Navigation Graph
 * Handles role selection and routes to appropriate role-specific navigation
 */
@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = MainRoutes.ROLE_SELECTION,
        modifier = modifier
    ) {
        // Role Selection Screen
        composable(MainRoutes.ROLE_SELECTION) {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    val destination = when (role) {
                        UserRole.SALES_FORCE -> MainRoutes.SALES_FORCE_FLOW
                        UserRole.CUSTOMER_MANAGEMENT -> MainRoutes.CUSTOMER_MANAGEMENT_FLOW
                    }
                    navController.navigate(destination) {
                        popUpTo(MainRoutes.ROLE_SELECTION) { 
                            inclusive = true 
                        }
                        launchSingleTop = true
                    }
                },
                onRegisterClick = {
                    navController.navigate(MainRoutes.CUSTOMER_REGISTRATION) {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        // Customer Registration Screen
        composable(MainRoutes.CUSTOMER_REGISTRATION) {
            CustomerRegistrationScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onRegistrationComplete = {
                    navController.navigate(MainRoutes.CUSTOMER_MANAGEMENT_FLOW) {
                        popUpTo(MainRoutes.ROLE_SELECTION) { 
                            inclusive = true 
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        
        // Sales Force Flow - Complete navigation for internal staff
        composable(MainRoutes.SALES_FORCE_FLOW) {
            SalesForceNavigation()
        }
        
        // Customer Management Flow - Complete navigation for clients
        composable(MainRoutes.CUSTOMER_MANAGEMENT_FLOW) {
            CustomerManagementNavigation()
        }
    }
}
