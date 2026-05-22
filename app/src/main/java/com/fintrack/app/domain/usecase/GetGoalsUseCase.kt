package com.fintrack.app.domain.usecase

import com.fintrack.app.domain.model.Goal
import com.fintrack.app.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGoalsUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    operator fun invoke(): Flow<List<Goal>> {
        return repository.getAllGoals()
    }
}
