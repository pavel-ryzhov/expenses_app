package com.example.expenses.data.repository.statistics

import com.example.expenses.entities.expense.Expense
import com.example.expenses.entities.sorting.Sorting
import javax.inject.Singleton

@Singleton
interface StatisticsRepository {
    suspend fun fetchExpenses(descriptionFilter: String, sorting: Sorting?): MutableList<Expense>
}