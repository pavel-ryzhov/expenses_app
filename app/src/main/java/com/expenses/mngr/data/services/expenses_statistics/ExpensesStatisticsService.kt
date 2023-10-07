package com.expenses.mngr.data.services.expenses_statistics

import com.expenses.mngr.entities.category.Category
import com.expenses.mngr.entities.category.CategoryDBEntity
import com.expenses.mngr.entities.expense.Amount
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.PieData
import javax.inject.Singleton

@Singleton
interface ExpensesStatisticsService {
    suspend fun getTotalByDay(year: Int, month: Int, day: Int, filter: Set<String> = setOf()): Amount
    suspend fun getTotalByMonth(year: Int, month: Int, filter: Set<String> = setOf()): Amount
    suspend fun getTotalByYear(year: Int): Amount
    suspend fun getTotalToday(): Amount
    suspend fun getTotalThisMonth(): Amount
    suspend fun getTotalThisYear(): Amount
    suspend fun getTotalForEachDayOfMonth(year: Int, month: Int): MutableList<Amount>
    suspend fun getTotalForEachDayOfMonthByCategory(year: Int, month: Int, category: CategoryDBEntity): MutableList<Amount>
    suspend fun getTotalByMonthAndCategory(year: Int, month: Int, category: CategoryDBEntity): Amount
    suspend fun getLineChartStatisticsOfMonth(year: Int, month: Int, filter: Set<String> = setOf()): LineData
    suspend fun getPieChartStatisticsOfMonth(year: Int, month: Int, filter: Set<String> = setOf()): PieData
    suspend fun getNonZeroCategoryOfMonth(year: Int, month: Int): Category
    suspend fun getNonZeroCategoryOfDay(year: Int, month: Int, day: Int): Category
    suspend fun hasExpensesInMonth(year: Int, month: Int): Boolean
    suspend fun hasExpensesInDay(year: Int, month: Int, day: Int): Boolean
    suspend fun getLineChartStatisticsOfDay(year: Int, month: Int, day: Int, filter: Set<String> = setOf()): LineData
    suspend fun getNumberOfExpenses(filter: Set<String> = setOf()): Int
    suspend fun getTotal(filter: Set<String> = setOf()): Amount
    suspend fun getNonZeroCategories(): Category
    suspend fun getPieChartStatistics(filter: Set<String> = setOf()): PieData
    suspend fun getTotalByCategory(category: CategoryDBEntity): Amount
}