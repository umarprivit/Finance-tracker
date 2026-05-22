package com.fintrack.app.di

import com.fintrack.app.data.repository.BudgetRepositoryImpl
import com.fintrack.app.data.repository.GoalRepositoryImpl
import com.fintrack.app.data.repository.TransactionRepositoryImpl
import com.fintrack.app.domain.repository.BudgetRepository
import com.fintrack.app.domain.repository.GoalRepository
import com.fintrack.app.domain.repository.TransactionRepository
import com.fintrack.app.data.repository.CategoryRepositoryImpl
import com.fintrack.app.domain.repository.CategoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(
        budgetRepositoryImpl: BudgetRepositoryImpl
    ): BudgetRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(
        goalRepositoryImpl: GoalRepositoryImpl
    ): GoalRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository
}
