package com.fintrack.app.ui.screens.budgets

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fintrack.app.domain.usecase.BudgetWithSpending
import com.fintrack.app.ui.components.BudgetProgressBar
import com.fintrack.app.ui.components.SetBudgetBottomSheet
import com.fintrack.app.ui.theme.*
import java.text.NumberFormat
import java.util.Locale
import com.fintrack.app.util.CurrencyFormatter

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(
    viewModel: BudgetsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isSheetOpen by remember { mutableStateOf(false) }
    var budgetToDelete by remember { mutableStateOf<BudgetWithSpending?>(null) }

    val fabScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        fabScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f)
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars),
        containerColor = Background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isSheetOpen = true },
                containerColor = AccentLime,
                contentColor = Background,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.scale(fabScale.value)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Budget")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Text(
                text = "Budgets",
                style = Typography.titleLarge.copy(color = TextPrimary),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            if (uiState.budgets.isEmpty() && !uiState.isLoading) {
                // Empty state
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Set your limits",
                        style = Typography.titleLarge.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Track where your money goes",
                        style = Typography.bodyMedium.copy(color = TextDisabled)
                    )
                }
            } else if (!uiState.isLoading) {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Summary Card
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceVariant)
                                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                                .padding(20.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Monthly Budget",
                                    style = Typography.titleLarge.copy(color = TextPrimary)
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                val remaining = uiState.totalBudgeted - uiState.totalSpent
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Column {
                                        Text(text = "Spent", style = Typography.labelSmall.copy(color = TextSecondary))
                                        Text(
                                            text = CurrencyFormatter.formatAmount(uiState.totalSpent),
                                            style = Typography.headlineMedium.copy(color = AccentLime)
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(text = "Remaining", style = Typography.labelSmall.copy(color = TextSecondary))
                                        Text(
                                            text = CurrencyFormatter.formatAmount(remaining.coerceAtLeast(0.0)),
                                            style = Typography.headlineMedium.copy(color = TextPrimary)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                
                                val totalPercent = if (uiState.totalBudgeted > 0) (uiState.totalSpent / uiState.totalBudgeted).toFloat() else 0f
                                BudgetProgressBar(percent = totalPercent)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Budget List
                    items(uiState.budgets, key = { it.budget.id }) { item ->
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val pulseColor by infiniteTransition.animateColor(
                            initialValue = BorderColor,
                            targetValue = ErrorRed,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulseColor"
                        )

                        val borderColor = if (item.percentUsed > 1.0f) pulseColor else BorderColor

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceVariant)
                                .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                                .combinedClickable(
                                    onClick = { /* Could open details */ },
                                    onLongClick = { budgetToDelete = item }
                                )
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val catColor = try {
                                            Color(android.graphics.Color.parseColor(item.category?.colorHex ?: "#888888"))
                                        } catch (e: Exception) {
                                            Color.Gray
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(catColor)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = item.category?.name ?: "Unknown",
                                            style = Typography.headlineMedium.copy(color = TextPrimary)
                                        )
                                    }

                                    Text(
                                        text = "${CurrencyFormatter.formatAmount(item.spentAmount)} / ${CurrencyFormatter.formatAmount(item.budget.limitAmount)}",
                                        style = Typography.bodyMedium.copy(color = TextSecondary)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                BudgetProgressBar(percent = item.percentUsed)

                                Spacer(modifier = Modifier.height(8.dp))

                                val percentText = "${(item.percentUsed * 100).toInt()}%"
                                Text(
                                    text = percentText,
                                    style = Typography.labelSmall.copy(color = TextSecondary),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
                    }
                }
            }
        }
    }

    if (isSheetOpen) {
        SetBudgetBottomSheet(
            isVisible = isSheetOpen,
            onDismiss = { isSheetOpen = false },
            categories = uiState.categories,
            onSave = { budget -> viewModel.setBudget(budget) }
        )
    }

    if (budgetToDelete != null) {
        AlertDialog(
            onDismissRequest = { budgetToDelete = null },
            containerColor = Surface,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = {
                Text(text = "Delete Budget?", style = Typography.titleLarge)
            },
            text = {
                Text(
                    text = "Are you sure you want to delete the budget for ${budgetToDelete?.category?.name}? This action cannot be undone.",
                    style = Typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteBudget(budgetToDelete!!.budget.id)
                        budgetToDelete = null
                    }
                ) {
                    Text("Delete", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { budgetToDelete = null }) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        )
    }
}
