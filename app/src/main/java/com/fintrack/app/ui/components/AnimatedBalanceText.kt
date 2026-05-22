package com.fintrack.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.fintrack.app.ui.theme.TextPrimary
import com.fintrack.app.util.CurrencyFormatter

@Composable
fun AnimatedBalanceText(
    targetValue: Double,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.displayMedium
) {
    val animatedValue by animateFloatAsState(
        targetValue = targetValue.toFloat(),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "balanceAnimation"
    )

    
    Text(
        text = CurrencyFormatter.formatAmount(animatedValue.toDouble()),
        style = textStyle,
        color = TextPrimary,
        modifier = modifier
    )
}
