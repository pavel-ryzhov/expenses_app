package com.example.expenses.data.repository.settings

import androidx.lifecycle.MutableLiveData
import com.example.expenses.data.data_sources.local.dao.CategoriesDao
import com.example.expenses.data.data_sources.local.dao.ExchangeRatesDao
import com.example.expenses.data.data_sources.local.dao.ExpensesDao
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.data.repository.exchange_rates.ExchangeRatesRepository
import com.example.expenses.data.services.currency_converter.CurrenciesConverterService
import com.example.expenses.data.wrappers.RemoteExchangeRatesDataSourceWrapper
import com.example.expenses.entities.exchange_rates.ExchangeRate
import com.example.expenses.entities.expense.Amount
import com.example.expenses.entities.expense.Expense
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val expensesDao: ExpensesDao,
    private val exchangeRatesDao: ExchangeRatesDao,
    private val appPreferences: AppPreferences,
    private val exchangeRatesDataSource: RemoteExchangeRatesDataSourceWrapper,
    private val exchangeRatesRepository: ExchangeRatesRepository
) : SettingsRepository() {

    override val mainCurrencyChangedLiveData = MutableLiveData<Unit?>()
    override val networkErrorLiveData = MutableLiveData<Unit?>()
    override val stateChangedLiveData = MutableLiveData<String>()
    override val changingMainCurrencyStartedLiveData = MutableLiveData<Unit?>()

    override suspend fun changeMainCurrency(currencyTo: String) {
        changingMainCurrencyStartedLiveData.postValue(Unit)
        val latestExpenses = mutableListOf<Expense>()
        val nonLatestExpenses = mutableMapOf<Triple<Int, Int, Int>, MutableList<Expense>>()
        val nonLatestRates = mutableMapOf<Triple<Int, Int, Int>, Float>()
        val newLatestRates: List<ExchangeRate>
        for (expense in expensesDao.getAllExpenses()){
            if (expense.useLatestRates()){
                latestExpenses.add(expense)
            } else {
                val date = Triple(expense.year, expense.month, expense.day)
                nonLatestExpenses.getOrPut(date){ mutableListOf()}.add(expense)
            }
        }
        stateChangedLiveData.postValue("Fetching new exchange rates...")
        try {
            newLatestRates = exchangeRatesRepository.getLatestExchangeRates(currencyTo, true)!!
            for (date in nonLatestExpenses.keys) {
                nonLatestRates[date] = exchangeRatesDataSource.getExchangeRates(
                    listOf(currencyTo),
                    appPreferences.getMainCurrency(),
                    date.first,
                    date.second,
                    date.third
                ).first().value
            }
        }catch (e: UnknownHostException){
            networkErrorLiveData.postValue(Unit)
            return
        }
        stateChangedLiveData.postValue("Recalculating expenses...")
        convertExpenses(latestExpenses, currencyTo, exchangeRatesDao.getExchangeRate(currencyTo).value)
        for (entry in nonLatestExpenses){
            convertExpenses(entry.value, currencyTo, nonLatestRates[entry.key]!!)
        }
        stateChangedLiveData.postValue("Writing new exchange rates...")
        exchangeRatesDao.deleteAll()
        exchangeRatesDao.insertAllExchangeRates(newLatestRates)
        val secondaryCurrencies = appPreferences.getSecondaryCurrencies()
        if (secondaryCurrencies.contains(currencyTo))
            appPreferences.saveSecondaryCurrencies(secondaryCurrencies.apply { remove(currencyTo) })
        appPreferences.saveMainCurrency(currencyTo)
        mainCurrencyChangedLiveData.postValue(Unit)
    }

    override suspend fun changeSecondaryCurrency(currenciesTo: List<String>) {

    }

    private suspend fun getExpensesSortedByUseLatestRates(): Pair<MutableList<Expense>, MutableMap<Triple<Int, Int, Int>, MutableList<Expense>>> {

    }

    private suspend fun convertExpenses(expenses: List<Expense>, currencyTo: String, rate: Float){
        for (expense in expenses){
            val amount = expense.amount.asMap().toMutableMap()
            amount[currencyTo] = amount[appPreferences.getMainCurrency()]!! * rate
            amount.remove(appPreferences.getMainCurrency())
            expensesDao.updateExpenseAmount(expense.id, Amount(amount))
        }
    }
}