package com.fintrack.app.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fintrack.app.domain.model.Transaction
import com.fintrack.app.domain.model.TransactionType
import com.fintrack.app.domain.usecase.AddTransactionUseCase
import com.fintrack.app.domain.usecase.DeleteTransactionUseCase
import com.fintrack.app.domain.usecase.GetCategoriesUseCase
import com.fintrack.app.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(FilterType.ALL)

    val uiState: StateFlow<TransactionsUiState> = combine(
        getTransactionsUseCase(),
        getCategoriesUseCase(),
        _selectedFilter
    ) { transactions, categories, filter ->
        val filteredTransactions = filterTransactions(transactions, filter)
        val transactionsWithCategories = filteredTransactions.map { transaction ->
            TransactionWithCategory(
                transaction = transaction,
                category = categories.find { it.id == transaction.categoryId }
            )
        }.sortedByDescending { it.transaction.date }

        TransactionsUiState(
            transactions = transactionsWithCategories,
            categories = categories,
            isLoading = false,
            selectedFilter = filter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionsUiState(isLoading = true)
    )

    private fun filterTransactions(transactions: List<Transaction>, filter: FilterType): List<Transaction> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        
        calendar.add(Calendar.MONTH, -1)
        val lastMonth = calendar.get(Calendar.MONTH)
        val lastMonthYear = calendar.get(Calendar.YEAR)
        
        return transactions.filter { transaction ->
            when (filter) {
                FilterType.ALL -> true
                FilterType.INCOME -> transaction.type == TransactionType.INCOME
                FilterType.EXPENSE -> transaction.type == TransactionType.EXPENSE
                FilterType.THIS_MONTH -> {
                    val txCalendar = Calendar.getInstance().apply { timeInMillis = transaction.date }
                    txCalendar.get(Calendar.MONTH) == currentMonth && txCalendar.get(Calendar.YEAR) == currentYear
                }
                FilterType.LAST_MONTH -> {
                    val txCalendar = Calendar.getInstance().apply { timeInMillis = transaction.date }
                    txCalendar.get(Calendar.MONTH) == lastMonth && txCalendar.get(Calendar.YEAR) == lastMonthYear
                }
            }
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            addTransactionUseCase(transaction)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            deleteTransactionUseCase(id)
        }
    }

    fun setFilter(filter: FilterType) {
        _selectedFilter.update { filter }
    }
}
