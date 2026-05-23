package com.finpulse.ui.feature.debts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finpulse.R
import com.finpulse.ui.components.AppTextField
import com.finpulse.ui.components.DetailScreenContent
import com.finpulse.ui.components.FinPulseDetailScaffold
import com.finpulse.ui.components.MoneyTextField

@Composable
fun DebtFormScreen(
    onBack: () -> Unit,
    viewModel: DebtFormViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val nameFocus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    FinPulseDetailScaffold(title = stringResource(R.string.add_debt), onBack = onBack) { padding ->
        if (state.isLoading) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator(Modifier.padding(24.dp))
            }
            return@FinPulseDetailScaffold
        }

        LaunchedEffect(Unit) {
            nameFocus.requestFocus()
            keyboard?.show()
        }

        DetailScreenContent(padding) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppTextField(
                    value = state.name,
                    onValueChange = viewModel::updateName,
                    label = { Text(stringResource(R.string.field_name)) },
                    modifier = Modifier.fillMaxWidth().focusRequester(nameFocus),
                    singleLine = true,
                )
                MoneyTextField(
                    value = state.principalInput,
                    onValueChange = viewModel::updatePrincipal,
                    label = { Text(stringResource(R.string.field_principal)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                MoneyTextField(
                    value = state.remainingInput,
                    onValueChange = viewModel::updateRemaining,
                    label = { Text(stringResource(R.string.field_remaining)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                MoneyTextField(
                    value = state.minimumPaymentInput,
                    onValueChange = viewModel::updateMinimum,
                    label = { Text(stringResource(R.string.field_minimum_payment)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                MoneyTextField(
                    value = state.interestInput,
                    onValueChange = viewModel::updateInterest,
                    label = { Text(stringResource(R.string.field_interest)) },
                    modifier = Modifier.fillMaxWidth(),
                    imeAction = ImeAction.Done,
                )
                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onBack) { Text(stringResource(R.string.action_cancel)) }
                    Button(onClick = { viewModel.save() }, enabled = !state.isSaving) {
                        Text(stringResource(R.string.action_save))
                    }
                }
            }
        }
    }
}
