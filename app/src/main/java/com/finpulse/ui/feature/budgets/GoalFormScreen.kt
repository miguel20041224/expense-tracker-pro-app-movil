package com.finpulse.ui.feature.budgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finpulse.R
import com.finpulse.ui.components.AppTextField
import com.finpulse.ui.components.DetailScreenContent
import com.finpulse.ui.components.FinPulseDetailScaffold
import com.finpulse.ui.components.MoneyTextField

@Composable
fun GoalFormScreen(
    onBack: () -> Unit,
    viewModel: GoalFormViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    FinPulseDetailScaffold(title = stringResource(R.string.add_goal), onBack = onBack) { padding ->
        DetailScreenContent(padding) {
            Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AppTextField(
                    value = state.name,
                    onValueChange = viewModel::updateName,
                    label = { Text(stringResource(R.string.field_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                MoneyTextField(
                    value = state.targetInput,
                    onValueChange = viewModel::updateTarget,
                    label = { Text(stringResource(R.string.field_goal_target)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                MoneyTextField(
                    value = state.savedInput,
                    onValueChange = viewModel::updateSaved,
                    label = { Text(stringResource(R.string.field_goal_saved)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onBack) { Text(stringResource(R.string.action_cancel)) }
                    Button(onClick = viewModel::save, enabled = !state.isSaving) {
                        Text(stringResource(R.string.action_save))
                    }
                }
            }
        }
    }
}
