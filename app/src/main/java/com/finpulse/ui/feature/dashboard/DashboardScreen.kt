package com.finpulse.ui.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finpulse.R
import com.finpulse.domain.model.DashboardSnapshot
import com.finpulse.domain.model.FinancialInsight
import com.finpulse.ui.components.CashFlowChart
import com.finpulse.ui.theme.FinAmber
import com.finpulse.ui.theme.FinEmerald
import com.finpulse.ui.theme.FinRose
import com.finpulse.ui.theme.FinSlate800
import com.finpulse.util.MoneyFormat

@Composable
fun DashboardScreen(
    onAlerts: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when {
        state.isLoading -> LoadingState()
        state.errorMessage != null -> ErrorState(onRetry = viewModel::refresh)
        state.snapshot != null -> DashboardContent(state.snapshot!!, onAlerts)
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.error_generic), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
    }
}

@Composable
private fun DashboardContent(snapshot: DashboardSnapshot, onAlerts: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                stringResource(R.string.dashboard_greeting),
                style = MaterialTheme.typography.headlineLarge,
            )
        }
        item {
            BalanceCard(
                balance = MoneyFormat.formatMinor(snapshot.balanceMinor),
                income = MoneyFormat.formatMinor(snapshot.incomeMinor),
                expenses = MoneyFormat.formatMinor(snapshot.expenseMinor),
            )
        }
        item {
            HealthScoreCard(score = snapshot.healthScore, alerts = snapshot.unreadAlerts, onAlerts = onAlerts)
        }
        item {
            Text(stringResource(R.string.dashboard_cashflow_chart), style = MaterialTheme.typography.titleLarge)
        }
        item {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    CashFlowChart(trend = snapshot.monthlyTrend)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(top = 8.dp)) {
                        Text("● ${stringResource(R.string.dashboard_income)}", color = FinEmerald, style = MaterialTheme.typography.labelSmall)
                        Text("● ${stringResource(R.string.dashboard_expenses)}", color = FinRose, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
        item {
            Text(
                stringResource(R.string.dashboard_overview),
                style = MaterialTheme.typography.titleLarge,
            )
        }
        item {
            OverviewRow(snapshot)
        }
        item {
            Text(
                stringResource(R.string.dashboard_insights),
                style = MaterialTheme.typography.titleLarge,
            )
        }
        if (snapshot.insights.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.dashboard_empty_insights),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
        } else {
            items(snapshot.insights, key = { it.id }) { insight ->
                InsightCard(insight)
            }
        }
    }
}

@Composable
private fun OverviewRow(snapshot: DashboardSnapshot) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OverviewChip(
                label = stringResource(R.string.dashboard_budgets),
                value = snapshot.budgetCount.toString(),
                modifier = Modifier.weight(1f),
            )
            OverviewChip(
                label = stringResource(R.string.dashboard_goals),
                value = snapshot.goalCount.toString(),
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OverviewChip(
                label = stringResource(R.string.dashboard_debt),
                value = MoneyFormat.formatMinor(snapshot.totalDebtMinor),
                modifier = Modifier.weight(1f),
            )
            OverviewChip(
                label = stringResource(R.string.dashboard_projections),
                value = snapshot.projectionCount.toString(),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun OverviewChip(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun BalanceCard(balance: String, income: String, expenses: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = FinSlate800),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                stringResource(R.string.dashboard_balance),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                balance,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricColumn(stringResource(R.string.dashboard_income), income, FinEmerald)
                MetricColumn(stringResource(R.string.dashboard_expenses), expenses, FinRose)
            }
        }
    }
}

@Composable
private fun MetricColumn(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column {
        Text(label, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
        Text(value, color = color, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun HealthScoreCard(score: Int, alerts: Int, onAlerts: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.dashboard_health_score), style = MaterialTheme.typography.titleMedium)
                Text("$score", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            LinearProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = when {
                    score >= 75 -> FinEmerald
                    score >= 50 -> FinAmber
                    else -> FinRose
                },
            )
            if (alerts > 0) {
                TextButton(onClick = onAlerts) {
                    Text(
                        "${stringResource(R.string.dashboard_alerts)}: $alerts →",
                        color = FinAmber,
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightCard(insight: FinancialInsight) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(insight.title, style = MaterialTheme.typography.titleMedium)
            Text(insight.body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
