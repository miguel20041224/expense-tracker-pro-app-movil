package com.finpulse.ui.feature.debts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.finpulse.domain.model.Debt
import com.finpulse.ui.theme.FinEmerald
import com.finpulse.ui.theme.FinSlate800
import com.finpulse.util.MoneyFormat

@Composable
fun DebtsScreen(
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onSnowball: () -> Unit,
    viewModel: DebtsViewModel = hiltViewModel(),
) {
    val debts by viewModel.debts.collectAsStateWithLifecycle()
    val plan by viewModel.snowballPlan.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add))
            }
        },
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(stringResource(R.string.nav_debts), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
            item {
                SnowballSummaryCard(plan, onSnowball)
            }
            if (debts.isEmpty()) {
                item {
                    Text(stringResource(R.string.empty_debts), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            } else {
                items(debts, key = { it.id }) { debt ->
                    DebtItem(debt, onEdit, viewModel::delete)
                }
            }
        }
    }
}

@Composable
private fun SnowballSummaryCard(
    plan: com.finpulse.domain.model.SnowballPlan,
    onOpen: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FinSlate800),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.snowball_title), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleMedium)
            plan.steps.firstOrNull { it.isRecommendedNext }?.let { step ->
                Text(
                    stringResource(R.string.snowball_next, step.debtName),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                )
            }
            Text(
                stringResource(R.string.snowball_timeline, plan.estimatedMonths),
                color = FinEmerald,
                fontWeight = FontWeight.SemiBold,
            )
            TextButton(onClick = onOpen) {
                Text(stringResource(R.string.snowball_view_plan))
            }
        }
    }
}

@Composable
private fun DebtItem(debt: Debt, onEdit: (Long) -> Unit, onDelete: (Debt) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEdit(debt.id) },
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(debt.name, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { onDelete(debt) }) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete))
                }
            }
            Text(
                "${MoneyFormat.formatMinor(debt.remainingMinor)} / ${MoneyFormat.formatMinor(debt.principalMinor)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(stringResource(R.string.debt_minimum, MoneyFormat.formatMinor(debt.minimumPaymentMinor)))
            Text(stringResource(R.string.debt_interest, debt.interestRate))
            LinearProgressIndicator(
                progress = { debt.progressPercent / 100f },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
            )
        }
    }
}
