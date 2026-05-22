package com.fintrack.app.domain.model

data class Goal(
    val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double,
    val deadline: Long,
    val iconName: String
)
