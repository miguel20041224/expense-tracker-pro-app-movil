package com.finpulse.ui.feature.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpulse.domain.model.Transaction
import com.finpulse.domain.model.TransactionType
import com.finpulse.domain.repository.CategoryRepository
import com.finpulse.domain.repository.TransactionRepository
import com.finpulse.domain.usecase.RefreshFinancialIntelligenceUseCase
import com.finpulse.util.AmountParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionFormUiState(
    val amountInput: String = "",
    val note: String = "",
    val categoryId: Long? = null,
    val isRecurring: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
)

@HiltViewModel
class TransactionFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val refreshIntelligence: RefreshFinancialIntelligenceUseCase,
) : ViewModel() {
    val type: TransactionType = when (savedStateHandle.get<String>("type")) {
        TransactionType.INCOME.storageValue -> TransactionType.INCOME
        else -> TransactionType.EXPENSE
    }
    private val transactionId: Long = savedStateHandle.get<Long>("id") ?: -1L

    val categories = categoryRepository.observeByType(type)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _uiState = MutableStateFlow(TransactionFormUiState())
    val uiState: StateFlow<TransactionFormUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val cats = categoryRepository.observeByType(type).first()
            if (cats.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No hay categorías. Cierra y abre la app de nuevo.",
                    )
                }
                return@launch
            }
            val defaultCategoryId = cats.first().id
            if (transactionId > 0) {
                val tx = transactionRepository.getById(transactionId)
                if (tx != null) {
                    _uiState.update {
                        it.copy(
                            amountInput = AmountParser.formatInputFromMinor(tx.amountMinor),
                            note = tx.note.orEmpty(),
                            categoryId = tx.categoryId ?: defaultCategoryId,
                            isRecurring = tx.isRecurring,
                            isLoading = false,
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "No encontrado") }
                }
            } else {
                _uiState.update {
                    it.copy(categoryId = defaultCategoryId, isLoading = false)
                }
            }
        }
    }

    fun updateAmount(v: String) = _uiState.update { it.copy(amountInput = v, error = null) }
    fun updateNote(v: String) = _uiState.update { it.copy(note = v) }
    fun updateCategory(id: Long?) = _uiState.update { it.copy(categoryId = id, error = null) }
    fun updateRecurring(v: Boolean) = _uiState.update { it.copy(isRecurring = v) }

    fun save() {
        val state = _uiState.value
        val minor = AmountParser.parseToMinor(state.amountInput)
        if (minor == null || minor <= 0) {
            _uiState.update { it.copy(error = "Monto inválido") }
            return
        }
        val categoryId = state.categoryId
        if (categoryId == null || categoryId <= 0) {
            _uiState.update { it.copy(error = "Selecciona una categoría") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            runCatching {
                transactionRepository.save(
                    Transaction(
                        id = if (transactionId > 0) transactionId else 0L,
                        accountId = 0L,
                        categoryId = categoryId,
                        categoryName = null,
                        type = type,
                        amountMinor = minor,
                        note = state.note.ifBlank { null },
                        occurredAt = System.currentTimeMillis(),
                        isRecurring = state.isRecurring,
                        recurrenceRule = if (state.isRecurring) "MONTHLY" else null,
                    ),
                )
                refreshIntelligence()
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, saved = true) }
            }.onFailure { e ->
                val message = when {
                    e.message?.contains("FOREIGN KEY", ignoreCase = true) == true ->
                        "No se pudo guardar. Reinicia la app o elige otra categoría."
                    else -> e.message ?: "Error"
                }
                _uiState.update { it.copy(isSaving = false, error = message) }
            }
        }
    }
}
