package com.finpulse.ui.auth

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object AuthRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
}

@Composable
fun AuthNavHost(onLoggedIn: (Long) -> Unit) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = AuthRoutes.LOGIN) {
        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                onRegister = { navController.navigate(AuthRoutes.REGISTER) },
                onLoggedIn = onLoggedIn,
            )
        }
        composable(AuthRoutes.REGISTER) {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onLoggedIn = onLoggedIn,
            )
        }
    }
}
