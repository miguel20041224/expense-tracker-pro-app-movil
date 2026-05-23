package com.finpulse.ui.feature.transactions

import com.finpulse.domain.model.TransactionType
import com.finpulse.domain.repository.CategoryRepository
import com.finpulse.domain.repository.TransactionRepository
import com.finpulse.domain.usecase.RefreshFinancialIntelligenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IncomeViewModel @Inject constructor(
    transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository,
    refreshIntelligence: RefreshFinancialIntelligenceUseCase,
) : BaseTransactionsViewModel(TransactionType.INCOME, transactionRepository, categoryRepository, refreshIntelligence)
