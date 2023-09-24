package com.expenses.manager.presentation.value_formatters

import com.expenses.manager.extensions.round
import com.expenses.manager.extensions.roundAndFormat
import com.github.mikephil.charting.formatter.ValueFormatter

class PercentageCurrencyValueFormatter(private val total: Double, private val rounding: Int = 2) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value.roundAndFormat(rounding)} (${(value / total * 100).round(1)}%)"
    }
}