package com.example.expenses.data.repository.exchange_rates

import androidx.lifecycle.LiveData
import com.example.expenses.entities.exchange_rates.ExchangeRate
import com.example.expenses.entities.symbols.Symbol
import javax.inject.Singleton

@Singleton
interface ExchangeRatesRepository {
    suspend fun fetchExchangeRates(mainCurrency: Symbol)
    fun getAllExchangeRatesLiveData(): LiveData<MutableList<ExchangeRate>>
    suspend fun getAllExchangeRates(): MutableList<ExchangeRate>
    suspend fun getExchangeRate(symbol: Symbol): ExchangeRate
    fun getExchangeRateLiveData(symbol: Symbol): LiveData<ExchangeRate>
    suspend fun getExchangeRates(symbols: List<Symbol>): MutableList<ExchangeRate>
    fun getExchangeRatesLiveData(symbols: List<Symbol>): LiveData<MutableList<ExchangeRate>>
}