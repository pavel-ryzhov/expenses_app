package com.expenses.mngr.data.data_sources.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteExchangeRatesDataSource {
    @GET("live")
    fun getLatestExchangeRates(
        @Query("access_key") accessKey: String,
        @Query("source") mainCurrency: String
    ): Call<String>

    @GET("historical")
    fun getExchangeRates(
        @Query("date") date: String,
        @Query("access_key") accessKey: String,
        @Query("source") mainCurrency: String
    ): Call<String>
}