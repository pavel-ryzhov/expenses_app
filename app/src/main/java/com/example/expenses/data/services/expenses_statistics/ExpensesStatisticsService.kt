package com.example.expenses.data.services.expenses_statistics

import com.example.expenses.entities.category.Category
import com.example.expenses.entities.category.CategoryDBEntity
import com.example.expenses.entities.expense.Expense
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.PieData
import javax.inject.Singleton

@Singleton
interface ExpensesStatisticsService {
    suspend fun getTotalByDay(year: Int, month: Int, day: Int, filter: Set<String> = setOf()): MutableMap<String, Double>?
    suspend fun getTotalByMonth(year: Int, month: Int, filter: Set<String> = setOf()): MutableMap<String, Double>?
    suspend fun getTotalByYear(year: Int): MutableMap<String, Double>?
    suspend fun getTotalToday(): MutableMap<String, Double>?
    suspend fun getTotalThisMonth(): MutableMap<String, Double>?
    suspend fun getTotalThisYear(): MutableMap<String, Double>?
    suspend fun getTotalForEachDayOfMonth(year: Int, month: Int): MutableList<MutableMap<String, Double>?>
    suspend fun getTotalForEachDayOfMonthByCategory(year: Int, month: Int, category: CategoryDBEntity): MutableList<MutableMap<String, Double>?>
    suspend fun getTotalByMonthAndCategory(year: Int, month: Int, category: CategoryDBEntity): MutableMap<String, Double>?
    suspend fun getLineChartStatisticsOfMonth(year: Int, month: Int, filter: Set<String> = setOf()): LineData
    suspend fun getPieChartStatisticsOfMonth(year: Int, month: Int, filter: Set<String> = setOf()): PieData
    suspend fun getNonZeroCategoryOfMonth(year: Int, month: Int): Category
    suspend fun getNonZeroCategoryOfDay(year: Int, month: Int, day: Int): Category
    suspend fun hasExpensesInMonth(year: Int, month: Int): Boolean
    suspend fun hasExpensesInDay(year: Int, month: Int, day: Int): Boolean
    suspend fun getLineChartStatisticsOfDay(year: Int, month: Int, day: Int, filter: Set<String> = setOf()): LineData
    suspend fun getNumberOfExpenses(filter: Set<String> = setOf()): Int
    suspend fun getTotal(filter: Set<String> = setOf()): MutableMap<String, Double>?
    suspend fun getNonZeroCategories(): Category
    suspend fun getPieChartStatistics(filter: Set<String> = setOf()): PieData
    suspend fun getTotalByCategory(category: CategoryDBEntity): MutableMap<String, Double>?
}