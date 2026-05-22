package com.fintrack.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fintrack.app.ui.theme.AccentLime
import com.fintrack.app.ui.theme.BorderColor
import com.fintrack.app.ui.theme.SurfaceVariant
import com.fintrack.app.ui.theme.TextPrimary
import com.fintrack.app.util.CurrencyFormatter

@Composable
fun CategoryChip(
    name: String,
    colorHex: String,
    amount: Double,
    modifier: Modifier = Modifier
) {
    val categoryColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        Color.Gray
    }

    Row(
        modifier = modifier
            .background(color = SurfaceVariant, shape = MaterialTheme.shapes.small)
            .border(width = 1.dp, color = BorderColor, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(categoryColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = CurrencyFormatter.formatAmount(amount),
            style = MaterialTheme.typography.bodyMedium,
            color = AccentLime
        )
    }
}
