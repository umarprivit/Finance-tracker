package com.fintrack.app.data.repository

import com.fintrack.app.data.local.db.daos.BudgetDao
import com.fintrack.app.data.local.db.daos.TransactionDao
import com.fintrack.app.data.local.db.entities.BudgetEntity
import com.fintrack.app.domain.model.Budget
import com.fintrack.app.domain.model.TransactionType
import com.fintrack.app.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao
) : BudgetRepository {

    override fun getAllBudgets(): Flow<List<Budget>> {
        return combine(
            budgetDao.getAllBudgets(),
            transactionDao.getAllTransactions()
        ) { budgetEntities, transactionEntities ->
            val calendar = Calendar.getInstance()
            budgetEntities.map { budgetEntity ->
                val spent = transactionEntities.filter { tx ->
                    // Only count expenses against the budget
                    if (tx.type != TransactionType.EXPENSE) {
                        return@filter false
                    }
                    if (tx.categoryId != budgetEntity.categoryId) {
                        return@filter false
                    }
                    calendar.timeInMillis = tx.date
                    val txMonth = calendar.get(Calendar.MONTH)
                    val txYear = calendar.get(Calendar.YEAR)
                    txMonth == budgetEntity.month && txYear == budgetEntity.year
                }.sumOf { it.amount }
                
                budgetEntity.toDomain().copy(spentAmount = spent)
            }
        }
    }

    override fun getBudgetById(id: Long): Flow<Budget?> {
        return budgetDao.getBudgetById(id).map { it?.toDomain() }
    }

    override suspend fun insertBudget(budget: Budget) {
        budgetDao.insertBudget(budget.toEntity())
    }

    override suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget.toEntity())
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget.toEntity())
    }

    override suspend fun deleteBudgetById(id: Long) {
        budgetDao.deleteBudgetById(id)
    }

    private fun BudgetEntity.toDomain() = Budget(
        id = id,
        categoryId = categoryId,
        limitAmount = limitAmount,
        spentAmount = spentAmount,
        month = month,
        year = year
    )

    private fun Budget.toEntity() = BudgetEntity(
        id = id,
        categoryId = categoryId,
        limitAmount = limitAmount,
        spentAmount = spentAmount,
        month = month,
        year = year
    )
}
