package com.example.expenses.presentation.value_formatters

import android.annotation.SuppressLint
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DateFormat
import java.text.SimpleDateFormat

class TimeValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return formatTime(value.toInt() * 5 / 60, value.toInt() % 12 * 5)
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatTime(hour: Int, minute: Int): String {
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(SimpleDateFormat("H:m").parse("$hour:$minute")!!)
    }

}