package com.example.expenses.data.data_sources.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expenses.entities.exchange_rates.ExchangeRate

@Dao
interface ExchangeRatesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllExchangeRates(list: MutableList<ExchangeRate>)

    @Query("SELECT * FROM exchangeRate WHERE code = :code")
    fun getExchangeRate(code: String): ExchangeRate

    @Query("SELECT * FROM exchangeRate")
    fun getAllExchangeRatesLiveData(): LiveData<MutableList<ExchangeRate>>

    @Query("SELECT * FROM exchangeRate")
    fun getAllExchangeRates(): MutableList<ExchangeRate>

    @Query("SELECT * FROM exchangeRate WHERE code = :code")
    fun getExchangeRateLiveData(code: String): LiveData<ExchangeRate>

    @Query("SELECT * FROM exchangeRate WHERE code IN (:codes)")
    fun getExchangeRates(codes: List<String>): MutableList<ExchangeRate>

    @Query("SELECT * FROM exchangeRate WHERE code IN (:codes)")
    fun getExchangeRatesLiveData(codes: List<String>): LiveData<MutableList<ExchangeRate>>
}