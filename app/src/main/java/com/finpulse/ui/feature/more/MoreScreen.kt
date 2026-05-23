package com.finpulse.ui.feature.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finpulse.R

@Composable
fun MoreScreen(
    onReports: () -> Unit,
    onProjections: () -> Unit,
    onSnowball: () -> Unit,
    onAlerts: () -> Unit,
    onBudgetsGoals: () -> Unit,
    onIntelligence: () -> Unit,
    onSettings: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(stringResource(R.string.nav_more), style = MaterialTheme.typography.headlineMedium)
        MoreMenuItem(stringResource(R.string.nav_alerts), onAlerts)
        MoreMenuItem(stringResource(R.string.nav_budgets_goals), onBudgetsGoals)
        MoreMenuItem(stringResource(R.string.nav_intelligence), onIntelligence)
        MoreMenuItem(stringResource(R.string.nav_reports), onReports)
        MoreMenuItem(stringResource(R.string.nav_projections), onProjections)
        MoreMenuItem(stringResource(R.string.snowball_title), onSnowball)
        MoreMenuItem(stringResource(R.string.nav_settings), onSettings)
    }
}

@Composable
private fun MoreMenuItem(label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(label, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
    }
}
