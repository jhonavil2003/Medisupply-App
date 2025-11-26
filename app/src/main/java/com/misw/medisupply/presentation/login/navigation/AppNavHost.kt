package com.misw.medisupply.presentation.login.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.misw.medisupply.presentation.login.screens.LoginScreen
import com.misw.medisupply.presentation.login.viewmodel.LoginViewModel
import com.misw.medisupply.presentation.navigation.MainRoutes
import com.misw.medisupply.presentation.registration.screens.CustomerRegistrationScreen

@Composable
fun AppNavHost(startDestination: String = MainRoutes.LOGIN, vm: LoginViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable(MainRoutes.LOGIN) {

            LoginScreen(vm = vm, navController = navController)
        }
        composable(MainRoutes.CUSTOMER_REGISTRATION) {
            CustomerRegistrationScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onRegistrationComplete = {
                    navController.navigate(MainRoutes.LOGIN) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}