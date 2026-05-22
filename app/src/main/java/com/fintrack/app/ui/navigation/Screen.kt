package com.fintrack.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.AutoGraph)
    object Transactions : Screen("transactions", "Transactions", Icons.Filled.SwapVert)
    object Budgets : Screen("budgets", "Budgets", Icons.Filled.PieChart)
    object Goals : Screen("goals", "Goals", Icons.Filled.Flag)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}
