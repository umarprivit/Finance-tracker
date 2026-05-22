package com.fintrack.app.domain.usecase

import com.fintrack.app.domain.model.Budget
import com.fintrack.app.domain.repository.BudgetRepository
import javax.inject.Inject

class SetBudgetUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(budget: Budget) {
        if (budget.id == 0L) {
            repository.insertBudget(budget)
        } else {
            repository.updateBudget(budget)
        }
    }
}
