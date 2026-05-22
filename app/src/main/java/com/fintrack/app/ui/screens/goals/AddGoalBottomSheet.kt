package com.fintrack.app.ui.screens.goals

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fintrack.app.domain.model.Goal
import com.fintrack.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSave: (Goal) -> Unit
) {
    if (!isVisible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Background,
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderColor)
            )
        }
    ) {
        AddGoalContent(
            onSave = {
                onSave(it)
                onDismiss()
            }
        )
    }
}

@Composable
fun AddGoalContent(onSave: (Goal) -> Unit) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var targetAmountText by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000) } // Default +30 days
    var selectedIcon by remember { mutableStateOf("Star") }

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = deadline
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val newCalendar = Calendar.getInstance()
            newCalendar.set(year, month, dayOfMonth)
            deadline = newCalendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val iconOptions = listOf("Star", "DirectionsCar", "Home", "Flight", "Computer")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Goal title
        BasicTextField(
            value = title,
            onValueChange = { title = it },
            textStyle = Typography.bodyMedium.copy(color = TextPrimary),
            singleLine = true,
            cursorBrush = SolidColor(AccentLime),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariant)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (title.isEmpty()) {
                        Text(text = "Goal Title...", color = TextDisabled, style = Typography.bodyMedium)
                    } else {
                        innerTextField()
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Target amount
        BasicTextField(
            value = targetAmountText,
            onValueChange = { targetAmountText = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = Typography.bodyMedium.copy(color = TextPrimary),
            singleLine = true,
            cursorBrush = SolidColor(AccentLime),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariant)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (targetAmountText.isEmpty()) {
                        Text(text = "Target Amount", color = TextDisabled, style = Typography.bodyMedium)
                    } else {
                        innerTextField()
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date picker
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceVariant)
                .clickable { datePickerDialog.show() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Date",
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = dateFormat.format(Date(deadline)),
                color = TextPrimary,
                style = Typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Icon selector
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(iconOptions) { iconName ->
                val isSelected = selectedIcon == iconName
                val iconVector = when (iconName) {
                    "DirectionsCar" -> Icons.Default.DirectionsCar
                    "Home" -> Icons.Default.Home
                    "Flight" -> Icons.Default.Flight
                    "Computer" -> Icons.Default.Computer
                    else -> Icons.Default.Star
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SurfaceVariant)
                        .then(
                            if (isSelected) Modifier.border(1.5.dp, AccentLime, CircleShape)
                            else Modifier
                        )
                        .clickable { selectedIcon = iconName },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = iconName,
                        tint = if (isSelected) AccentLime else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())

        // Save button
        Button(
            onClick = {
                val amt = targetAmountText.toDoubleOrNull() ?: 0.0
                if (title.isNotBlank() && amt > 0) {
                    onSave(
                        Goal(
                            title = title,
                            targetAmount = amt,
                            savedAmount = 0.0,
                            deadline = deadline,
                            iconName = selectedIcon
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentLime,
                contentColor = Background
            ),
            enabled = title.isNotBlank() && targetAmountText.toDoubleOrNull() != null
        ) {
            Text(
                text = "Save",
                style = Typography.bodyMedium.copy(
                    fontWeight = FontWeight.W500,
                    color = Background
                )
            )
        }
    }
}
