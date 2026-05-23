package com.finpulse.ui.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpulse.domain.model.Transaction
import com.finpulse.domain.model.TransactionType
import com.finpulse.domain.repository.CategoryRepository
import com.finpulse.domain.repository.TransactionRepository
import com.finpulse.domain.usecase.RefreshFinancialIntelligenceUseCase
import com.finpulse.util.DateRanges
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionFilterState(
    val monthsAgo: Int = 0,
    val categoryId: Long? = null,
)

abstract class BaseTransactionsViewModel(
    val type: TransactionType,
    private val transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository,
    private val refreshIntelligence: RefreshFinancialIntelligenceUseCase,
) : ViewModel() {
    private val _filter = MutableStateFlow(TransactionFilterState())
    val filter: StateFlow<TransactionFilterState> = _filter.asStateFlow()

    val categories = categoryRepository.observeByType(type)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val transactions = _filter.flatMapLatest { f ->
        val range = DateRanges.monthRange(f.monthsAgo)
        transactionRepository.observeFiltered(type, range.startEpochMilli, range.endEpochMilli, f.categoryId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setMonthOffset(monthsAgo: Int) = _filter.update { it.copy(monthsAgo = monthsAgo) }
    fun setCategory(categoryId: Long?) = _filter.update { it.copy(categoryId = categoryId) }

    fun delete(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.delete(transaction.id)
            refreshIntelligence()
        }
    }
}
