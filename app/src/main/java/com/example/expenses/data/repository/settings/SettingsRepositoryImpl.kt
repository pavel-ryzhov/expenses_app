package com.example.expenses.data.repository.settings

import androidx.lifecycle.MutableLiveData
import com.example.expenses.data.data_sources.local.dao.CategoriesDao
import com.example.expenses.data.data_sources.local.dao.ExpensesDao
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.data.repository.exchange_rates.ExchangeRatesRepository
import com.example.expenses.data.services.currency_converter.CurrenciesConverterService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val expensesDao: ExpensesDao,
    private val converterService: CurrenciesConverterService,
    private val appPreferences: AppPreferences,
    private val exchangeRatesRepository: ExchangeRatesRepository
) : SettingsRepository() {

    override val mainCurrencyChangedLiveData = MutableLiveData<Unit?>()
    override val networkErrorLiveData = MutableLiveData<Unit?>()
    override val stateChangedLiveData = MutableLiveData<String>()
    override val changingMainCurrencyStartedLiveData = MutableLiveData<Unit?>()

    override suspend fun changeMainCurrency(currencyTo: String) {
//        changingMainCurrencyStartedLiveData.postValue(Unit)
//        stateChangedLiveData.postValue("Fetching new exchange rates...")
//        val additionalDelays = 4000L //TODO delete this
//        Thread.sleep(additionalDelays)//TODO
//        val exchangeRates = exchangeRatesRepository.getExchangeRatesWithoutWritingToDatabase(currencyTo)
//        if (exchangeRates != null){
//            stateChangedLiveData.postValue("Recalculating expenses...")
//            Thread.sleep(additionalDelays)
//            expensesDao.getAllExpensesAmountsAsMap().entries.forEach { entry -> expensesDao.updateExpenseAmount(entry.key, converterService.fromMainCurrency(entry.value, currencyTo)) }
//            stateChangedLiveData.postValue("Deleting old exchange rates...")
//            Thread.sleep(additionalDelays)
//            exchangeRatesRepository.deleteAllExchangeRates()
//            stateChangedLiveData.postValue("Writing new exchange rates...")
//            Thread.sleep(additionalDelays)
//            exchangeRatesRepository.writeExchangeRatesToDatabase(exchangeRates)
//            appPreferences.saveMainCurrency(currencyTo)
//            Thread.sleep(additionalDelays)
//            val secondaryCurrencies = appPreferences.getSecondaryCurrenciesCodes()
//            if (currencyTo in secondaryCurrencies) {
//                appPreferences.saveSecondaryCurrenciesCodes(secondaryCurrencies.apply {
//                    remove(currencyTo)
//                })
//            }
//            mainCurrencyChangedLiveData.postValue(Unit)
//        } else {
//            networkErrorLiveData.postValue(Unit)
//        }
    }
}