package com.fintrack.app.domain.usecase

import com.fintrack.app.domain.model.Goal
import com.fintrack.app.domain.repository.GoalRepository
import javax.inject.Inject

class AddGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(goal: Goal) {
        repository.insertGoal(goal)
    }
}
