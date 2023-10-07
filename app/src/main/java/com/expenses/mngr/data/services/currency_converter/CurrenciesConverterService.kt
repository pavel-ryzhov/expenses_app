package com.expenses.mngr.data.services.currency_converter

import com.expenses.mngr.entities.exchange_rates.ExchangeRate
import com.expenses.mngr.entities.expense.Amount
import javax.inject.Singleton

@Singleton
interface CurrenciesConverterService {
    suspend fun convertCurrencyByLatest(amount: Double, from: String, to: String): Double
    suspend fun toMainCurrencyByLatest(amount: Double, from: String): Double
    suspend fun fromMainCurrencyByLatest(amount: Double, to: String): Double
    suspend fun amountFromMainCurrencyByLatest(amount: Double): Amount
    suspend fun amountFromCurrencyByLatest(amount: Double, from: String): Amount
    suspend fun convertCurrency(amount: Double, from: String, to: String, exchangeRates: List<ExchangeRate>): Double
    suspend fun toMainCurrency(amount: Double, from: String, exchangeRates: List<ExchangeRate>): Double
    suspend fun fromMainCurrency(amount: Double, to: String, exchangeRates: List<ExchangeRate>): Double
    suspend fun amountFromMainCurrency(amount: Double, year: Int, month: Int, day: Int): Amount
    suspend fun amountFromCurrency(amount: Double, from: String, year: Int, month: Int, day: Int): Amount
    suspend fun amountFromMainCurrency(amount: Double, exchangeRates: List<ExchangeRate>): Amount
    suspend fun amountFromCurrency(amount: Double, from: String, exchangeRates: List<ExchangeRate>): Amount
}