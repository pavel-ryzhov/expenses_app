package com.expenses.manager.domain.statistics

import com.expenses.manager.data.data_sources.local.dao.ExpensesDao
import com.expenses.manager.entities.expense.Expense
import com.expenses.manager.entities.sorting.Sorting
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsRepositoryImpl @Inject constructor(
    private val expensesDao: ExpensesDao
) : StatisticsRepository {
    override suspend fun fetchExpenses(
        descriptionFilter: String,
        sorting: Sorting?
    ): MutableList<Expense> {
        val result = (sorting ?: Sorting.DATE).sortExpenses(expensesDao.getAllExpenses())
        return if (descriptionFilter.isNotBlank()) result.filter { it.description.lowercase().contains(descriptionFilter.lowercase()) }.toMutableList() else result
    }
}