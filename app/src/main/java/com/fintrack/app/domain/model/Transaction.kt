package com.fintrack.app.domain.model

enum class TransactionType {
    INCOME,
    EXPENSE
}

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long,
    val note: String,
    val date: Long, // epoch millis
    val isRecurring: Boolean = false
)
