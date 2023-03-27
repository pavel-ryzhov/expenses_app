package com.example.expenses.presentation.general

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.data.repository.exchange_rates.ExchangeRatesRepository
import com.example.expenses.data.services.currency_converter.CurrenciesConverterService
import com.example.expenses.data.services.expenses_statistics.ExpensesStatisticsService
import com.example.expenses.extensions.roundAndFormat
import com.example.expenses.presentation.dialogs.amount_in_secondary_currencies.DataWrapper
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GeneralViewModel @Inject constructor(
    private val exchangeRatesRepository: ExchangeRatesRepository,
    private val appPreferences: AppPreferences,
    private val expensesStatisticsService: ExpensesStatisticsService,
    private val currenciesConverterService: CurrenciesConverterService
) : ViewModel() {

    val totalTodayLiveData = MutableLiveData<String>()
    val totalThisMonthLiveData = MutableLiveData<String>()
    val monthStatisticsLiveData = MutableLiveData<LineDataSet>()
    val networkErrorLiveData = MutableLiveData<Unit>()

    fun fetchData(){
        fetchExchangeRates()
        fetchTotalToday()
        fetchTotalThisMonth()
        fetchMonthStatistics()
    }

    fun getDataWrapper(): DataWrapper = DataWrapper(currenciesConverterService, appPreferences, viewModelScope)

    private fun fetchExchangeRates() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                exchangeRatesRepository.fetchExchangeRates(appPreferences.getMainCurrency())
            }catch (e: UnknownHostException){
                networkErrorLiveData.postValue(Unit)
            }
        }
    }
    private fun fetchTotalToday(){
        viewModelScope.launch(Dispatchers.IO) {
            totalTodayLiveData.postValue("${expensesStatisticsService.getTotalToday().roundAndFormat(appPreferences.getDoubleRounding())} ${appPreferences.getMainCurrency().code}")
        }
    }
    private fun fetchTotalThisMonth(){
        viewModelScope.launch(Dispatchers.IO) {
            totalThisMonthLiveData.postValue("${expensesStatisticsService.getTotalThisMonth().roundAndFormat(appPreferences.getDoubleRounding())} ${appPreferences.getMainCurrency().code}")
        }
    }
    private fun fetchMonthStatistics(){
        viewModelScope.launch(Dispatchers.IO) {
            val entries = mutableListOf<Entry>()
            val calendar = GregorianCalendar()
            val expenses = expensesStatisticsService.getTotalForEachDayOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
            for (i in calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1 downTo calendar.get(Calendar.DAY_OF_MONTH)){
                if (expenses[i] == 0.0)
                    expenses.removeLast()
                else break
            }
            for (i in expenses.indices){
                entries.add(Entry(i + 1f, expenses[i].toFloat()))
            }
            monthStatisticsLiveData.postValue(LineDataSet(entries, ""))
        }
    }

    fun getAppPreferences() = appPreferences
}