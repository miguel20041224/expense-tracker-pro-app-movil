package com.finpulse.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.finpulse.R
import com.finpulse.ui.feature.dashboard.DashboardScreen
import com.finpulse.ui.feature.placeholder.PlaceholderScreen
import com.finpulse.ui.navigation.FinPulseDestination

@Composable
fun FinPulseNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                FinPulseDestination.bottomBar.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = null) },
                        label = { Text(stringResource(destination.labelRes)) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = FinPulseDestination.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(FinPulseDestination.Dashboard.route) { DashboardScreen() }
            composable(FinPulseDestination.Transactions.route) {
                PlaceholderScreen(R.string.transactions_placeholder)
            }
            composable(FinPulseDestination.Intelligence.route) {
                PlaceholderScreen(R.string.intelligence_placeholder)
            }
            composable(FinPulseDestination.More.route) {
                PlaceholderScreen(R.string.more_placeholder)
            }
        }
    }
}
