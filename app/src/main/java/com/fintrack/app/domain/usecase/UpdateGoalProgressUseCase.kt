package com.fintrack.app.domain.usecase

import com.fintrack.app.domain.repository.GoalRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UpdateGoalProgressUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(goalId: Long, amountToAdd: Double) {
        val goal = repository.getGoalById(goalId).firstOrNull()
        if (goal != null) {
            val updatedGoal = goal.copy(savedAmount = goal.savedAmount + amountToAdd)
            repository.updateGoal(updatedGoal)
        }
    }
}
