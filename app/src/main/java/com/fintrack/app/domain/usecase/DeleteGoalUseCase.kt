package com.fintrack.app.domain.usecase

import com.fintrack.app.domain.repository.GoalRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class DeleteGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(id: Long) {
        val goal = repository.getGoalById(id).firstOrNull()
        if (goal != null) {
            repository.deleteGoal(goal)
        }
    }
}
