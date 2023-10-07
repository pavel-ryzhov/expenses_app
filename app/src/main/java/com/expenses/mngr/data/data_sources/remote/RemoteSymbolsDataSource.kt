package com.expenses.mngr.data.data_sources.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteSymbolsDataSource {
    @GET("list")
    fun getSymbolsAsString(
        @Query("access_key") accessKey: String
    ): Call<String>
}