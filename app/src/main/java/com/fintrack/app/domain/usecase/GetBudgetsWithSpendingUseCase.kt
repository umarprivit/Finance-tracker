package com.fintrack.app.domain.usecase

import com.fintrack.app.domain.model.Budget
import com.fintrack.app.domain.model.Category
import com.fintrack.app.domain.repository.BudgetRepository
import com.fintrack.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class BudgetWithSpending(
    val budget: Budget,
    val category: Category?,
    val spentAmount: Double,
    val percentUsed: Float
)

class GetBudgetsWithSpendingUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Flow<List<BudgetWithSpending>> {
        return combine(
            budgetRepository.getAllBudgets(),
            categoryRepository.getAllCategories()
        ) { budgets, categories ->
            budgets.map { budget ->
                val category = categories.find { it.id == budget.categoryId }
                val spent = budget.spentAmount
                val limit = budget.limitAmount
                val percent = if (limit > 0) (spent / limit).toFloat() else 0f
                BudgetWithSpending(
                    budget = budget,
                    category = category,
                    spentAmount = spent,
                    percentUsed = percent
                )
            }.sortedByDescending { it.percentUsed } // sort by highest spent percent
        }
    }
}
