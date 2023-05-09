package com.example.expenses.presentation.value_formatters

import com.example.expenses.extensions.round
import com.example.expenses.extensions.roundAndFormat

class PercentageCurrencyValueFormatter(currency: String, private val total: Double) : CurrencyValueFormatter(currency) {
    override fun getFormattedValue(value: Float): String {
        return "${value.roundAndFormat()} (${(value / total * 100).round(1)}%)"
    }
}