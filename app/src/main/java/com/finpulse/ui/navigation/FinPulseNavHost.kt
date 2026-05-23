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
import androidx.navigation.NavType
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.finpulse.domain.model.TransactionType
import com.finpulse.ui.feature.alerts.AlertsScreen
import com.finpulse.ui.feature.budgets.BudgetFormScreen
import com.finpulse.ui.feature.budgets.BudgetsGoalsScreen
import com.finpulse.ui.feature.budgets.GoalFormScreen
import com.finpulse.ui.feature.intelligence.IntelligenceScreen
import com.finpulse.ui.feature.settings.SettingsScreen
import com.finpulse.ui.feature.dashboard.DashboardScreen
import com.finpulse.ui.feature.debts.DebtFormScreen
import com.finpulse.ui.feature.debts.DebtsScreen
import com.finpulse.ui.feature.debts.SnowballScreen
import com.finpulse.ui.feature.more.MoreScreen
import com.finpulse.ui.feature.projections.ProjectionsScreen
import com.finpulse.ui.feature.reports.ReportsScreen
import com.finpulse.ui.feature.transactions.ExpensesListScreen
import com.finpulse.ui.feature.transactions.IncomeListScreen
import com.finpulse.ui.feature.transactions.TransactionFormScreen

@Composable
fun FinPulseNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in FinPulseRoutes.bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    FinPulseDestination.bottomBar.forEach { destination ->
                        val selected = navBackStackEntry?.destination?.hierarchy?.any {
                            it.route == destination.route
                        } == true
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
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = FinPulseRoutes.DASHBOARD,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(FinPulseRoutes.DASHBOARD) {
                DashboardScreen(onAlerts = { navController.navigate(FinPulseRoutes.ALERTS) })
            }
            composable(FinPulseRoutes.EXPENSES) {
                ExpensesListScreen(
                    onAdd = { navController.navigate(FinPulseRoutes.transactionForm(TransactionType.EXPENSE.storageValue)) },
                    onEdit = { id -> navController.navigate(FinPulseRoutes.transactionForm(TransactionType.EXPENSE.storageValue, id)) },
                )
            }
            composable(FinPulseRoutes.INCOME) {
                IncomeListScreen(
                    onAdd = { navController.navigate(FinPulseRoutes.transactionForm(TransactionType.INCOME.storageValue)) },
                    onEdit = { id -> navController.navigate(FinPulseRoutes.transactionForm(TransactionType.INCOME.storageValue, id)) },
                )
            }
            composable(FinPulseRoutes.DEBTS) {
                DebtsScreen(
                    onAdd = { navController.navigate(FinPulseRoutes.debtForm()) },
                    onEdit = { id -> navController.navigate(FinPulseRoutes.debtForm(id)) },
                    onSnowball = { navController.navigate(FinPulseRoutes.SNOWBALL) },
                )
            }
            composable(FinPulseRoutes.MORE) {
                MoreScreen(
                    onAlerts = { navController.navigate(FinPulseRoutes.ALERTS) },
                    onBudgetsGoals = { navController.navigate(FinPulseRoutes.BUDGETS_GOALS) },
                    onIntelligence = { navController.navigate(FinPulseRoutes.INTELLIGENCE) },
                    onReports = { navController.navigate(FinPulseRoutes.REPORTS) },
                    onProjections = { navController.navigate(FinPulseRoutes.PROJECTIONS) },
                    onSnowball = { navController.navigate(FinPulseRoutes.SNOWBALL) },
                    onSettings = { navController.navigate(FinPulseRoutes.SETTINGS) },
                )
            }
            composable(
                route = FinPulseRoutes.TRANSACTION_FORM,
                arguments = listOf(
                    navArgument("type") { type = NavType.StringType },
                    navArgument("id") { type = NavType.LongType; defaultValue = -1L },
                ),
            ) {
                TransactionFormScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = FinPulseRoutes.DEBT_FORM,
                arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L }),
            ) {
                DebtFormScreen(onBack = { navController.popBackStack() })
            }
            composable(FinPulseRoutes.SNOWBALL) {
                SnowballScreen(onBack = { navController.popBackStack() })
            }
            composable(FinPulseRoutes.REPORTS) {
                ReportsScreen(onBack = { navController.popBackStack() })
            }
            composable(FinPulseRoutes.PROJECTIONS) {
                ProjectionsScreen(onBack = { navController.popBackStack() })
            }
            composable(FinPulseRoutes.ALERTS) {
                AlertsScreen(onBack = { navController.popBackStack() })
            }
            composable(FinPulseRoutes.BUDGETS_GOALS) {
                BudgetsGoalsScreen(
                    onBack = { navController.popBackStack() },
                    onAddBudget = { navController.navigate(FinPulseRoutes.BUDGET_FORM) },
                    onAddGoal = { navController.navigate(FinPulseRoutes.GOAL_FORM) },
                )
            }
            composable(FinPulseRoutes.BUDGET_FORM) {
                BudgetFormScreen(onBack = { navController.popBackStack() })
            }
            composable(FinPulseRoutes.GOAL_FORM) {
                GoalFormScreen(onBack = { navController.popBackStack() })
            }
            composable(FinPulseRoutes.SETTINGS) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
            composable(FinPulseRoutes.INTELLIGENCE) {
                IntelligenceScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
