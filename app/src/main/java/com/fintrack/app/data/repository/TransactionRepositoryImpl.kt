package com.fintrack.app.data.repository

import com.fintrack.app.data.local.datasource.LocalTransactionDataSource
import com.fintrack.app.data.local.db.entities.TransactionEntity
import com.fintrack.app.domain.model.Transaction
import com.fintrack.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val localDataSource: LocalTransactionDataSource
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return localDataSource.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTransactionById(id: Long): Flow<Transaction?> {
        return localDataSource.getTransactionById(id).map { it?.toDomain() }
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        localDataSource.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        localDataSource.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        localDataSource.deleteTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransactionById(id: Long) {
        localDataSource.deleteTransactionById(id)
    }

    private fun TransactionEntity.toDomain() = Transaction(
        id = id,
        amount = amount,
        type = type,
        categoryId = categoryId,
        note = note,
        date = date,
        isRecurring = isRecurring
    )

    private fun Transaction.toEntity() = TransactionEntity(
        id = id,
        amount = amount,
        type = type,
        categoryId = categoryId,
        note = note,
        date = date,
        isRecurring = isRecurring
    )
}
