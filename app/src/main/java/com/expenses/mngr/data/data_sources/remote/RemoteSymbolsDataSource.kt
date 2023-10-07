package com.expenses.mngr.data.data_sources.remote

import retrofit2.Call
import retrofit2.http.GET

interface RemoteSymbolsDataSource {
    @GET("symbols")
    fun getSymbolsAsString(): Call<String>
}