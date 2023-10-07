package com.expenses.mngr.data.wrappers

import com.expenses.mngr.BuildConfig
import com.expenses.mngr.data.data_sources.remote.RemoteSymbolsDataSource
import com.expenses.mngr.entities.symbols.Symbol
import org.json.JSONObject
import javax.inject.Inject

class RemoteSymbolsDataSourceWrapper @Inject constructor(private val remoteSymbolsDataSource: RemoteSymbolsDataSource) {
    suspend fun getSymbols() = stringJSONToSymbolsList(
        remoteSymbolsDataSource.getSymbolsAsString(BuildConfig.API_ACCESS_KEY).execute().body().toString()
    )

    companion object {
        fun stringJSONToSymbolsList(string: String): MutableList<Symbol> {
            val result = mutableListOf<Symbol>()
            val jsonObject = JSONObject(string).getJSONObject("currencies")
            jsonObject.keys().forEach {
                result.add(Symbol(it, jsonObject.getString(it)))
            }
            return result
        }
    }
}