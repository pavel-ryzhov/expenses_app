package com.example.expenses.data.repository.exchange_rates

import androidx.lifecycle.MutableLiveData
import com.example.expenses.data.data_sources.local.dao.ExchangeRatesDao
import com.example.expenses.data.data_sources.local.dao.SymbolsDao
import com.example.expenses.data.wrappers.RemoteExchangeRatesDataSourceWrapper
import com.example.expenses.entities.exchange_rates.ExchangeRate
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRatesRepositoryImpl @Inject constructor(
    private val exchangeRatesDao: ExchangeRatesDao,
    private val exchangeRatesDataSource: RemoteExchangeRatesDataSourceWrapper,
    private val symbolsDao: SymbolsDao
) : ExchangeRatesRepository {

    private val networkErrorLiveData = MutableLiveData<Unit?>()

    override suspend fun fetchExchangeRates(mainCurrency: String) {
        try {
            exchangeRatesDao.insertAllExchangeRates(
                exchangeRatesDataSource.getExchangeRates(
                    symbolsDao.getAllSymbols().map { it.code }.toMutableList(), mainCurrency
                )
            )
        } catch (e: UnknownHostException) {
            networkErrorLiveData.postValue(Unit)
        }
    }

    override fun getAllExchangeRatesLiveData() = exchangeRatesDao.getAllExchangeRatesLiveData()

    override suspend fun getAllExchangeRates() = exchangeRatesDao.getAllExchangeRates()

    override fun getExchangeRateLiveData(code: String) =
        exchangeRatesDao.getExchangeRateLiveData(code)

    override suspend fun getExchangeRate(code: String) = exchangeRatesDao.getExchangeRate(code)

    override fun getExchangeRatesLiveData(codes: List<String>) =
        exchangeRatesDao.getExchangeRatesLiveData(codes)

    override suspend fun getExchangeRates(codes: List<String>) =
        exchangeRatesDao.getExchangeRates(codes)

    override suspend fun getExchangeRatesWithoutWritingToDatabase(mainCurrency: String) = try {
        exchangeRatesDataSource.getExchangeRates(symbolsDao.getAllSymbols().map { it.code }
            .toMutableList(), mainCurrency)
    } catch (e: UnknownHostException) {
        null
    }

    override suspend fun writeExchangeRatesToDatabase(exchangeRates: MutableList<ExchangeRate>) {
        exchangeRatesDao.insertAllExchangeRates(exchangeRates)
    }

    override suspend fun deleteAllExchangeRates() = exchangeRatesDao.deleteAll()

    override fun getNetworkErrorLiveData() = networkErrorLiveData
}