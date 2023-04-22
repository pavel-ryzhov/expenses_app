package com.example.expenses.presentation.statistics.daily

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.expenses.R
import com.example.expenses.data.services.expenses_statistics.ExpensesStatisticsService
import com.example.expenses.entities.category.Category
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DailyStatisticsViewModel @Inject constructor(
    private val expensesStatisticsService: ExpensesStatisticsService,
    application: Application
) : AndroidViewModel(application) {

    val lineChartStatisticsLiveData = MutableLiveData<LineData>()
    val legendLiveData = MutableLiveData<Category>()

    private val categoriesFilters = mutableSetOf<String>()
    private lateinit var calendar: Calendar

    fun fetchData(calendar: Calendar){
        this.calendar = calendar
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        fetchLineChartStatistics(year, month, day)
        fetchLegendData(year, month, day)
    }

    private fun fetchLineChartStatistics(year: Int, month: Int, day: Int){
        viewModelScope.launch(Dispatchers.IO) {
            lineChartStatisticsLiveData.postValue(
                expensesStatisticsService.getLineChartStatisticsOfDay(year, month, day, categoriesFilters).apply {
                    (getDataSetByLabel("All", true) as? LineDataSet)?.let {
                        it.color = ContextCompat.getColor(getApplication(), R.color.blue)
                        it.setDrawCircles(false)
                    }
                    setDrawValues(false)
                }
            )
        }
    }

    private fun fetchLegendData(year: Int, month: Int, day: Int){
        viewModelScope.launch(Dispatchers.IO) {
            legendLiveData.postValue(expensesStatisticsService.getNonZeroCategoryOfDay(year, month, day).apply {
                subCategories.add(Category("All", "All", this, color = ContextCompat.getColor(getApplication(), R.color.blue)))
            })
        }
    }

    private fun addCategoryFilterRecursively(category: Category){
        categoriesFilters.add(category.fullName)
        if (category.hasSubCategories())
            for (subCategory in category.subCategories)
                addCategoryFilterRecursively(subCategory)
    }

    fun addCategoryFilter(category: Category){
        addCategoryFilterRecursively(category)
        fetchFilterableData(calendar)
    }

    fun removeCategoryFilter(category: Category){
        categoriesFilters.removeAll(categoriesFilters.filter { it.startsWith(category.fullName) }.toSet())
        fetchFilterableData(calendar)
    }

    private fun fetchFilterableData(calendar: Calendar){
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        fetchLineChartStatistics(year, month, day)
    }
}