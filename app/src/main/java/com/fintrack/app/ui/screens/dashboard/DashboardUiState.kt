package com.fintrack.app.ui.screens.dashboard

import com.fintrack.app.domain.usecase.DashboardSummary

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val summary: DashboardSummary) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}
