package com.fintrack.app.domain.usecase

import com.fintrack.app.domain.model.Category
import com.fintrack.app.domain.model.Transaction
import com.fintrack.app.domain.model.TransactionType
import com.fintrack.app.domain.repository.CategoryRepository
import com.fintrack.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class CategorySpend(
    val category: Category,
    val amount: Double,
    val percentage: Float
)

data class DashboardSummary(
    val totalBalance: Double,
    val monthlyIncome: Double,
    val monthlyExpense: Double,
    val monthlyBudget: Double,
    val topCategories: List<CategorySpend>,
    val recentTransactions: List<Transaction>,
    val categoryMap: Map<Long, Category>
)

class GetDashboardSummaryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: com.fintrack.app.domain.repository.BudgetRepository
) {
    operator fun invoke(): Flow<DashboardSummary> {
        return combine(
            transactionRepository.getAllTransactions(),
            categoryRepository.getAllCategories(),
            budgetRepository.getAllBudgets()
        ) { transactions, categories, budgets ->
            val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            val totalBalance = totalIncome - totalExpense

            val monthlyIncome = totalIncome // Simplification
            val monthlyExpense = totalExpense

            val monthlyBudget = budgets.sumOf { it.limitAmount }

            val categoryMap = categories.associateBy { it.id }

            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
            val expensesByCategory = expenses.groupBy { it.categoryId }
            
            val topCategories = expensesByCategory.mapNotNull { (categoryId, categoryTransactions) ->
                val category = categoryMap[categoryId] ?: return@mapNotNull null
                val amount = categoryTransactions.sumOf { it.amount }
                val percentage = if (totalExpense > 0) (amount / totalExpense).toFloat() else 0f
                CategorySpend(category, amount, percentage)
            }.sortedByDescending { it.amount }

            DashboardSummary(
                totalBalance = totalBalance,
                monthlyIncome = monthlyIncome,
                monthlyExpense = monthlyExpense,
                monthlyBudget = monthlyBudget,
                topCategories = topCategories,
                recentTransactions = transactions.take(5),
                categoryMap = categoryMap
            )
        }
    }
}
