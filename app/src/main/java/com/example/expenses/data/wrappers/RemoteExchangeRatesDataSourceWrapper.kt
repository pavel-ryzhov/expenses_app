package com.example.expenses.data.wrappers

import com.example.expenses.data.data_sources.remote.RemoteExchangeRatesDataSource
import com.example.expenses.entities.exchange_rates.ExchangeRate
import org.json.JSONObject
import javax.inject.Inject

class RemoteExchangeRatesDataSourceWrapper @Inject constructor(
    private val remoteExchangeRatesDataSource: RemoteExchangeRatesDataSource
) {

    fun getExchangeRates(codes: MutableList<String>, mainCurrency: String) =
        stringJSONToExchangeRatesList(
            remoteExchangeRatesDataSource.getExchangeRates(mainCurrency.lowercase()).execute()
                .body().toString(), codes
        )

    companion object {
        fun stringJSONToExchangeRatesList(
            string: String,
            codes: List<String>
        ): MutableList<ExchangeRate> {
            val json = JSONObject(string).getJSONObject("rates")
            val result = mutableListOf<ExchangeRate>()
            codes.forEach {
                result.add(
                    ExchangeRate(
                        it, json.optDouble(it, 0.0).toFloat()
                    )
                )
            }
            println(result)
            return result
        }
    }
}