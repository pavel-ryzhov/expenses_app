package com.example.expenses.data.services.currency_converter

interface CurrenciesConverterService {
    fun convertCurrency(amount: Double, from: String, to: String): Double
    fun toMainCurrency(amount: Double, from: String): Double
    fun fromMainCurrency(amount: Double, to: String): Double
}