package com.example.expenses.data.services.expenses_statistics

import com.example.expenses.entities.category.Category
import com.example.expenses.entities.category.CategoryDBEntity
import com.example.expenses.entities.expense.Expense
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.PieData
import javax.inject.Singleton

@Singleton
interface ExpensesStatisticsService {
    suspend fun getTotalByDay(year: Int, month: Int, day: Int, filter: Set<String> = setOf()): Double
    suspend fun getTotalByMonth(year: Int, month: Int, filter: Set<String> = setOf()): Double
    suspend fun getTotalByYear(year: Int): Double
    suspend fun getTotalToday(): Double
    suspend fun getTotalThisMonth(): Double
    suspend fun getTotalThisYear(): Double
    suspend fun getTotalForEachDayOfMonth(year: Int, month: Int): MutableList<Double>
    suspend fun getTotalForEachDayOfMonthByCategory(year: Int, month: Int, category: CategoryDBEntity): MutableList<Double>
    suspend fun getTotalByMonthAndCategory(year: Int, month: Int, category: CategoryDBEntity): Double
    suspend fun getLineChartStatisticsOfMonth(year: Int, month: Int, filter: Set<String> = setOf()): LineData
    suspend fun getPieChartStatisticsOfMonth(year: Int, month: Int, filter: Set<String> = setOf()): PieData
    suspend fun getNonZeroCategoryOfMonth(year: Int, month: Int): Category
    suspend fun getNonZeroCategoryOfDay(year: Int, month: Int, day: Int): Category
    suspend fun hasExpensesInMonth(year: Int, month: Int): Boolean
    suspend fun hasExpensesInDay(year: Int, month: Int, day: Int): Boolean
    suspend fun getLineChartStatisticsOfDay(year: Int, month: Int, day: Int, filter: Set<String> = setOf()): LineData
    suspend fun getNumberOfExpenses(filter: Set<String> = setOf()): Int
    suspend fun getTotal(filter: Set<String> = setOf()): Double
    suspend fun getNonZeroCategories(): Category
    suspend fun getPieChartStatistics(filter: Set<String> = setOf()): PieData
    suspend fun getTotalByCategory(category: CategoryDBEntity): Double
}