package com.finpulse.ui.feature.transactions

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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.finpulse.domain.model.TransactionType
import com.finpulse.ui.components.AppTextField
import com.finpulse.ui.components.DetailScreenContent
import com.finpulse.ui.components.FinPulseDetailScaffold
import com.finpulse.ui.components.MoneyTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormScreen(
    onBack: () -> Unit,
    viewModel: TransactionFormViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var categoryExpanded by remember { mutableStateOf(false) }
    val amountFocus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val title = if (viewModel.type == TransactionType.INCOME) {
        stringResource(R.string.add_income)
    } else {
        stringResource(R.string.add_expense)
    }

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    FinPulseDetailScaffold(title = title, onBack = onBack) { padding ->
        if (state.isLoading) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator(Modifier.padding(24.dp))
            }
            return@FinPulseDetailScaffold
        }

        LaunchedEffect(Unit) {
            amountFocus.requestFocus()
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
                MoneyTextField(
                    value = state.amountInput,
                    onValueChange = viewModel::updateAmount,
                    label = { Text(stringResource(R.string.field_amount)) },
                    modifier = Modifier.fillMaxWidth().focusRequester(amountFocus),
                )
                AppTextField(
                    value = state.note,
                    onValueChange = viewModel::updateNote,
                    label = { Text(stringResource(R.string.field_note)) },
                    modifier = Modifier.fillMaxWidth(),
                    imeAction = ImeAction.Done,
                )
                val categoryMissing = state.categoryId == null || categories.none { it.id == state.categoryId }
                val selectedName = categories.find { it.id == state.categoryId }?.name
                    ?: stringResource(R.string.hint_select_category)
                ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
                    OutlinedTextField(
                        value = selectedName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.field_category)) },
                        isError = categoryMissing && state.error != null,
                        supportingText = if (categories.isEmpty()) {
                            { Text(stringResource(R.string.error_no_categories)) }
                        } else null,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) },
                        modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    )
                    ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    viewModel.updateCategory(cat.id)
                                    categoryExpanded = false
                                },
                            )
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.field_recurring))
                    Switch(checked = state.isRecurring, onCheckedChange = viewModel::updateRecurring)
                }
                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onBack) { Text(stringResource(R.string.action_cancel)) }
                    Button(
                        onClick = { viewModel.save() },
                        enabled = !state.isSaving,
                    ) {
                        Text(stringResource(R.string.action_save))
                    }
                }
            }
        }
    }
}
