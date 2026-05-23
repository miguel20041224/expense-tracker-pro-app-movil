package com.finpulse.ui.feature.debts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finpulse.R
import com.finpulse.ui.components.DetailScreenContent
import com.finpulse.ui.components.FinPulseDetailScaffold
import com.finpulse.util.MoneyFormat

@Composable
fun SnowballScreen(
    onBack: () -> Unit,
    viewModel: DebtsViewModel = hiltViewModel(),
) {
    val plan by viewModel.snowballPlan.collectAsStateWithLifecycle()

    FinPulseDetailScaffold(title = stringResource(R.string.snowball_title), onBack = onBack) { padding ->
        DetailScreenContent(padding) {
            LazyColumn(
                Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(
                        stringResource(R.string.snowball_total_debt, MoneyFormat.formatMinor(plan.totalRemainingMinor)),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(stringResource(R.string.snowball_interest_saved, MoneyFormat.formatMinor(plan.estimatedInterestSavedMinor)))
                    Text(stringResource(R.string.snowball_timeline, plan.estimatedMonths))
                }
                items(plan.steps, key = { it.debtId }) { step ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text("${step.order}. ${step.debtName}", style = MaterialTheme.typography.titleMedium)
                            Text(stringResource(R.string.snowball_step_remaining, MoneyFormat.formatMinor(step.remainingMinor)))
                            Text(stringResource(R.string.snowball_step_months, step.estimatedMonthsToPayoff))
                            if (step.isRecommendedNext) {
                                Text(
                                    stringResource(R.string.snowball_recommended),
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
