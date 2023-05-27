package com.example.expenses.data.services.expenses_statistics

import com.example.expenses.entities.category.Category
import com.example.expenses.entities.category.CategoryDBEntity
import com.example.expenses.entities.expense.Expense
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.PieData
import javax.inject.Singleton

@Singleton
interface ExpensesStatisticsService {
    fun getTotalByDay(year: Int, month: Int, day: Int, filter: Set<String> = setOf()): Double
    fun getTotalByMonth(year: Int, month: Int, filter: Set<String> = setOf()): Double
    fun getTotalByYear(year: Int): Double
    fun getTotalToday(): Double
    fun getTotalThisMonth(): Double
    fun getTotalThisYear(): Double
    fun getTotalForEachDayOfMonth(year: Int, month: Int): MutableList<Double>
    fun getTotalForEachDayOfMonthByCategory(year: Int, month: Int, category: CategoryDBEntity): MutableList<Double>
    fun getTotalByMonthAndCategory(year: Int, month: Int, category: CategoryDBEntity): Double
    fun getLineChartStatisticsOfMonth(year: Int, month: Int, filter: Set<String> = setOf()): LineData
    fun getPieChartStatisticsOfMonth(year: Int, month: Int, filter: Set<String> = setOf()): PieData
    fun getNonZeroCategoryOfMonth(year: Int, month: Int): Category
    fun getNonZeroCategoryOfDay(year: Int, month: Int, day: Int): Category
    fun hasExpensesInMonth(year: Int, month: Int): Boolean
    fun hasExpensesInDay(year: Int, month: Int, day: Int): Boolean
    fun getLineChartStatisticsOfDay(year: Int, month: Int, day: Int, filter: Set<String> = setOf()): LineData
    fun getNumberOfExpenses(filter: Set<String> = setOf()): Int
    fun getTotal(filter: Set<String> = setOf()): Double
    fun getNonZeroCategories(): Category
    fun getPieChartStatistics(filter: Set<String> = setOf()): PieData
    fun getTotalByCategory(category: CategoryDBEntity): Double
}