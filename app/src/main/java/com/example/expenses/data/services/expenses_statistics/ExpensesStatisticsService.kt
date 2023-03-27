package com.example.expenses.data.services.expenses_statistics

import com.example.expenses.entities.category.Category
import com.example.expenses.entities.category.CategoryDBEntity
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.PieData

interface ExpensesStatisticsService {
    fun getTotalByDay(year: Int, month: Int, day: Int): Double
    fun getTotalByMonth(year: Int, month: Int): Double
    fun getTotalByYear(year: Int): Double
    fun getTotalToday(): Double
    fun getTotalThisMonth(): Double
    fun getTotalThisYear(): Double
    fun getTotalForEachDayOfMonth(year: Int, month: Int): MutableList<Double>
    fun getMaxOfMonth(year: Int, month: Int): Double
    fun getMinOfMonth(year: Int, month: Int): Double
    fun getTotalForEachDayOfMonthByCategory(year: Int, month: Int, category: CategoryDBEntity): MutableList<Double>
    fun getTotalByMonthAndCategory(year: Int, month: Int, category: CategoryDBEntity): Double
    fun getLineChartStatisticsOfMonth(year: Int, month: Int, filter: Set<String> = setOf()): LineData
    fun getPieChartStatisticsOfMonth(year: Int, month: Int, filter: Set<String> = setOf()): PieData
    fun getNonZeroCategoryOfMonth(year: Int, month: Int): Category
    fun hasExpensesInMonth(year: Int, month: Int): Boolean
}