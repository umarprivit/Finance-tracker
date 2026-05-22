package com.fintrack.app.ui.components

import android.app.DatePickerDialog
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintrack.app.domain.model.Category
import com.fintrack.app.domain.model.Transaction
import com.fintrack.app.domain.model.TransactionType
import com.fintrack.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    categories: List<Category>,
    onSave: (Transaction) -> Unit
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
        AddTransactionContent(
            categories = categories,
            onSave = {
                onSave(it)
                onDismiss()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionContent(
    categories: List<Category>,
    onSave: (Transaction) -> Unit
) {
    val context = LocalContext.current
    var amountText by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var note by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(System.currentTimeMillis()) }

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = date
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val newCalendar = Calendar.getInstance()
            newCalendar.set(year, month, dayOfMonth)
            date = newCalendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Amount input
        BasicTextField(
            value = amountText,
            onValueChange = { amountText = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = Typography.displayLarge.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.W600,
                color = AccentLime,
                textAlign = TextAlign.Center
            ),
            cursorBrush = SolidColor(AccentLime),
            decorationBox = { innerTextField ->
                if (amountText.isEmpty()) {
                    Text(
                        text = "0.00",
                        style = Typography.displayLarge.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.W600,
                            color = TextDisabled,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    innerTextField()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Toggle row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceVariant)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (transactionType == TransactionType.EXPENSE) AccentLime else Color.Transparent)
                    .clickable { transactionType = TransactionType.EXPENSE },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Expense",
                    style = Typography.bodyMedium.copy(
                        color = if (transactionType == TransactionType.EXPENSE) Background else TextSecondary,
                        fontWeight = if (transactionType == TransactionType.EXPENSE) FontWeight.W600 else FontWeight.W400
                    )
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (transactionType == TransactionType.INCOME) AccentLime else Color.Transparent)
                    .clickable { transactionType = TransactionType.INCOME },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Income",
                    style = Typography.bodyMedium.copy(
                        color = if (transactionType == TransactionType.INCOME) Background else TextSecondary,
                        fontWeight = if (transactionType == TransactionType.INCOME) FontWeight.W600 else FontWeight.W400
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Category picker
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory?.id == category.id
                Row(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceVariant)
                        .then(
                            if (isSelected) Modifier.border(1.5.dp, AccentLime, RoundedCornerShape(18.dp))
                            else Modifier
                        )
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val catColor = android.graphics.Color.parseColor(category.colorHex)
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(catColor))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = category.name,
                        style = Typography.bodySmall.copy(color = TextPrimary)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Note field
        BasicTextField(
            value = note,
            onValueChange = { note = it },
            textStyle = Typography.bodyMedium.copy(color = TextPrimary),
            singleLine = true,
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
                    if (note.isEmpty()) {
                        Text(text = "Add note...", color = TextDisabled, style = Typography.bodyMedium)
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
                text = dateFormat.format(Date(date)),
                color = TextPrimary,
                style = Typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())

        // Save button
        Button(
            onClick = {
                val amt = amountText.toDoubleOrNull() ?: 0.0
                if (amt > 0 && selectedCategory != null) {
                    onSave(
                        Transaction(
                            amount = amt,
                            type = transactionType,
                            categoryId = selectedCategory!!.id,
                            note = note,
                            date = date
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
            enabled = amountText.toDoubleOrNull() != null && selectedCategory != null
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
