package com.example.expenses.presentation.value_formatters

import com.example.expenses.extensions.round
import com.example.expenses.extensions.roundAndFormat
import com.github.mikephil.charting.formatter.ValueFormatter

class PercentageCurrencyValueFormatter(private val total: Double, private val rounding: Int = 2) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value.roundAndFormat(rounding)} (${(value / total * 100).round(1)}%)"
    }
}