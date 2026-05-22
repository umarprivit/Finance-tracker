package com.fintrack.app.ui.screens.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fintrack.app.R
import com.fintrack.app.ui.components.AddTransactionBottomSheet
import com.fintrack.app.ui.components.ShimmerBox
import com.fintrack.app.ui.components.TransactionRow
import com.fintrack.app.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isFilterOpen by remember { mutableStateOf(false) }
    var isSheetOpen by remember { mutableStateOf(false) }

    val fabScale = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        fabScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f)
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars),
        containerColor = Background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    isSheetOpen = true
                },
                containerColor = AccentLime,
                contentColor = Background,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.scale(fabScale.value)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transactions",
                    style = Typography.titleLarge.copy(color = TextPrimary)
                )
                IconButton(onClick = { isFilterOpen = !isFilterOpen }) {
                    // Filter icon - using a basic text/icon since standard filter icon might need specific import
                    Text(text = "▼", color = if (isFilterOpen) AccentLime else TextSecondary)
                }
            }

            // Filter Chips
            AnimatedVisibility(
                visible = isFilterOpen,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(FilterType.values()) { filter ->
                        val isSelected = filter == uiState.selectedFilter
                        val title = filter.name.replace("_", " ")
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color.Transparent else SurfaceVariant)
                                .then(
                                    if (isSelected) Modifier.border(1.5.dp, AccentLime, RoundedCornerShape(16.dp))
                                    else Modifier
                                )
                                .clickable { viewModel.setFilter(filter) }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = Typography.labelSmall.copy(
                                    color = if (isSelected) AccentLime else TextSecondary
                                )
                            )
                        }
                    }
                }
            }

            if (uiState.isLoading && uiState.transactions.isEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(5) {
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                }
            } else if (uiState.transactions.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "0",
                        style = Typography.displayLarge.copy(
                            color = TextDisabled,
                            fontSize = 80.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No transactions yet",
                        style = Typography.bodyMedium.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add your first one",
                        style = Typography.labelSmall.copy(color = AccentLime)
                    )
                }
            } else {
                // Transactions List Grouped by Month
                val groupedTransactions = uiState.transactions.groupBy {
                    val calendar = Calendar.getInstance().apply { timeInMillis = it.transaction.date }
                    Pair(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
                }

                val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    groupedTransactions.forEach { (yearMonth, transactions) ->
                        stickyHeader {
                            val calendar = Calendar.getInstance().apply {
                                set(Calendar.YEAR, yearMonth.first)
                                set(Calendar.MONTH, yearMonth.second)
                            }
                            Text(
                                text = monthFormat.format(calendar.time),
                                style = Typography.labelSmall.copy(color = TextSecondary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Background)
                                    .padding(vertical = 8.dp)
                            )
                        }

                        itemsIndexed(transactions, key = { _, it -> it.transaction.id }) { index, txWithCat ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { dismissValue ->
                                    if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.deleteTransaction(txWithCat.transaction.id)
                                        scope.launch {
                                            val result = snackbarHostState.showSnackbar(
                                                message = "Transaction deleted",
                                                actionLabel = "Undo"
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                viewModel.addTransaction(txWithCat.transaction)
                                            }
                                        }
                                        true
                                    } else {
                                        false
                                    }
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                enableDismissFromEndToStart = true,
                                backgroundContent = {
                                    val color = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) ErrorRed else Color.Transparent
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(color)
                                            .padding(end = 24.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        if (color != Color.Transparent) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Background
                                            )
                                        }
                                    }
                                }
                            ) {
                                // Make sure TransactionRow is used with Surface/Background so it looks proper on swipe
                                Box(modifier = Modifier.background(Background)) {
                                    TransactionRow(
                                        transaction = txWithCat.transaction,
                                        category = txWithCat.category,
                                        index = index
                                    )
                                }
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
        AddTransactionBottomSheet(
            isVisible = isSheetOpen,
            onDismiss = { isSheetOpen = false },
            categories = uiState.categories,
            onSave = { tx -> viewModel.addTransaction(tx) }
        )
    }
}
