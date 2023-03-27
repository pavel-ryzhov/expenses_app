package com.example.expenses.data.services.expenses_statistics

import com.example.expenses.data.data_sources.local.dao.CategoriesDao
import com.example.expenses.data.data_sources.local.dao.ExpensesDao
import com.example.expenses.entities.category.Category
import com.example.expenses.entities.category.CategoryDBEntity
import com.github.mikephil.charting.data.*
import java.util.*
import javax.inject.Inject

class ExpensesStatisticsServiceImpl @Inject constructor(
    private val expensesDao: ExpensesDao,
    private val categoriesDao: CategoriesDao
) : ExpensesStatisticsService {
    override fun getTotalByDay(year: Int, month: Int, day: Int): Double {
        return expensesDao.getExpensesByDay(year, month, day).sumOf { it.amount }
    }

    override fun getTotalByMonth(year: Int, month: Int): Double {
        return expensesDao.getExpensesByMonth(year, month).sumOf { it.amount }
    }

    override fun getTotalByYear(year: Int): Double {
        return expensesDao.getExpensesByYear(year).sumOf { it.amount }
    }

    override fun getTotalToday(): Double {
        val calendar = GregorianCalendar()
        return getTotalByDay(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    override fun getTotalThisMonth(): Double {
        val calendar = GregorianCalendar()
        return getTotalByMonth(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH)
        )
    }

    override fun getTotalThisYear(): Double {
        val calendar = GregorianCalendar()
        return getTotalByYear(
            calendar.get(Calendar.YEAR)
        )
    }

    override fun getTotalForEachDayOfMonth(year: Int, month: Int): MutableList<Double> {
        val calendar = GregorianCalendar(year, month, 1)
        val result = mutableListOf<Double>()
        for (i in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            result.add(expensesDao.getExpensesByDay(year, month, i).sumOf { it.amount })
        }
        return result
    }

    override fun getTotalForEachDayOfMonthByCategory(
        year: Int,
        month: Int,
        category: CategoryDBEntity
    ): MutableList<Double> {
        val calendar = GregorianCalendar(year, month, 1)
        val result = mutableListOf<Double>()
        for (i in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            result.add(
                expensesDao.getExpensesByDayAndCategory(year, month, i, category.name)
                    .sumOf { it.amount })
        }
        return result
    }

    override fun getMaxOfMonth(year: Int, month: Int): Double {
        return getTotalForEachDayOfMonth(year, month).max()
    }

    override fun getMinOfMonth(year: Int, month: Int): Double {
        return getTotalForEachDayOfMonth(year, month).min()
    }

    override fun getLineChartStatisticsOfMonth(
        year: Int,
        month: Int,
        filter: Set<String>
    ): LineData {
        val dataSets = if ("All" !in filter) mutableListOf(
            LineDataSet(
                getTotalForEachDayOfMonth(
                    year,
                    month
                ).mapIndexed { index, it -> Entry((index + 1).toFloat(), it.toFloat()) }, "All"
            )
        ) else mutableListOf()
        dataSets.addAll(categoriesDao.getAllCategoryDBEntities().filter { it.name !in filter }
            .map { category ->
                LineDataSet(
                    getTotalForEachDayOfMonthByCategory(
                        year,
                        month,
                        category
                    ).mapIndexed { index, it -> Entry((index + 1).toFloat(), it.toFloat()) },
                    category.name
                ).apply {
                    color = category.color
                    setCircleColor(category.color)
                }
            }.filter { it.yMax != 0f })
        return LineData(dataSets.toList())
    }

    override fun getTotalByMonthAndCategory(
        year: Int,
        month: Int,
        category: CategoryDBEntity
    ): Double {
        return expensesDao.getExpensesByMonthAndCategory(year, month, category.name)
            .sumOf { it.amount }
    }

    override fun getPieChartStatisticsOfMonth(year: Int, month: Int, filter: Set<String>): PieData {
        val categories = categoriesDao.getAllCategoryDBEntities()
        val entries = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()
        categories.forEach { category ->
            val value = getTotalByMonthAndCategory(year, month, category).toFloat()
            if (value != 0f && category.name !in filter) {
                entries.add(PieEntry(value, category.name))
                colors.add(category.color)
            }
        }
        return PieData(PieDataSet(entries, "").apply { setColors(colors) })
    }

    override fun getNonZeroCategoryOfMonth(year: Int, month: Int): Category {
        return categoriesDao.getRootCategory().apply {
            removeZeroCategories(year, month, this)
        }
    }

    private fun removeZeroCategories(year: Int, month: Int, category: Category) {
        if (category.hasSubCategories()) {
            for (i in category.subCategories.indices.reversed())
                if (expensesDao.countExpensesOfCategoryAndSubCategories(
                        year,
                        month,
                        category.subCategories[i].fullName
                    ) == 0
                )
                    category.subCategories.removeAt(i)
                else removeZeroCategories(year, month, category.subCategories[i])
        }
    }

    override fun hasExpensesInMonth(year: Int, month: Int) = expensesDao.countExpensesInMonth(year, month) > 0
}