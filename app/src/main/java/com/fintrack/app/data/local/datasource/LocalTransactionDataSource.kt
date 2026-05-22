package com.fintrack.app.data.local.datasource

import com.fintrack.app.data.local.db.daos.TransactionDao
import com.fintrack.app.data.local.db.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalTransactionDataSource @Inject constructor(
    private val transactionDao: TransactionDao
) {
    fun getAllTransactions(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    
    fun getTransactionById(id: Long): Flow<TransactionEntity?> = transactionDao.getTransactionById(id)
    
    suspend fun insertTransaction(transaction: TransactionEntity) = transactionDao.insertTransaction(transaction)
    
    suspend fun updateTransaction(transaction: TransactionEntity) = transactionDao.updateTransaction(transaction)
    
    suspend fun deleteTransaction(transaction: TransactionEntity) = transactionDao.deleteTransaction(transaction)

    suspend fun deleteTransactionById(id: Long) = transactionDao.deleteTransactionById(id)
}
