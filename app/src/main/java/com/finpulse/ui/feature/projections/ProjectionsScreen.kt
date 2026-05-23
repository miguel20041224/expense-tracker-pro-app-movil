package com.finpulse.ui.feature.projections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finpulse.R
import com.finpulse.ui.components.DetailScreenContent
import com.finpulse.ui.components.FinPulseDetailScaffold
import com.finpulse.util.MoneyFormat

@Composable
fun ProjectionsScreen(
    onBack: () -> Unit,
    viewModel: ProjectionsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    FinPulseDetailScaffold(title = stringResource(R.string.nav_projections), onBack = onBack) { padding ->
        DetailScreenContent(padding) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                state.snapshot?.let { snapshot ->
                    val monthlySavings = (snapshot.incomeMinor - snapshot.expenseMinor).coerceAtLeast(0)
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(stringResource(R.string.projection_savings_title), style = MaterialTheme.typography.titleMedium)
                            Text(stringResource(R.string.projection_monthly_savings, MoneyFormat.formatMinor(monthlySavings)))
                            Text(stringResource(R.string.projection_6m, MoneyFormat.formatMinor(monthlySavings * 6)))
                        }
                    }
                }
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(stringResource(R.string.projection_debt_title), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.projection_debt_free, state.snowball.estimatedMonths))
                        Text(stringResource(R.string.projection_debt_remaining, MoneyFormat.formatMinor(state.snowball.totalRemainingMinor)))
                    }
                }
            }
        }
    }
}
