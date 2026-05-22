package com.fintrack.app.ui.screens.goals

import com.fintrack.app.domain.model.Goal

data class GoalsUiState(
    val goals: List<Goal> = emptyList(),
    val totalSaved: Double = 0.0,
    val totalTarget: Double = 0.0,
    val isLoading: Boolean = true
)
