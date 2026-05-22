package com.fintrack.app.ui.screens.transactions

import com.fintrack.app.domain.model.Category
import com.fintrack.app.domain.model.Transaction

enum class FilterType {
    ALL, INCOME, EXPENSE, THIS_MONTH, LAST_MONTH
}

data class TransactionWithCategory(
    val transaction: Transaction,
    val category: Category?
)

data class TransactionsUiState(
    val transactions: List<TransactionWithCategory> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val selectedFilter: FilterType = FilterType.ALL,
    val errorMessage: String? = null
)
