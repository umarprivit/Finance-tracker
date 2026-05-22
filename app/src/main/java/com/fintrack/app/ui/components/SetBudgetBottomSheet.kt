package com.fintrack.app.ui.components

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintrack.app.domain.model.Budget
import com.fintrack.app.domain.model.Category
import com.fintrack.app.ui.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetBudgetBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    categories: List<Category>,
    onSave: (Budget) -> Unit
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
        SetBudgetContent(
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
fun SetBudgetContent(
    categories: List<Category>,
    onSave: (Budget) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    
    val currentCalendar = Calendar.getInstance()
    var selectedMonth by remember { mutableStateOf(currentCalendar.get(Calendar.MONTH)) }
    var selectedYear by remember { mutableStateOf(currentCalendar.get(Calendar.YEAR)) }

    var monthDropdownExpanded by remember { mutableStateOf(false) }
    var yearDropdownExpanded by remember { mutableStateOf(false) }

    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val years = (currentCalendar.get(Calendar.YEAR) .. currentCalendar.get(Calendar.YEAR) + 5).toList()

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
                    val catColor = try {
                        android.graphics.Color.parseColor(category.colorHex)
                    } catch (e: Exception) {
                        android.graphics.Color.GRAY
                    }
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

        Spacer(modifier = Modifier.height(24.dp))

        // Month/Year Selectors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Month Dropdown
            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariant)
                        .clickable { monthDropdownExpanded = true }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = months[selectedMonth], color = TextPrimary, style = Typography.bodyMedium)
                    Text(text = "▼", color = TextSecondary, fontSize = 10.sp)
                }
                
                DropdownMenu(
                    expanded = monthDropdownExpanded,
                    onDismissRequest = { monthDropdownExpanded = false },
                    modifier = Modifier.background(SurfaceVariant)
                ) {
                    months.forEachIndexed { index, month ->
                        DropdownMenuItem(
                            text = { Text(month, color = TextPrimary) },
                            onClick = {
                                selectedMonth = index
                                monthDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Year Dropdown
            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariant)
                        .clickable { yearDropdownExpanded = true }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = selectedYear.toString(), color = TextPrimary, style = Typography.bodyMedium)
                    Text(text = "▼", color = TextSecondary, fontSize = 10.sp)
                }

                DropdownMenu(
                    expanded = yearDropdownExpanded,
                    onDismissRequest = { yearDropdownExpanded = false },
                    modifier = Modifier.background(SurfaceVariant)
                ) {
                    years.forEach { year ->
                        DropdownMenuItem(
                            text = { Text(year.toString(), color = TextPrimary) },
                            onClick = {
                                selectedYear = year
                                yearDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())

        // Save button
        Button(
            onClick = {
                val amt = amountText.toDoubleOrNull() ?: 0.0
                if (amt > 0 && selectedCategory != null) {
                    onSave(
                        Budget(
                            categoryId = selectedCategory!!.id,
                            limitAmount = amt,
                            spentAmount = 0.0, // Calculated dynamically
                            month = selectedMonth,
                            year = selectedYear
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
                text = "Save Budget",
                style = Typography.bodyMedium.copy(
                    fontWeight = FontWeight.W500,
                    color = Background
                )
            )
        }
    }
}
