package com.example.expenses.presentation.statistics.monthly

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.expenses.R
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.data.services.expenses_statistics.ExpensesStatisticsService
import com.example.expenses.entities.category.Category
import com.example.expenses.extensions.roundAndFormat
import com.example.expenses.presentation.value_formatters.PercentageCurrencyValueFormatter
import com.example.expenses.utils.chooseLessSimilarColor
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR
import javax.inject.Inject

@HiltViewModel
class MonthlyStatisticsViewModel @Inject constructor(
    private val expensesStatisticsService: ExpensesStatisticsService,
    private val appPreferences: AppPreferences,
    application: Application
) : AndroidViewModel(application) {

    val totalLiveData = MutableLiveData<String>()
    val lineChartStatisticsLiveData = MutableLiveData<LineData>()
    val pieChartStatisticsLiveData = MutableLiveData<PieData>()
    val legendLiveData = MutableLiveData<Category>()
    val hasExpensesLiveData = MutableLiveData<Boolean>()

    private val categoriesFilters = mutableSetOf<String>()
    lateinit var calendar: Calendar

    fun fetchData(calendar: Calendar = GregorianCalendar()) {
        this.calendar = calendar
        val year = calendar.get(YEAR)
        val month = calendar.get(MONTH)
        viewModelScope.launch(Dispatchers.IO) {
            val hasExpenses = expensesStatisticsService.hasExpensesInMonth(year, month)
            hasExpensesLiveData.postValue(hasExpenses)
            if (hasExpenses) {
                fetchFilterableData(calendar)
                fetchLegendData(year, month)
                fetchTotal(year, month)
            }
        }
    }

    private fun fetchFilterableData(calendar: Calendar){
        val year = calendar.get(YEAR)
        val month = calendar.get(MONTH)
        fetchLineChartStatistics(year, month)
        fetchPieChartStatistics(year, month)
        fetchTotal(year, month)
    }

    fun addCategoryFilter(category: Category){
        addCategoryFilterRecursively(category)
        fetchFilterableData(calendar)
    }

    fun removeCategoryFilter(category: Category){
        categoriesFilters.removeAll(categoriesFilters.filter { it.startsWith(category.fullName) }.toSet())
        fetchFilterableData(calendar)
    }

    private fun addCategoryFilterRecursively(category: Category){
        categoriesFilters.add(category.fullName)
        if (category.hasSubCategories())
            for (subCategory in category.subCategories)
                addCategoryFilterRecursively(subCategory)
    }

    private fun fetchTotal(year: Int, month: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val mainCurrency = appPreferences.getMainCurrency().code
            totalLiveData.postValue(
                "${
                    expensesStatisticsService.getTotalByMonth(
                        year,
                        month,
                        categoriesFilters
                    ).roundAndFormat()
                } $mainCurrency"
            )
        }
    }

    private fun fetchLineChartStatistics(year: Int, month: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            lineChartStatisticsLiveData.postValue(
                expensesStatisticsService.getLineChartStatisticsOfMonth(
                    year,
                    month,
                    categoriesFilters
                ).apply {
                    setDrawValues(false)
                    (getDataSetByLabel("All", true) as? LineDataSet)?.let {
                        it.color = ContextCompat.getColor(getApplication(), R.color.blue)
                        it.setDrawCircles(false)
                    }
                }
            )
        }
    }

    private fun fetchPieChartStatistics(year: Int, month: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            pieChartStatisticsLiveData.postValue(
                expensesStatisticsService.getPieChartStatisticsOfMonth(
                    year,
                    month,
                    categoriesFilters
                ).apply {
                    setValueFormatter(
                        PercentageCurrencyValueFormatter(
                            appPreferences.getMainCurrency().code,
                            this.yValueSum.toDouble()
                        )
                    )
                    val color1 = ContextCompat.getColor(getApplication(), R.color.blue)
                    val color2 = ContextCompat.getColor(getApplication(), R.color.milky_white)
                    setValueTextColors(colors.map { chooseLessSimilarColor(it, color1, color2) })
                })
        }
    }

    private fun fetchLegendData(year: Int, month: Int){
        viewModelScope.launch(Dispatchers.IO) {
            legendLiveData.postValue(expensesStatisticsService.getNonZeroCategoryOfMonth(year, month).apply {
                subCategories.add(Category("All", "All", this, color = ContextCompat.getColor(getApplication(), R.color.blue)))
            })
        }
    }
}
