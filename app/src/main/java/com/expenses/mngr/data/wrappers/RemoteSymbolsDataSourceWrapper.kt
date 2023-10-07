package com.expenses.mngr.data.wrappers

import android.util.Log
import com.expenses.mngr.data.data_sources.remote.RemoteSymbolsDataSource
import com.expenses.mngr.entities.symbols.Symbol
import org.json.JSONObject
import javax.inject.Inject

class RemoteSymbolsDataSourceWrapper @Inject constructor(private val remoteSymbolsDataSource: RemoteSymbolsDataSource) {
    suspend fun getSymbols() = stringJSONToSymbolsList(
        remoteSymbolsDataSource.getSymbolsAsString().execute().body().toString()
    )

    companion object {
        fun stringJSONToSymbolsList(string: String): MutableList<Symbol> {
            val result = mutableListOf<Symbol>()
            Log.d("ssss", string)
            val jsonObject = JSONObject(string).getJSONObject("symbols")
            jsonObject.keys().forEach {
                result.add(Symbol(it, jsonObject.getJSONObject(it).getString("description")))
            }
            return result
        }
    }
}