package com.expenses.manager.domain.statistics

import com.expenses.manager.entities.expense.Expense
import com.expenses.manager.entities.sorting.Sorting
import javax.inject.Singleton

@Singleton
interface StatisticsRepository {
    suspend fun fetchExpenses(descriptionFilter: String, sorting: Sorting?): MutableList<Expense>
}