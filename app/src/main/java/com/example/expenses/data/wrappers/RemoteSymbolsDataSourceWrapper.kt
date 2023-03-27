package com.example.expenses.data.wrappers

import com.example.expenses.data.data_sources.remote.RemoteSymbolsDataSource
import com.example.expenses.entities.symbols.Symbol
import org.json.JSONObject
import javax.inject.Inject

class RemoteSymbolsDataSourceWrapper @Inject constructor(private val remoteSymbolsDataSource: RemoteSymbolsDataSource) {
    fun getSymbols() = stringJSONToSymbolsList(
        remoteSymbolsDataSource.getSymbolsAsString().execute().body().toString()
    )

    companion object {
        fun stringJSONToSymbolsList(string: String): MutableList<Symbol> {
            val result = mutableListOf<Symbol>()
            val jsonObject = JSONObject(string).getJSONObject("symbols")
            jsonObject.keys().forEach {
                result.add(Symbol(it, jsonObject.getJSONObject(it).getString("description")))
            }
            return result
        }
    }
}