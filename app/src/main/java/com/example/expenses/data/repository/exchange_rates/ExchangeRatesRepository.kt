package com.example.expenses.data.repository.exchange_rates

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.expenses.entities.exchange_rates.ExchangeRate
import javax.inject.Singleton

@Singleton
interface ExchangeRatesRepository {
    suspend fun fetchExchangeRates(mainCurrency: String)
    fun getAllExchangeRatesLiveData(): LiveData<MutableList<ExchangeRate>>
    suspend fun getAllExchangeRates(): MutableList<ExchangeRate>
    suspend fun getExchangeRate(code: String): ExchangeRate
    fun getExchangeRateLiveData(code: String): LiveData<ExchangeRate>
    suspend fun getExchangeRates(codes: List<String>): MutableList<ExchangeRate>
    fun getExchangeRatesLiveData(codes: List<String>): LiveData<MutableList<ExchangeRate>>
    suspend fun getExchangeRatesWithoutWritingToDatabase(mainCurrency: String): MutableList<ExchangeRate>?
    suspend fun writeExchangeRatesToDatabase(exchangeRates: MutableList<ExchangeRate>)
    suspend fun deleteAllExchangeRates()
    fun getNetworkErrorLiveData(): MutableLiveData<Unit?>
}