package com.fintrack.app.ui.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fintrack.app.ui.theme.*

@Composable
fun AddToGoalDialog(
    goalId: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long, Double) -> Unit
) {
    var amountText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Surface)
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Add savings",
                    style = Typography.titleLarge.copy(color = TextPrimary),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Amount input
                BasicTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
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
                            if (amountText.isEmpty()) {
                                Text(text = "Amount", color = TextDisabled, style = Typography.bodyMedium)
                            } else {
                                innerTextField()
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Cancel button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BorderColor,
                            contentColor = TextPrimary
                        )
                    ) {
                        Text("Cancel", style = Typography.bodyMedium)
                    }

                    // Confirm button
                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                onConfirm(goalId, amt)
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentLime,
                            contentColor = Background
                        ),
                        enabled = amountText.toDoubleOrNull() != null
                    ) {
                        Text(
                            text = "Add",
                            style = Typography.bodyMedium.copy(
                                fontWeight = FontWeight.W600,
                                color = Background
                            )
                        )
                    }
                }
            }
        }
    }
}
