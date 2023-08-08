package com.example.expenses.data.repository.settings

import androidx.lifecycle.MutableLiveData
import com.example.expenses.data.data_sources.local.dao.ExchangeRatesDao
import com.example.expenses.data.data_sources.local.dao.ExpensesDao
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.data.repository.exchange_rates.ExchangeRatesRepository
import com.example.expenses.data.wrappers.RemoteExchangeRatesDataSourceWrapper
import com.example.expenses.entities.exchange_rates.ExchangeRate
import com.example.expenses.entities.expense.Amount
import com.example.expenses.entities.expense.Expense
import com.example.expenses.extensions.toMap
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
    override val secondaryCurrenciesChangedLiveData = MutableLiveData<Unit?>()
    override val changingSecondaryCurrenciesStartedLiveData = MutableLiveData<Unit?>()

    override suspend fun changeMainCurrency(currencyTo: String) {
        changingMainCurrencyStartedLiveData.postValue(Unit)
        val secondaryCurrencies = appPreferences.getSecondaryCurrencies()
        if (secondaryCurrencies.contains(currencyTo)) {
            if (secondaryCurrencies.size == 1) {
                appPreferences.saveSecondaryCurrencies(listOf(appPreferences.getMainCurrency()))
            } else {
                stateChangedLiveData.postValue("Processing expenses...")
                expensesDao.getAllExpenses().forEach {
                    expensesDao.updateExpenseAmount(
                        it.id,
                        Amount(
                            it.amount.asMap().toMutableMap()
                                .apply { remove(appPreferences.getMainCurrency()) })
                    )
                    appPreferences.saveSecondaryCurrencies(secondaryCurrencies.apply {
                        remove(
                            currencyTo
                        )
                    })
                }
            }
        } else {
            stateChangedLiveData.postValue("Fetching exchange rates...")
            val expensesByUseLatestRates = getExpensesSortedByUseLatestRates()
            val nonLatestRates = mutableMapOf<Triple<Int, Int, Int>, Float>()
            val newLatestRates: List<ExchangeRate>
            try {
                newLatestRates = exchangeRatesRepository.getLatestExchangeRates(currencyTo, true)!!
                for (date in expensesByUseLatestRates.second.keys) {
                    nonLatestRates[date] = exchangeRatesDataSource.getExchangeRates(
                        listOf(currencyTo),
                        appPreferences.getMainCurrency(),
                        date.first,
                        date.second,
                        date.third
                    ).first().value
                }
            } catch (e: UnknownHostException) {
                networkErrorLiveData.postValue(Unit)
                return
            }
            stateChangedLiveData.postValue("Recalculating expenses...")
            convertExpenses(
                expensesByUseLatestRates.first,
                currencyTo,
                exchangeRatesDao.getExchangeRate(currencyTo).value
            )
            for (entry in expensesByUseLatestRates.second) {
                convertExpenses(entry.value, currencyTo, nonLatestRates[entry.key]!!)
            }
            stateChangedLiveData.postValue("Writing new exchange rates...")
            exchangeRatesDao.deleteAll()
            exchangeRatesDao.insertAllExchangeRates(newLatestRates)
        }
        appPreferences.saveMainCurrency(currencyTo)
        mainCurrencyChangedLiveData.postValue(Unit)
    }

    override suspend fun changeSecondaryCurrencies(currenciesTo: List<String>) {
        changingSecondaryCurrenciesStartedLiveData.postValue(Unit)
        val oldSecondaryCurrencies = appPreferences.getSecondaryCurrencies()
        val newCurrencies = currenciesTo.filter { it !in oldSecondaryCurrencies }
        if (newCurrencies.isNotEmpty()) {
            stateChangedLiveData.postValue("Fetching exchange rates...")
            val expensesByUseLatestRates = getExpensesSortedByUseLatestRates()
            val nonLatestRates = mutableMapOf<Triple<Int, Int, Int>, MutableMap<String, Float>>()
            try {
                for (date in expensesByUseLatestRates.second.keys) {
                    nonLatestRates[date] = exchangeRatesDataSource.getExchangeRates(
                        newCurrencies,
                        appPreferences.getMainCurrency(),
                        date.first,
                        date.second,
                        date.third
                    ).toMap()
                }
            } catch (e: UnknownHostException) {
                networkErrorLiveData.postValue(Unit)
                return
            }
            stateChangedLiveData.postValue("Recalculating expenses...")
            convertExpensesSecondaryCurrencies(
                expensesByUseLatestRates.first,
                currenciesTo,
                exchangeRatesDao.getExchangeRates(newCurrencies).toMap()
            )
            for (entry in expensesByUseLatestRates.second) {
                convertExpensesSecondaryCurrencies(
                    entry.value,
                    currenciesTo,
                    nonLatestRates[entry.key]!!
                )
            }
        } else {
            val difference = oldSecondaryCurrencies.filter { it !in currenciesTo }.toSet()
            if (difference.isNotEmpty()) {
                stateChangedLiveData.postValue("Recalculating expenses...")
                expensesDao.getAllExpenses().forEach {
                    expensesDao.updateExpenseAmount(it.id, Amount(it.amount.asMap().minus(difference)))
                }
            }
        }
        appPreferences.saveSecondaryCurrencies(currenciesTo)
        secondaryCurrenciesChangedLiveData.postValue(Unit)
    }

    private suspend fun getExpensesSortedByUseLatestRates(): Pair<MutableList<Expense>, MutableMap<Triple<Int, Int, Int>, MutableList<Expense>>> {
        val latestExpenses = mutableListOf<Expense>()
        val nonLatestExpenses = mutableMapOf<Triple<Int, Int, Int>, MutableList<Expense>>()
        for (expense in expensesDao.getAllExpenses()) {
            if (expense.useLatestRates()) {
                latestExpenses.add(expense)
            } else {
                val date = Triple(expense.year, expense.month, expense.day)
                nonLatestExpenses.getOrPut(date) { mutableListOf() }.add(expense)
            }
        }
        return Pair(latestExpenses, nonLatestExpenses)
    }

    private suspend fun convertExpenses(expenses: List<Expense>, currencyTo: String, rate: Float) {
        for (expense in expenses) {
            val amount = expense.amount.asMap().toMutableMap()
            amount[currencyTo] = amount[appPreferences.getMainCurrency()]!! * rate
            amount.remove(appPreferences.getMainCurrency())
            expensesDao.updateExpenseAmount(expense.id, Amount(amount))
        }
    }

    private suspend fun convertExpensesSecondaryCurrencies(
        expenses: List<Expense>,
        currenciesTo: List<String>,
        rates: Map<String, Float>
    ) {
        for (expense in expenses) {
            val amount = expense.amount.asMap()
                .filterKeys { it == appPreferences.getMainCurrency() || it in currenciesTo }
                .toMutableMap()
            for (rate in rates) {
                amount[rate.key] = amount[appPreferences.getMainCurrency()]!! * rate.value
            }
            expensesDao.updateExpenseAmount(expense.id, Amount(amount))
        }
    }
}