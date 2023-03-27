package com.example.expenses.data.data_sources.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteExchangeRatesDataSource {
    @GET("latest")
    fun getExchangeRates(
        @Query("base") mainCurrency: String
    ): Call<String>
}