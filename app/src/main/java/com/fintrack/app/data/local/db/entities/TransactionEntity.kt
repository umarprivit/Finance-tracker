package com.fintrack.app.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fintrack.app.domain.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long,
    val note: String,
    val date: Long,
    val isRecurring: Boolean
)
