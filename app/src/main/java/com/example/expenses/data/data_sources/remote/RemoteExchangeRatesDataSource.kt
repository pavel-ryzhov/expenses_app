package com.example.expenses.data.data_sources.remote

import android.annotation.SuppressLint
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.GregorianCalendar

abstract class RemoteExchangeRatesDataSource {
    @GET("latest")
    abstract fun getExchangeRates(
        @Query("base") mainCurrency: String
    ): Call<String>

    @GET("{date}")
    abstract fun getExchangeRates(
        @Query("base") mainCurrency: String,
        @Path("date") date: String
    ): Call<String>

    @SuppressLint("SimpleDateFormat")
    fun getExchangeRates(
        @Query("base") mainCurrency: String,
        year: Int,
        month: Int,
        day: Int,
    ) : Call<String> {
        return getExchangeRates(mainCurrency, SimpleDateFormat("yyyy-MM-dd").format(
            GregorianCalendar(year, month, day)
        ))
    }
}