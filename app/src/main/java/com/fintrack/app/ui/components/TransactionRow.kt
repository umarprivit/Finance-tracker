package com.fintrack.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fintrack.app.domain.model.Category
import com.fintrack.app.domain.model.Transaction
import com.fintrack.app.domain.model.TransactionType
import com.fintrack.app.ui.theme.AccentLime
import com.fintrack.app.ui.theme.BorderColor
import com.fintrack.app.ui.theme.ErrorRed
import com.fintrack.app.ui.theme.TextPrimary
import com.fintrack.app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.fintrack.app.util.CurrencyFormatter

@Composable
fun TransactionRow(
    transaction: Transaction,
    category: Category?,
    index: Int,
    modifier: Modifier = Modifier
) {
    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(durationMillis = 400, delayMillis = index * 40)
        )
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val categoryColor = try {
                    Color(android.graphics.Color.parseColor(category?.colorHex ?: "#888888"))
                } catch (e: Exception) {
                    Color.Gray
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(categoryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category?.name?.take(1) ?: "?",
                        style = MaterialTheme.typography.titleMedium,
                        color = categoryColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category?.name ?: "Unknown",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                    
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    Text(
                        text = dateFormat.format(Date(transaction.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                val amountText = CurrencyFormatter.formatAmount(transaction.amount)
                val isIncome = transaction.type == TransactionType.INCOME

                Text(
                    text = if (isIncome) "+$amountText" else "-$amountText",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isIncome) AccentLime else ErrorRed,
                    fontWeight = FontWeight.Bold
                )
            }
            
            HorizontalDivider(color = BorderColor, thickness = 1.dp)
        }
    }
}
