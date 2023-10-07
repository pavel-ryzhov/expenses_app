package com.expenses.mngr.data.wrappers

import android.annotation.SuppressLint
import com.expenses.mngr.data.data_sources.remote.RemoteExchangeRatesDataSource
import com.expenses.mngr.entities.exchange_rates.ExchangeRate
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RemoteExchangeRatesDataSourceWrapper @Inject constructor(
    private val remoteExchangeRatesDataSource: RemoteExchangeRatesDataSource
) {

    suspend fun getLatestExchangeRates(codes: MutableList<String>, mainCurrency: String) =
        stringJSONToExchangeRatesList(
            remoteExchangeRatesDataSource.getLatestExchangeRates(mainCurrency.lowercase()).execute()
                .body().toString(), codes
        )

    @SuppressLint("SimpleDateFormat")
    suspend fun getExchangeRates(codes: List<String>, mainCurrency: String, year: Int, month: Int, day: Int) =
        stringJSONToExchangeRatesList(
            remoteExchangeRatesDataSource.getExchangeRates(SimpleDateFormat("yyyy-MM-dd").format(
                GregorianCalendar(year, month, day).time
            ), mainCurrency.lowercase()).execute()
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