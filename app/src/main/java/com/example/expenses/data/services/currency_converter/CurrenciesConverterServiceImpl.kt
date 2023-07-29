package com.example.expenses.data.services.currency_converter

import android.util.Log
import com.example.expenses.data.data_sources.local.dao.ExchangeRatesDao
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.data.wrappers.RemoteExchangeRatesDataSourceWrapper
import com.example.expenses.entities.exchange_rates.ExchangeRate
import com.example.expenses.entities.expense.Amount
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrenciesConverterServiceImpl @Inject constructor(
    private val exchangeRatesDao: ExchangeRatesDao,
    private val exchangeRatesDataSourceWrapper: RemoteExchangeRatesDataSourceWrapper,
    private val appPreferences: AppPreferences
) : CurrenciesConverterService {
    override suspend fun convertCurrencyByLatest(amount: Double, from: String, to: String): Double {
        return fromMainCurrencyByLatest(toMainCurrencyByLatest(amount, from), to)
    }

    override suspend fun toMainCurrencyByLatest(amount: Double, from: String): Double {
        return amount / exchangeRatesDao.getExchangeRate(from).value
    }

    override suspend fun fromMainCurrencyByLatest(amount: Double, to: String): Double {
        return amount * exchangeRatesDao.getExchangeRate(to).value
    }

    override suspend fun convertCurrency(
        amount: Double,
        from: String,
        to: String,
        exchangeRates: List<ExchangeRate>
    ): Double {
        return fromMainCurrency(toMainCurrency(amount, from, exchangeRates), to, exchangeRates)
    }

    override suspend fun toMainCurrency(
        amount: Double,
        from: String,
        exchangeRates: List<ExchangeRate>
    ): Double {
        return amount / ExchangeRate.getExchangeRateFromList(exchangeRates, from)!!.value
    }

    override suspend fun fromMainCurrency(
        amount: Double,
        to: String,
        exchangeRates: List<ExchangeRate>
    ): Double {
        return amount * ExchangeRate.getExchangeRateFromList(exchangeRates, to)!!.value
    }

    override suspend fun amountFromMainCurrencyByLatest(amount: Double): Amount {
        val map = mutableMapOf<String, Double>()
        map[appPreferences.getMainCurrency()] = amount
        appPreferences.getSecondaryCurrencies().forEach {
            map[it] = fromMainCurrencyByLatest(amount, it)
        }
        return Amount(map)
    }

    override suspend fun amountFromCurrencyByLatest(amount: Double, from: String): Amount {
        return amountFromMainCurrencyByLatest(toMainCurrencyByLatest(amount, from))
    }

    override suspend fun amountFromMainCurrency(
        amount: Double,
        year: Int,
        month: Int,
        day: Int
    ): Amount {
        val map = mutableMapOf<String, Double>()
        map[appPreferences.getMainCurrency()] = amount
        exchangeRatesDataSourceWrapper.getExchangeRates(
            appPreferences.getSecondaryCurrencies(),
            appPreferences.getMainCurrency(),
            year,
            month,
            day
        ).forEach {
            map[it.code] = amount * it.value
        }
        return Amount(map)
    }

    override suspend fun amountFromCurrency(
        amount: Double,
        from: String,
        year: Int,
        month: Int,
        day: Int
    ): Amount {
        val exchangeRates = exchangeRatesDataSourceWrapper.getExchangeRates(
            appPreferences.getCurrencies(),
            appPreferences.getMainCurrency(),
            year,
            month,
            day
        )
        val map = mutableMapOf<String, Double>()
        val mainExchangeRate =
            ExchangeRate.getExchangeRateFromList(exchangeRates, appPreferences.getMainCurrency())!!
        val amountInMainCurrency = toMainCurrency(amount, from, exchangeRates)
        exchangeRates.remove(mainExchangeRate)
        map[appPreferences.getMainCurrency()] = amountInMainCurrency
        exchangeRates.forEach {
            map[it.code] = amountInMainCurrency * it.value
        }
        return Amount(map)
    }

    override suspend fun amountFromMainCurrency(
        amount: Double,
        exchangeRates: List<ExchangeRate>
    ): Amount {
        val map = mutableMapOf<String, Double>()
        val secondaryCurrencies = appPreferences.getSecondaryCurrencies()
        map[appPreferences.getMainCurrency()] = amount
        exchangeRates.filter { it.code in secondaryCurrencies }.forEach {
            map[it.code] = amount * it.value
        }
        return Amount(map)
    }

    override suspend fun amountFromCurrency(
        amount: Double,
        from: String,
        exchangeRates: List<ExchangeRate>
    ): Amount {
        val amountInMainCurrency = toMainCurrency(amount, from, exchangeRates)
        val map = mutableMapOf<String, Double>()
        val secondaryCurrencies = appPreferences.getSecondaryCurrencies()
        map[appPreferences.getMainCurrency()] = amountInMainCurrency
        exchangeRates.filter { it.code in secondaryCurrencies }.forEach {
            map[it.code] = amountInMainCurrency * it.value
        }
        return Amount(map)
    }
}