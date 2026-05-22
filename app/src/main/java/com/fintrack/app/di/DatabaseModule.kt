package com.fintrack.app.di

import android.app.Application
import androidx.room.Room
import com.fintrack.app.data.local.db.FinTrackDatabase
import com.fintrack.app.data.local.db.daos.BudgetDao
import com.fintrack.app.data.local.db.daos.CategoryDao
import com.fintrack.app.data.local.db.daos.GoalDao
import com.fintrack.app.data.local.db.daos.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): FinTrackDatabase {
        val db = Room.databaseBuilder(
            app,
            FinTrackDatabase::class.java,
            "fintrack_db"
        ).build()

        // Seed default categories
        CoroutineScope(Dispatchers.IO).launch {
            val dao = db.categoryDao
            val currentCategories = dao.getAllCategories().first()
            if (currentCategories.isEmpty()) {
                val defaults = listOf(
                    com.fintrack.app.data.local.db.entities.CategoryEntity(name = "Food", iconName = "food", colorHex = "#FF6B6B"),
                    com.fintrack.app.data.local.db.entities.CategoryEntity(name = "Transport", iconName = "transport", colorHex = "#4ECDC4"),
                    com.fintrack.app.data.local.db.entities.CategoryEntity(name = "Shopping", iconName = "shopping", colorHex = "#45B7D1"),
                    com.fintrack.app.data.local.db.entities.CategoryEntity(name = "Bills", iconName = "bills", colorHex = "#96CEB4"),
                    com.fintrack.app.data.local.db.entities.CategoryEntity(name = "Health", iconName = "health", colorHex = "#FFEAA7"),
                    com.fintrack.app.data.local.db.entities.CategoryEntity(name = "Entertainment", iconName = "entertainment", colorHex = "#DDA0DD"),
                    com.fintrack.app.data.local.db.entities.CategoryEntity(name = "Income", iconName = "income", colorHex = "#C6FF00"),
                    com.fintrack.app.data.local.db.entities.CategoryEntity(name = "Other", iconName = "other", colorHex = "#888888")
                )
                defaults.forEach { dao.insertCategory(it) }
            }
        }

        return db
    }

    @Provides
    @Singleton
    fun provideTransactionDao(db: FinTrackDatabase): TransactionDao = db.transactionDao

    @Provides
    @Singleton
    fun provideCategoryDao(db: FinTrackDatabase): CategoryDao = db.categoryDao

    @Provides
    @Singleton
    fun provideBudgetDao(db: FinTrackDatabase): BudgetDao = db.budgetDao

    @Provides
    @Singleton
    fun provideGoalDao(db: FinTrackDatabase): GoalDao = db.goalDao
}
