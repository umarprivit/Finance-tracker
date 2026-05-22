package com.fintrack.app.ui.screens.budgets

import com.fintrack.app.domain.model.Category
import com.fintrack.app.domain.usecase.BudgetWithSpending

data class BudgetsUiState(
    val totalBudgeted: Double = 0.0,
    val totalSpent: Double = 0.0,
    val budgets: List<BudgetWithSpending> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true
)
