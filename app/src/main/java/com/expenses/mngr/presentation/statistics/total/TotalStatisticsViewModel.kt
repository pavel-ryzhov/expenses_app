package com.expenses.mngr.presentation.statistics.total

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.expenses.mngr.R
import com.expenses.mngr.data.preferences.AppPreferences
import com.expenses.mngr.data.services.expenses_statistics.ExpensesStatisticsService
import com.expenses.mngr.entities.category.Category
import com.expenses.mngr.entities.expense.Amount
import com.expenses.mngr.presentation.value_formatters.PercentageCurrencyValueFormatter
import com.expenses.mngr.utils.chooseLessSimilarColor
import com.github.mikephil.charting.data.PieData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TotalStatisticsViewModel @Inject constructor(
    private val expensesStatisticsService: ExpensesStatisticsService,
    private val appPreferences: AppPreferences,
    application: Application
) : AndroidViewModel(application) {

    val totalLiveData = MutableLiveData<Amount>()
    val numberOfExpensesLiveData = MutableLiveData<Int>()
    val pieChartStatisticsLiveData = MutableLiveData<PieData>()
    val legendLiveData = MutableLiveData<Category>()
    val hasExpensesLiveData = MutableLiveData<Boolean>()

    private val categoriesFilters = mutableSetOf<String>()

    fun fetchData() = viewModelScope.launch(Dispatchers.IO) {
        val numberOfExpenses = expensesStatisticsService.getNumberOfExpenses()
        val hasExpenses = numberOfExpenses > 0
        hasExpensesLiveData.postValue(hasExpenses)
        if (hasExpenses) {
            numberOfExpensesLiveData.postValue(numberOfExpenses)
            fetchPieChartStatistics()
            fetchTotal()
            fetchLegendData()
        }
    }

    private fun fetchTotal() = viewModelScope.launch(Dispatchers.IO) {
        totalLiveData.postValue(expensesStatisticsService.getTotal(categoriesFilters))
    }

    private fun fetchPieChartStatistics() = viewModelScope.launch(Dispatchers.IO) {
        pieChartStatisticsLiveData.postValue(
            expensesStatisticsService.getPieChartStatistics(categoriesFilters).apply {
                setValueFormatter(
                    PercentageCurrencyValueFormatter(
                        this.yValueSum.toDouble(),
                        appPreferences.getDoubleRounding()
                    )
                )
                setValueTextSize(12f)
                val color1 = ContextCompat.getColor(getApplication(), R.color.blue)
                val color2 = ContextCompat.getColor(getApplication(), R.color.milky_white)
                setValueTextColors(colors.map { chooseLessSimilarColor(it, color1, color2) })
            }
        )
    }


    private fun fetchNumberOfExpenses() = viewModelScope.launch(Dispatchers.IO) {
        numberOfExpensesLiveData.postValue(
            expensesStatisticsService.getNumberOfExpenses(
                categoriesFilters
            )
        )
    }

    private fun fetchLegendData() = viewModelScope.launch(Dispatchers.IO) {
        legendLiveData.postValue(expensesStatisticsService.getNonZeroCategories())
    }

    fun addCategoryFilter(category: Category) {
        addCategoryFilterRecursively(category)
        fetchFilterableData()
    }

    fun removeCategoryFilter(category: Category) {
        categoriesFilters.removeAll(categoriesFilters.filter { it.startsWith(category.fullName) }
            .toSet())
        fetchFilterableData()
    }

    private fun fetchFilterableData() {
        fetchPieChartStatistics()
        fetchTotal()
        fetchNumberOfExpenses()
    }

    private fun addCategoryFilterRecursively(category: Category) {
        categoriesFilters.add(category.fullName)
        if (category.hasSubCategories())
            for (subCategory in category.subCategories)
                addCategoryFilterRecursively(subCategory)
    }
}