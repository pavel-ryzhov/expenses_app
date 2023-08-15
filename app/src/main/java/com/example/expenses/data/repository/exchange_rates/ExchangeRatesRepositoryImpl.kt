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

    override suspend fun fetchLatestExchangeRates(mainCurrency: String, throwExceptionOnNetworkError: Boolean) {
        try {
            exchangeRatesDao.insertAllExchangeRates(
                exchangeRatesDataSource.getLatestExchangeRates(
                    symbolsDao.getAllSymbols().map { it.code }.toMutableList(), mainCurrency
                )
            )
        } catch (e: UnknownHostException) {
            if (throwExceptionOnNetworkError)
                throw UnknownHostException()
            else
                networkErrorLiveData.postValue(Unit)
        }  catch (e: Exception){
            e.printStackTrace()
            fetchLatestExchangeRates(mainCurrency, throwExceptionOnNetworkError)
        }
    }

    override fun getAllCachedLatestExchangeRatesLiveData() = exchangeRatesDao.getAllExchangeRatesLiveData()

    override suspend fun getAllCachedLatestExchangeRates() = exchangeRatesDao.getAllExchangeRates()

    override fun getCachedLatestExchangeRateLiveData(code: String) =
        exchangeRatesDao.getExchangeRateLiveData(code)

    override suspend fun getCachedLatestExchangeRate(code: String) = exchangeRatesDao.getExchangeRate(code)

    override fun getCachedLatestExchangeRatesLiveData(codes: List<String>) =
        exchangeRatesDao.getExchangeRatesLiveData(codes)

    override suspend fun getCachedLatestExchangeRates(codes: List<String>) =
        exchangeRatesDao.getExchangeRates(codes)

    override suspend fun getLatestExchangeRates(mainCurrency: String, throwExceptionOnNetworkError: Boolean): MutableList<ExchangeRate>? {
        return try {
            exchangeRatesDataSource.getLatestExchangeRates(symbolsDao.getAllSymbols().map { it.code }
                .toMutableList(), mainCurrency)
        } catch (e: UnknownHostException) {
            if (throwExceptionOnNetworkError)
                throw UnknownHostException()
            else {
                networkErrorLiveData.postValue(Unit)
                null
            }
        } catch (e: Exception){
            e.printStackTrace()
            getLatestExchangeRates(mainCurrency, throwExceptionOnNetworkError)
        }
    }

    override suspend fun writeLatestExchangeRatesToDatabase(exchangeRates: MutableList<ExchangeRate>) {
        exchangeRatesDao.insertAllExchangeRates(exchangeRates)
    }

    override suspend fun deleteAllLatestExchangeRates() = exchangeRatesDao.deleteAll()

    override fun getNetworkErrorLiveData() = networkErrorLiveData

    override suspend fun getExchangeRates(
        mainCurrency: String,
        year: Int,
        month: Int,
        day: Int,
        throwExceptionOnNetworkError: Boolean
    ): MutableList<ExchangeRate>? {
        return try {
            exchangeRatesDataSource.getExchangeRates(symbolsDao.getAllSymbols().map { it.code }
                .toMutableList(), mainCurrency, year, month, day)
        } catch (e: UnknownHostException) {
            if (throwExceptionOnNetworkError)
                throw UnknownHostException()
            else {
                networkErrorLiveData.postValue(Unit)
                null
            }
        } catch (e: Exception){
            e.printStackTrace()
            getExchangeRates(mainCurrency, year, month, day, throwExceptionOnNetworkError)
        }
    }
}