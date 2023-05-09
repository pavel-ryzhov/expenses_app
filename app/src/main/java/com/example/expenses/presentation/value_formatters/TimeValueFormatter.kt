package com.example.expenses.presentation.value_formatters

import com.github.mikephil.charting.formatter.ValueFormatter
import com.example.expenses.utils.formatTime

class TimeValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return formatTime(value.toInt() * 5 / 60, value.toInt() % 12 * 5)
    }
}