package com.example.expenses.presentation.value_formatters

import com.github.mikephil.charting.formatter.ValueFormatter

open class CurrencyValueFormatter(private val currency: String) : ValueFormatter() {
    override fun getFormattedValue(value: Float) = "$value $currency"
}