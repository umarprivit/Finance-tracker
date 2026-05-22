package com.fintrack.app.ui.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fintrack.app.domain.model.Goal
import com.fintrack.app.ui.components.BudgetProgressBar
import com.fintrack.app.ui.theme.*
import java.text.NumberFormat
import java.util.*
import kotlin.math.max
import com.fintrack.app.util.CurrencyFormatter

@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isAddGoalSheetVisible by remember { mutableStateOf(false) }
    var goalToAddSavingsTo by remember { mutableStateOf<Long?>(null) }
    var goalToDelete by remember { mutableStateOf<Long?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Goals",
                style = Typography.headlineLarge.copy(color = TextPrimary),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Top stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total saved chip
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariant)
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Total saved",
                            style = Typography.bodySmall.copy(color = TextSecondary)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyFormatter.formatAmount(uiState.totalSaved),
                            style = Typography.titleMedium.copy(color = AccentLime)
                        )
                    }
                }

                // Remaining chip
                val remaining = max(0.0, uiState.totalTarget - uiState.totalSaved)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariant)
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Remaining",
                            style = Typography.bodySmall.copy(color = TextSecondary)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyFormatter.formatAmount(remaining),
                            style = Typography.titleMedium.copy(color = TextPrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentLime)
                }
            } else if (uiState.goals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No goals yet",
                            style = Typography.titleLarge.copy(color = TextPrimary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start saving for something",
                            style = Typography.bodyMedium.copy(color = TextDisabled)
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(uiState.goals, key = { it.id }) { goal ->
                        GoalCard(
                            goal = goal,
                            onTap = { goalToAddSavingsTo = goal.id },
                            onLongPress = { goalToDelete = goal.id }
                        )
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { isAddGoalSheetVisible = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp),
            containerColor = AccentLime,
            contentColor = Background
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Goal")
        }

        // Dialogs & Sheets
        AddGoalBottomSheet(
            isVisible = isAddGoalSheetVisible,
            onDismiss = { isAddGoalSheetVisible = false },
            onSave = { newGoal -> viewModel.addGoal(newGoal) }
        )

        goalToAddSavingsTo?.let { goalId ->
            AddToGoalDialog(
                goalId = goalId,
                onDismiss = { goalToAddSavingsTo = null },
                onConfirm = { id, amount -> viewModel.addSavingsToGoal(id, amount) }
            )
        }

        goalToDelete?.let { goalId ->
            AlertDialog(
                onDismissRequest = { goalToDelete = null },
                title = { Text("Delete Goal?", color = TextPrimary) },
                text = { Text("Are you sure you want to delete this goal? This action cannot be undone.", color = TextSecondary) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteGoal(goalId)
                        goalToDelete = null
                    }) {
                        Text("Delete", color = ErrorRed)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { goalToDelete = null }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = SurfaceVariant
            )
        }
    }
}

@Composable
fun GoalCard(
    goal: Goal,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    val percent = if (goal.targetAmount > 0) (goal.savedAmount / goal.targetAmount).toFloat() else 0f
    val isCompleted = percent >= 1f
    val currentTime = System.currentTimeMillis()
    val diffMillis = goal.deadline - currentTime
    val daysLeft = diffMillis / (1000 * 60 * 60 * 24)

    val borderColor = if (isCompleted) AccentLime else BorderColor

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariant)
            .border(if (isCompleted) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap() },
                    onLongPress = { onLongPress() }
                )
            }
            .padding(16.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                GoalJarCanvas(
                    savedPercent = percent,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentLime)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Completed",
                            style = Typography.bodySmall.copy(
                                color = Background,
                                fontWeight = FontWeight.W600,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = goal.title,
                style = Typography.titleMedium.copy(color = TextPrimary),
                maxLines = 1
            )
            
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Target: ${CurrencyFormatter.formatAmount(goal.targetAmount)}",
                style = Typography.bodySmall.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${CurrencyFormatter.formatAmount(goal.savedAmount)} saved",
                style = Typography.bodySmall.copy(color = AccentLime)
            )

            Spacer(modifier = Modifier.height(12.dp))

            BudgetProgressBar(percent = percent)

            Spacer(modifier = Modifier.height(8.dp))

            val deadlineText = if (daysLeft in 0..7) {
                "$daysLeft days left"
            } else if (daysLeft < 0) {
                "Overdue"
            } else {
                val cal = Calendar.getInstance().apply { timeInMillis = goal.deadline }
                val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                "Due ${sdf.format(cal.time)}"
            }

            val deadlineColor = if (daysLeft in 0..7 || daysLeft < 0) WarningAmber else TextDisabled

            Text(
                text = deadlineText,
                style = Typography.bodySmall.copy(color = deadlineColor, fontSize = 10.sp)
            )
        }
    }
}
