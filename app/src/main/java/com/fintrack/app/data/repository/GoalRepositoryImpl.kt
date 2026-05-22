package com.fintrack.app.data.repository

import com.fintrack.app.data.local.db.daos.GoalDao
import com.fintrack.app.data.local.db.entities.GoalEntity
import com.fintrack.app.domain.model.Goal
import com.fintrack.app.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {

    override fun getAllGoals(): Flow<List<Goal>> {
        return goalDao.getAllGoals().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getGoalById(id: Long): Flow<Goal?> {
        return goalDao.getGoalById(id).map { it?.toDomain() }
    }

    override suspend fun insertGoal(goal: Goal) {
        goalDao.insertGoal(goal.toEntity())
    }

    override suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoal(goal.toEntity())
    }

    override suspend fun deleteGoal(goal: Goal) {
        goalDao.deleteGoal(goal.toEntity())
    }

    private fun GoalEntity.toDomain() = Goal(
        id = id,
        title = title,
        targetAmount = targetAmount,
        savedAmount = savedAmount,
        deadline = deadline,
        iconName = iconName
    )

    private fun Goal.toEntity() = GoalEntity(
        id = id,
        title = title,
        targetAmount = targetAmount,
        savedAmount = savedAmount,
        deadline = deadline,
        iconName = iconName
    )
}
