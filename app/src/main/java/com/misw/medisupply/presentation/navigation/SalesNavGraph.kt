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
object SaleRoutes {
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
fun SalesNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = SaleRoutes.SALES_FORCE_FLOW,
        modifier = modifier
    ) {

        // Sales Force Flow - Complete navigation for internal staff
        composable(SaleRoutes.SALES_FORCE_FLOW) {
            SalesForceNavigation(
                onNavigateToRoleSelection = {
                    navController.navigate(MainRoutes.ROLE_SELECTION) {
                        popUpTo(MainRoutes.SALES_FORCE_FLOW) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
