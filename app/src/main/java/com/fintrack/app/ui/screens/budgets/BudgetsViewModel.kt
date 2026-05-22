package com.fintrack.app.ui.screens.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fintrack.app.domain.model.Budget
import com.fintrack.app.domain.usecase.DeleteBudgetUseCase
import com.fintrack.app.domain.usecase.GetBudgetsWithSpendingUseCase
import com.fintrack.app.domain.usecase.GetCategoriesUseCase
import com.fintrack.app.domain.usecase.SetBudgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    getBudgetsWithSpendingUseCase: GetBudgetsWithSpendingUseCase,
    getCategoriesUseCase: GetCategoriesUseCase,
    private val setBudgetUseCase: SetBudgetUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase
) : ViewModel() {

    val uiState: StateFlow<BudgetsUiState> = combine(
        getBudgetsWithSpendingUseCase(),
        getCategoriesUseCase()
    ) { budgetsWithSpending, categories ->
        val totalBudgeted = budgetsWithSpending.sumOf { it.budget.limitAmount }
        val totalSpent = budgetsWithSpending.sumOf { it.spentAmount }
        
        BudgetsUiState(
            totalBudgeted = totalBudgeted,
            totalSpent = totalSpent,
            budgets = budgetsWithSpending,
            categories = categories,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudgetsUiState(isLoading = true)
    )

    fun setBudget(budget: Budget) {
        viewModelScope.launch {
            setBudgetUseCase(budget)
        }
    }

    fun deleteBudget(id: Long) {
        viewModelScope.launch {
            deleteBudgetUseCase(id)
        }
    }
}
