package com.fintrack.app.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fintrack.app.domain.model.Goal
import com.fintrack.app.domain.usecase.AddGoalUseCase
import com.fintrack.app.domain.usecase.DeleteGoalUseCase
import com.fintrack.app.domain.usecase.GetGoalsUseCase
import com.fintrack.app.domain.usecase.UpdateGoalProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val addGoalUseCase: AddGoalUseCase,
    private val updateGoalProgressUseCase: UpdateGoalProgressUseCase,
    private val deleteGoalUseCase: DeleteGoalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState(isLoading = true))
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            getGoalsUseCase()
                .catch { e ->
                    // Handle error
                    _uiState.update { it.copy(isLoading = false) }
                }
                .collect { goals ->
                    val totalSaved = goals.sumOf { it.savedAmount }
                    val totalTarget = goals.sumOf { it.targetAmount }
                    
                    _uiState.update {
                        it.copy(
                            goals = goals,
                            totalSaved = totalSaved,
                            totalTarget = totalTarget,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            addGoalUseCase(goal)
        }
    }

    fun addSavingsToGoal(goalId: Long, amountToAdd: Double) {
        viewModelScope.launch {
            updateGoalProgressUseCase(goalId, amountToAdd)
        }
    }

    fun deleteGoal(goalId: Long) {
        viewModelScope.launch {
            deleteGoalUseCase(goalId)
        }
    }
}
