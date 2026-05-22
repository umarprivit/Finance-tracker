package com.fintrack.app.domain.model

data class Budget(
    val id: Long = 0,
    val categoryId: Long,
    val limitAmount: Double,
    val spentAmount: Double,
    val month: Int,
    val year: Int
)
