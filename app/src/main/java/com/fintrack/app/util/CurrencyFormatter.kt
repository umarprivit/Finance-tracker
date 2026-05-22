package com.fintrack.app.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    fun formatAmount(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "PK")).apply {
            maximumFractionDigits = 0
        }
        return format.format(amount).replace("PKR", "PKR ")
    }
}
