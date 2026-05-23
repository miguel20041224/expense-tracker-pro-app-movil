package com.finpulse.ui.feature.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun ReportsScreen(
    onBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsStateWithLifecycle()
    var exportMsg by remember { mutableStateOf<String?>(null) }

    FinPulseDetailScaffold(title = stringResource(R.string.nav_reports), onBack = onBack) { padding ->
        DetailScreenContent(padding) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                val data = snapshot
                if (data == null) {
                    Text(stringResource(R.string.loading))
                } else {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("${stringResource(R.string.dashboard_balance)}: ${MoneyFormat.formatMinor(data.balanceMinor)}")
                            Text("${stringResource(R.string.dashboard_income)}: ${MoneyFormat.formatMinor(data.incomeMinor)}")
                            Text("${stringResource(R.string.dashboard_expenses)}: ${MoneyFormat.formatMinor(data.expenseMinor)}")
                            Text("${stringResource(R.string.dashboard_health_score)}: ${data.healthScore}")
                            Text("${stringResource(R.string.dashboard_debt)}: ${MoneyFormat.formatMinor(data.totalDebtMinor)}")
                        }
                    }
                    Button(
                        onClick = { viewModel.exportPdf(data) { path -> exportMsg = path ?: "Error" } },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.export_pdf))
                    }
                    exportMsg?.let { Text(stringResource(R.string.pdf_saved, it), style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
    }
}
