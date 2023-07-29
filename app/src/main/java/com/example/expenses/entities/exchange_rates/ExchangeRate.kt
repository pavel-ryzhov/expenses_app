package com.example.expenses.entities.exchange_rates

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ExchangeRate(
    @PrimaryKey
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "value") val value: Float
){
    companion object {
        fun getExchangeRateFromList(exchangeRates: List<ExchangeRate>, currency: String): ExchangeRate? {
            exchangeRates.forEach {
                if (it.code == currency) return it
            }
            return null
        }
    }
}