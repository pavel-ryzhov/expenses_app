package com.example.expenses.data.services.currency_converter

import javax.inject.Singleton

@Singleton
interface CurrenciesConverterService {
    suspend fun convertCurrency(amount: Double, from: String, to: String): Double
    suspend fun toMainCurrency(amount: Double, from: String): Double
    suspend fun fromMainCurrency(amount: Double, to: String): Double
}