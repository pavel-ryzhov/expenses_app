package com.example.expenses.domain.exchange_rates

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.expenses.entities.exchange_rates.ExchangeRate
import javax.inject.Singleton

@Singleton
interface ExchangeRatesRepository {
    fun getNetworkErrorLiveData(): MutableLiveData<Unit?>
    suspend fun fetchLatestExchangeRates(mainCurrency: String, throwExceptionOnNetworkError: Boolean = false)
    fun getAllCachedLatestExchangeRatesLiveData(): LiveData<MutableList<ExchangeRate>>
    suspend fun getAllCachedLatestExchangeRates(): MutableList<ExchangeRate>
    suspend fun getCachedLatestExchangeRate(code: String): ExchangeRate
    fun getCachedLatestExchangeRateLiveData(code: String): LiveData<ExchangeRate>
    suspend fun getCachedLatestExchangeRates(codes: List<String>): MutableList<ExchangeRate>
    fun getCachedLatestExchangeRatesLiveData(codes: List<String>): LiveData<MutableList<ExchangeRate>>
    suspend fun writeLatestExchangeRatesToDatabase(exchangeRates: MutableList<ExchangeRate>)
    suspend fun deleteAllLatestExchangeRates()
    suspend fun getLatestExchangeRates(mainCurrency: String, throwExceptionOnNetworkError: Boolean = false): MutableList<ExchangeRate>?
    suspend fun getExchangeRates(mainCurrency: String, year: Int, month: Int, day: Int, throwExceptionOnNetworkError: Boolean = false): MutableList<ExchangeRate>?
}