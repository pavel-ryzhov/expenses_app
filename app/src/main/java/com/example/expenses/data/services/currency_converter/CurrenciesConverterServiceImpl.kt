package com.example.expenses.data.services.currency_converter

import com.example.expenses.data.data_sources.local.dao.ExchangeRatesDao
import javax.inject.Inject

class CurrenciesConverterServiceImpl @Inject constructor(
    private val exchangeRatesDao: ExchangeRatesDao,
) : CurrenciesConverterService {
    override fun convertCurrency(amount: Double, from: String, to: String): Double {
        return fromMainCurrency(toMainCurrency(amount, from), to)
    }

    override fun toMainCurrency(amount: Double, from: String): Double {
        return amount / exchangeRatesDao.getExchangeRate(from).value
    }

    override fun fromMainCurrency(amount: Double, to: String): Double {
        return amount * exchangeRatesDao.getExchangeRate(to).value
    }
}