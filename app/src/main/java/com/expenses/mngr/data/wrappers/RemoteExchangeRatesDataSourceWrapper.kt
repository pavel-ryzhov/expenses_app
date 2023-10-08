package com.expenses.mngr.data.wrappers

import android.annotation.SuppressLint
import com.expenses.mngr.BuildConfig
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
            remoteExchangeRatesDataSource.getLatestExchangeRates(BuildConfig.API_ACCESS_KEY, mainCurrency.uppercase()).execute()
                .body().toString(), mainCurrency, codes
        )

    @SuppressLint("SimpleDateFormat")
    suspend fun getExchangeRates(codes: List<String>, mainCurrency: String, year: Int, month: Int, day: Int) =
        stringJSONToExchangeRatesList(
            remoteExchangeRatesDataSource.getExchangeRates(SimpleDateFormat("yyyy-MM-dd").format(
                GregorianCalendar(year, month, day).time
            ), BuildConfig.API_ACCESS_KEY, mainCurrency.uppercase()).execute()
                .body().toString(), mainCurrency, codes
        )

    companion object {
        fun stringJSONToExchangeRatesList(
            string: String,
            mainCurrency: String,
            codes: List<String>
        ): MutableList<ExchangeRate> {
            val json = JSONObject(string).getJSONObject("quotes")
            val result = mutableListOf<ExchangeRate>()
            codes.forEach {
                result.add(
                    ExchangeRate(
                        it, json.optDouble((mainCurrency + it).uppercase(), if (it == mainCurrency) 1.0 else 0.0).toFloat()
                    )
                )
            }
            println(result)
            return result
        }
    }
}