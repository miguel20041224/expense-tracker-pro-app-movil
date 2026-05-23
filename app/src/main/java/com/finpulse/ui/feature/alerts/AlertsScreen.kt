package com.finpulse.ui.feature.alerts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finpulse.R
import com.finpulse.domain.model.Alert
import com.finpulse.ui.components.DetailScreenContent
import com.finpulse.ui.components.FinPulseDetailScaffold
import com.finpulse.ui.theme.FinAmber

@Composable
fun AlertsScreen(
    onBack: () -> Unit,
    viewModel: AlertsViewModel = hiltViewModel(),
) {
    val alerts by viewModel.alerts.collectAsStateWithLifecycle()

    FinPulseDetailScaffold(title = stringResource(R.string.nav_alerts), onBack = onBack) { padding ->
        DetailScreenContent(padding) {
            Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 8.dp)) {
                TextButton(onClick = viewModel::markAllRead) {
                    Text(stringResource(R.string.alerts_mark_all_read))
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (alerts.isEmpty()) {
                        item { Text(stringResource(R.string.alerts_empty)) }
                    } else {
                        items(alerts, key = { it.id }) { alert ->
                            AlertCard(alert, onRead = { viewModel.markRead(alert.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertCard(alert: Alert, onRead: () -> Unit) {
    val colors = if (alert.isRead) {
        CardDefaults.cardColors()
    } else {
        CardDefaults.cardColors(containerColor = FinAmber.copy(alpha = 0.12f))
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onRead),
        shape = RoundedCornerShape(12.dp),
        colors = colors,
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(alert.title, style = MaterialTheme.typography.titleMedium)
            Text(alert.message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
