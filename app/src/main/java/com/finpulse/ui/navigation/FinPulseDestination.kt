package com.finpulse.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.finpulse.R

sealed class FinPulseDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    data object Dashboard : FinPulseDestination(FinPulseRoutes.DASHBOARD, R.string.nav_dashboard, Icons.Default.Dashboard)
    data object Expenses : FinPulseDestination(FinPulseRoutes.EXPENSES, R.string.nav_expenses, Icons.Default.TrendingDown)
    data object Income : FinPulseDestination(FinPulseRoutes.INCOME, R.string.nav_income, Icons.Default.TrendingUp)
    data object Debts : FinPulseDestination(FinPulseRoutes.DEBTS, R.string.nav_debts, Icons.Default.AccountBalance)
    data object More : FinPulseDestination(FinPulseRoutes.MORE, R.string.nav_more, Icons.Default.Menu)

    companion object {
        val bottomBar = listOf(Dashboard, Expenses, Income, Debts, More)
    }
}
