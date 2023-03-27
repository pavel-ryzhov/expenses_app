package com.example.expenses.presentation.value_formatters

import com.example.expenses.extensions.round

class PercentageCurrencyValueFormatter(currency: String, private val total: Double) : CurrencyValueFormatter(currency) {
    override fun getFormattedValue(value: Float): String {
        return "${super.getFormattedValue(value)} (${(value / total * 100).round(1)}%)"
    }
}