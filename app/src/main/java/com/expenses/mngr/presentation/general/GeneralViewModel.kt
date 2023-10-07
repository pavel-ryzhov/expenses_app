package com.expenses.mngr.presentation.general

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expenses.mngr.data.preferences.AppPreferences
import com.expenses.mngr.domain.exchange_rates.ExchangeRatesRepository
import com.expenses.mngr.data.services.expenses_statistics.ExpensesStatisticsService
import com.expenses.mngr.entities.expense.Amount
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GeneralViewModel @Inject constructor(
    private val exchangeRatesRepository: ExchangeRatesRepository,
    private val appPreferences: AppPreferences,
    private val expensesStatisticsService: ExpensesStatisticsService,
) : ViewModel() {

    val totalTodayLiveData = MutableLiveData<Amount>()
    val totalThisMonthLiveData = MutableLiveData<Amount>()
    val monthStatisticsLiveData = MutableLiveData<LineDataSet>()
    val networkErrorLiveData = exchangeRatesRepository.getNetworkErrorLiveData()

    private var firstLaunch = true

    fun fetchData() {
        fetchExchangeRates()
        fetchTotalToday()
        fetchTotalThisMonth()
        fetchMonthStatistics()
        firstLaunch = false
    }

    private fun fetchExchangeRates() {
        if (firstLaunch)
            viewModelScope.launch(Dispatchers.IO) {
                exchangeRatesRepository.fetchLatestExchangeRates(appPreferences.getMainCurrency())
            }
    }

    private fun fetchTotalToday() {
        viewModelScope.launch(Dispatchers.IO) {
            totalTodayLiveData.postValue(
                expensesStatisticsService.getTotalToday()
            )
        }
    }

    private fun fetchTotalThisMonth() {
        viewModelScope.launch(Dispatchers.IO) {
            totalThisMonthLiveData.postValue(
                expensesStatisticsService.getTotalThisMonth()
            )
        }
    }

    private fun fetchMonthStatistics() {
        viewModelScope.launch(Dispatchers.IO) {
            val entries = mutableListOf<Entry>()
            val calendar = GregorianCalendar()
            val expenses = expensesStatisticsService.getTotalForEachDayOfMonth(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH)
            )
            for (i in calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1 downTo calendar.get(
                Calendar.DAY_OF_MONTH
            )) {
                if (expenses[i].isZero())
                    expenses.removeLast()
                else break
            }
            for (i in expenses.indices) {
                entries.add(Entry(i + 1f, expenses[i].get(appPreferences.getMainCurrency()).toFloat()))
            }
            monthStatisticsLiveData.postValue(LineDataSet(entries, ""))
        }
    }

    fun getAppPreferences() = appPreferences

    fun notifyFragmentStopped(){
        networkErrorLiveData.postValue(null)
    }
}