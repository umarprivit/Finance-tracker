package com.fintrack.app.ui.screens.dashboard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fintrack.app.domain.usecase.DashboardSummary
import com.fintrack.app.ui.components.AnimatedBalanceText
import com.fintrack.app.ui.components.CategoryChip
import com.fintrack.app.ui.components.ShimmerBox
import com.fintrack.app.ui.components.SpendingRingChart
import com.fintrack.app.ui.components.TransactionRow
import com.fintrack.app.ui.theme.AccentLime
import com.fintrack.app.ui.theme.BorderColor
import com.fintrack.app.ui.theme.ErrorRed
import com.fintrack.app.ui.theme.SurfaceVariant
import com.fintrack.app.ui.theme.TextPrimary
import com.fintrack.app.ui.theme.TextSecondary
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.fintrack.app.util.CurrencyFormatter

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(16.dp)
    ) {
        when (val state = uiState) {
            is DashboardUiState.Loading -> {
                DashboardShimmer()
            }
            is DashboardUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = ErrorRed)
                }
            }
            is DashboardUiState.Success -> {
                DashboardContent(summary = state.summary)
            }
        }
    }
}

@Composable
fun DashboardShimmer() {
    Column(modifier = Modifier.fillMaxSize()) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ShimmerBox(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            ShimmerBox(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
        }
    }
}

@Composable
fun DashboardContent(summary: DashboardSummary) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Section 1 — Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Good morning",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    Text(
                        text = dateFormat.format(Date()),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                AnimatedBalanceText(
                    targetValue = summary.totalBalance,
                    textStyle = MaterialTheme.typography.displayLarge
                )
            }
        }

        // Section 2 — Income / Expense cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Income Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(SurfaceVariant, shape = MaterialTheme.shapes.medium)
                        .border(1.dp, BorderColor, shape = MaterialTheme.shapes.medium)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropUp,
                        contentDescription = "Income",
                        tint = AccentLime,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Income",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    AnimatedBalanceText(
                        targetValue = summary.monthlyIncome,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
                    )
                }

                // Expense Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(SurfaceVariant, shape = MaterialTheme.shapes.medium)
                        .border(1.dp, BorderColor, shape = MaterialTheme.shapes.medium)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expenses",
                        tint = ErrorRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Expenses",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    AnimatedBalanceText(
                        targetValue = summary.monthlyExpense,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
                    )
                }
            }
        }

        // Section 3 — Spending ring
        item {

            val remaining = summary.monthlyBudget - summary.monthlyExpense
            val spentPercent = if (summary.monthlyBudget > 0) {
                (summary.monthlyExpense / summary.monthlyBudget).toFloat().coerceIn(0f, 1f)
            } else {
                0f
            }

            val isOverspent = summary.monthlyExpense > summary.monthlyBudget
            val haptic = LocalHapticFeedback.current
            LaunchedEffect(isOverspent) {
                if (isOverspent) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }

            val pulseTransition = rememberInfiniteTransition(label = "pulse")
            val pulseAlpha by pulseTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseAlpha"
            )
            val borderColor = if (isOverspent) ErrorRed.copy(alpha = pulseAlpha) else BorderColor

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceVariant, shape = MaterialTheme.shapes.medium)
                    .border(if (isOverspent) 2.dp else 1.dp, borderColor, shape = MaterialTheme.shapes.medium)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SpendingRingChart(
                    spentPercent = spentPercent,
                    accentColor = if (spentPercent > 0.9f) ErrorRed else AccentLime
                )
                
                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Monthly Budget",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Spent: ${CurrencyFormatter.formatAmount(summary.monthlyExpense)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Remaining: ${CurrencyFormatter.formatAmount(remaining)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AccentLime
                    )
                }
            }
        }

        // Section 4 — Top categories
        if (summary.topCategories.isNotEmpty()) {
            item {
                Text(
                    text = "Top spending",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(summary.topCategories) { categorySpend ->
                        CategoryChip(
                            name = categorySpend.category.name,
                            colorHex = categorySpend.category.colorHex,
                            amount = categorySpend.amount
                        )
                    }
                }
            }
        }

        // Section 5 — Recent transactions
        if (summary.recentTransactions.isNotEmpty()) {
            item {
                Text(
                    text = "Recent",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            itemsIndexed(summary.recentTransactions) { index, transaction ->
                val category = summary.categoryMap[transaction.categoryId]
                TransactionRow(
                    transaction = transaction,
                    category = category,
                    index = index
                )
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transactions yet.", color = TextSecondary)
                }
            }
        }
    }
}
