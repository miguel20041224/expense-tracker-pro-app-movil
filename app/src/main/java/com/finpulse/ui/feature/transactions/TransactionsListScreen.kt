package com.finpulse.ui.feature.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finpulse.R
import com.finpulse.domain.model.Category
import com.finpulse.domain.model.Transaction
import com.finpulse.domain.model.TransactionType
import com.finpulse.util.MoneyFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpensesListScreen(
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: ExpensesViewModel = hiltViewModel(),
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    TransactionsListScreen(
        title = stringResource(R.string.nav_expenses),
        type = TransactionType.EXPENSE,
        transactions = transactions,
        filter = filter,
        categories = categories,
        onMonthSelected = viewModel::setMonthOffset,
        onCategorySelected = viewModel::setCategory,
        onAdd = onAdd,
        onEdit = onEdit,
        onDelete = viewModel::delete,
    )
}

@Composable
fun IncomeListScreen(
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: IncomeViewModel = hiltViewModel(),
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    TransactionsListScreen(
        title = stringResource(R.string.nav_income),
        type = TransactionType.INCOME,
        transactions = transactions,
        filter = filter,
        categories = categories,
        onMonthSelected = viewModel::setMonthOffset,
        onCategorySelected = viewModel::setCategory,
        onAdd = onAdd,
        onEdit = onEdit,
        onDelete = viewModel::delete,
    )
}

@Composable
private fun TransactionsListScreen(
    title: String,
    type: TransactionType,
    transactions: List<Transaction>,
    filter: TransactionFilterState,
    categories: List<Category>,
    onMonthSelected: (Int) -> Unit,
    onCategorySelected: (Long?) -> Unit,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Transaction) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add))
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            TransactionFilters(filter, categories, onMonthSelected, onCategorySelected)
            if (transactions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (type == TransactionType.INCOME) stringResource(R.string.empty_income)
                        else stringResource(R.string.empty_expenses),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(transactions, key = { it.id }) { tx ->
                        TransactionItem(tx, onEdit, onDelete)
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionFilters(
    filter: TransactionFilterState,
    categories: List<Category>,
    onMonthSelected: (Int) -> Unit,
    onCategorySelected: (Long?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = filter.monthsAgo == 0,
                onClick = { onMonthSelected(0) },
                label = { Text(stringResource(R.string.filter_this_month)) },
            )
            FilterChip(
                selected = filter.monthsAgo == 1,
                onClick = { onMonthSelected(1) },
                label = { Text(stringResource(R.string.filter_last_month)) },
            )
        }
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = filter.categoryId == null,
                onClick = { onCategorySelected(null) },
                label = { Text(stringResource(R.string.filter_all_categories)) },
            )
            categories.forEach { cat ->
                FilterChip(
                    selected = filter.categoryId == cat.id,
                    onClick = { onCategorySelected(cat.id) },
                    label = { Text(cat.name) },
                )
            }
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    onEdit: (Long) -> Unit,
    onDelete: (Transaction) -> Unit,
) {
    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(transaction.occurredAt))
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEdit(transaction.id) },
        shape = RoundedCornerShape(14.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(transaction.categoryName ?: "—", style = MaterialTheme.typography.titleMedium)
                Text(transaction.note ?: date, style = MaterialTheme.typography.bodySmall)
                if (transaction.isRecurring) {
                    Text(stringResource(R.string.label_recurring), style = MaterialTheme.typography.labelSmall)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    MoneyFormat.formatMinor(transaction.amountMinor),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                IconButton(onClick = { onDelete(transaction) }) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete))
                }
            }
        }
    }
}
