package com.finpulse.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.ui.graphics.vector.ImageVector
import com.finpulse.R

sealed class FinPulseDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    data object Dashboard : FinPulseDestination("dashboard", R.string.nav_dashboard, Icons.Default.Dashboard)
    data object Transactions : FinPulseDestination("transactions", R.string.nav_transactions, Icons.Default.SwapHoriz)
    data object Intelligence : FinPulseDestination("intelligence", R.string.nav_intelligence, Icons.Default.AutoGraph)
    data object More : FinPulseDestination("more", R.string.nav_more, Icons.Default.Menu)

    companion object {
        val bottomBar = listOf(Dashboard, Transactions, Intelligence, More)
    }
}
