package com.fintrack.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fintrack.app.data.local.db.daos.BudgetDao
import com.fintrack.app.data.local.db.daos.CategoryDao
import com.fintrack.app.data.local.db.daos.GoalDao
import com.fintrack.app.data.local.db.daos.TransactionDao
import com.fintrack.app.data.local.db.entities.BudgetEntity
import com.fintrack.app.data.local.db.entities.CategoryEntity
import com.fintrack.app.data.local.db.entities.GoalEntity
import com.fintrack.app.data.local.db.entities.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        GoalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FinTrackDatabase : RoomDatabase() {
    abstract val transactionDao: TransactionDao
    abstract val categoryDao: CategoryDao
    abstract val budgetDao: BudgetDao
    abstract val goalDao: GoalDao
}
