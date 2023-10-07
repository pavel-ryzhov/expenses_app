package com.expenses.mngr.domain.statistics

import com.expenses.mngr.entities.expense.Expense
import com.expenses.mngr.entities.sorting.Sorting
import javax.inject.Singleton

@Singleton
interface StatisticsRepository {
    suspend fun fetchExpenses(descriptionFilter: String, sorting: Sorting?): MutableList<Expense>
}