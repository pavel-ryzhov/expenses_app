package com.example.expenses.data.repository.exchange_rates

import com.example.expenses.data.data_sources.local.dao.ExchangeRatesDao
import com.example.expenses.data.data_sources.local.dao.SymbolsDao
import com.example.expenses.data.wrappers.RemoteExchangeRatesDataSourceWrapper
import com.example.expenses.entities.symbols.Symbol
import javax.inject.Inject

class ExchangeRatesRepositoryImpl @Inject constructor(
    private val exchangeRatesDao: ExchangeRatesDao,
    private val exchangeRatesDataSource: RemoteExchangeRatesDataSourceWrapper,
    private val symbolsDao: SymbolsDao
) : ExchangeRatesRepository {
    override fun fetchExchangeRates(mainCurrency: Symbol) {
        exchangeRatesDao.insertAllExchangeRates(
            exchangeRatesDataSource.getExchangeRates(
                symbolsDao.getAllSymbols().map { it.code }.toMutableList(), mainCurrency.code
            )
        )
    }

    override fun getAllExchangeRatesLiveData() = exchangeRatesDao.getAllExchangeRatesLiveData()

    override fun getAllExchangeRates() = exchangeRatesDao.getAllExchangeRates()

    override fun getExchangeRateLiveData(symbol: Symbol) = exchangeRatesDao.getExchangeRateLiveData(symbol.code)

    override fun getExchangeRate(symbol: Symbol) = exchangeRatesDao.getExchangeRate(symbol.code)

    override fun getExchangeRatesLiveData(symbols: List<Symbol>) = exchangeRatesDao.getExchangeRatesLiveData(symbols.map { it.code })

    override fun getExchangeRates(symbols: List<Symbol>) = exchangeRatesDao.getExchangeRates(symbols.map { it.code })
}