package com.example.expenses.data.data_sources.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RemoteExchangeRatesDataSource {
    @GET("latest")
    fun getLatestExchangeRates(
        @Query("base") mainCurrency: String
    ): Call<String>

    @GET("{date}")
    fun getExchangeRates(
        @Path("date") date: String,
        @Query("base") mainCurrency: String
    ): Call<String>
}