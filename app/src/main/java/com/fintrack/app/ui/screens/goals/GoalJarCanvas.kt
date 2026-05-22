package com.fintrack.app.ui.screens.goals

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import com.fintrack.app.ui.theme.AccentCyan
import com.fintrack.app.ui.theme.AccentLime
import com.fintrack.app.ui.theme.BorderColor

@Composable
fun GoalJarCanvas(
    savedPercent: Float,
    modifier: Modifier = Modifier
) {
    // Fill color logic
    val fillColor = when {
        savedPercent >= 1f -> Color.White
        savedPercent >= 0.8f -> AccentCyan
        else -> AccentLime
    }

    // Animate fill height
    val animatedFillPercent by animateFloatAsState(
        targetValue = savedPercent.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 900),
        label = "jarFillAnimation"
    )

    Canvas(modifier = modifier.size(80.dp, 100.dp)) {
        val w = size.width
        val h = size.height

        // Lid dimensions
        val lidWidth = w * 0.6f
        val lidHeight = h * 0.08f
        val lidLeft = (w - lidWidth) / 2
        val lidTop = 0f

        // Neck dimensions
        val neckWidth = w * 0.5f
        val neckHeight = h * 0.12f
        val neckLeft = (w - neckWidth) / 2
        val neckTop = lidTop + lidHeight

        // Body dimensions
        val bodyWidth = w * 0.9f
        val bodyHeight = h - neckTop
        val bodyLeft = (w - bodyWidth) / 2
        val bodyTop = neckTop
        val cornerRadius = CornerRadius(w * 0.15f, w * 0.15f)

        // Draw lid
        drawRoundRect(
            color = AccentLime,
            topLeft = Offset(lidLeft, lidTop),
            size = Size(lidWidth, lidHeight),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Path for the jar (neck + body)
        val jarPath = Path().apply {
            // Add neck
            addRect(Rect(neckLeft, neckTop, neckLeft + neckWidth, neckTop + neckHeight))
            
            // Add body with rounded corners at bottom
            addRoundRect(
                RoundRect(
                    left = bodyLeft,
                    top = bodyTop,
                    right = bodyLeft + bodyWidth,
                    bottom = bodyTop + bodyHeight,
                    bottomLeftCornerRadius = cornerRadius,
                    bottomRightCornerRadius = cornerRadius,
                    topLeftCornerRadius = CornerRadius(w * 0.05f, w * 0.05f),
                    topRightCornerRadius = CornerRadius(w * 0.05f, w * 0.05f)
                )
            )
        }

        // Draw jar outline
        drawPath(
            path = jarPath,
            color = BorderColor,
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Draw filled portion inside the jar
        clipPath(jarPath) {
            val totalJarHeight = neckHeight + bodyHeight
            val fillHeight = totalJarHeight * animatedFillPercent
            val fillTop = h - fillHeight
            
            drawRect(
                color = fillColor,
                topLeft = Offset(0f, fillTop),
                size = Size(w, fillHeight)
            )
        }
    }
}
