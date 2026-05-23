package com.finpulse.ui.feature.budgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finpulse.R
import com.finpulse.domain.model.Budget
import com.finpulse.domain.model.Goal
import com.finpulse.ui.components.FinPulseDetailScaffold
import com.finpulse.ui.theme.FinEmerald
import com.finpulse.ui.theme.FinRose
import com.finpulse.util.MoneyFormat

@Composable
fun BudgetsGoalsScreen(
    onBack: () -> Unit,
    onAddBudget: () -> Unit,
    onAddGoal: () -> Unit,
    viewModel: BudgetsGoalsViewModel = hiltViewModel(),
) {
    val budgets by viewModel.budgets.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()

    FinPulseDetailScaffold(title = stringResource(R.string.nav_budgets_goals), onBack = onBack) { topPadding ->
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(onClick = onAddBudget) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_budget))
                }
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        PaddingValues(
                            top = topPadding.calculateTopPadding(),
                            bottom = innerPadding.calculateBottomPadding(),
                        ),
                    )
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onAddBudget) { Text(stringResource(R.string.add_budget)) }
                        TextButton(onClick = onAddGoal) { Text(stringResource(R.string.add_goal)) }
                    }
                }
                item { Text(stringResource(R.string.section_budgets), style = MaterialTheme.typography.titleLarge) }
                if (budgets.isEmpty()) {
                    item { Text(stringResource(R.string.budgets_empty)) }
                } else {
                    items(budgets, key = { it.id }) { BudgetCard(it, onDelete = { viewModel.deleteBudget(it.id) }) }
                }
                item { Text(stringResource(R.string.section_goals), style = MaterialTheme.typography.titleLarge) }
                if (goals.isEmpty()) {
                    item { Text(stringResource(R.string.goals_empty)) }
                } else {
                    items(goals, key = { it.id }) { GoalCard(it, onDelete = { viewModel.deleteGoal(it.id) }) }
                }
            }
        }
    }
}

@Composable
private fun BudgetCard(budget: Budget, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(budget.categoryName, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete))
                }
            }
            Text(
                "${MoneyFormat.formatMinor(budget.spentMinor)} / ${MoneyFormat.formatMinor(budget.amountMinor)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            LinearProgressIndicator(
                progress = { budget.usagePercent / 100f },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                color = if (budget.usagePercent >= 90) FinRose else FinEmerald,
            )
        }
    }
}

@Composable
private fun GoalCard(goal: Goal, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(goal.name, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete))
                }
            }
            Text(
                "${MoneyFormat.formatMinor(goal.savedMinor)} / ${MoneyFormat.formatMinor(goal.targetMinor)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            LinearProgressIndicator(
                progress = { goal.progressPercent / 100f },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
            )
        }
    }
}
