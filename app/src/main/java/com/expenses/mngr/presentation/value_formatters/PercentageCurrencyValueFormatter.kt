package com.expenses.mngr.presentation.value_formatters

import com.expenses.mngr.extensions.round
import com.expenses.mngr.extensions.roundAndFormat
import com.github.mikephil.charting.formatter.ValueFormatter

class PercentageCurrencyValueFormatter(private val total: Double, private val rounding: Int = 2) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value.roundAndFormat(rounding)} (${(value / total * 100).round(1)}%)"
    }
}