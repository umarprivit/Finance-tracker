package com.fintrack.app.domain.usecase

import com.fintrack.app.domain.model.Transaction
import com.fintrack.app.domain.model.TransactionType
import com.fintrack.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(
        month: Int? = null,
        year: Int? = null,
        type: TransactionType? = null,
        categoryId: Long? = null
    ): Flow<List<Transaction>> {
        return repository.getAllTransactions().map { transactions ->
            transactions.filter { transaction ->
                var matches = true
                
                if (type != null) {
                    matches = matches && transaction.type == type
                }
                
                if (categoryId != null) {
                    matches = matches && transaction.categoryId == categoryId
                }
                
                if (month != null || year != null) {
                    val calendar = Calendar.getInstance().apply { timeInMillis = transaction.date }
                    if (month != null) {
                        matches = matches && calendar.get(Calendar.MONTH) == month
                    }
                    if (year != null) {
                        matches = matches && calendar.get(Calendar.YEAR) == year
                    }
                }
                
                matches
            }
        }
    }
}
