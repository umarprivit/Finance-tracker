package com.fintrack.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fintrack.app.ui.theme.AccentLime
import com.fintrack.app.ui.theme.BorderColor
import com.fintrack.app.ui.theme.ErrorRed
import com.fintrack.app.ui.theme.WarningAmber

@Composable
fun BudgetProgressBar(
    percent: Float,
    modifier: Modifier = Modifier
) {
    // Determine color based on threshold
    val barColor = when {
        percent < 0.7f -> AccentLime
        percent <= 0.9f -> WarningAmber
        else -> ErrorRed
    }

    // Animate width
    val targetPercent = percent.coerceIn(0f, 1f)
    val animatedPercent by animateFloatAsState(
        targetValue = targetPercent,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "BudgetProgress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(BorderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedPercent)
                .background(barColor)
        )
    }
}
