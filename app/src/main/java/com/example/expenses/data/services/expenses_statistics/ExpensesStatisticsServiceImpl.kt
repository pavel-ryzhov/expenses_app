package com.example.expenses.data.services.expenses_statistics

import com.example.expenses.data.data_sources.local.dao.CategoriesDao
import com.example.expenses.data.data_sources.local.dao.ExpensesDao
import com.example.expenses.entities.category.Category
import com.example.expenses.entities.category.CategoryDBEntity
import com.example.expenses.entities.expense.Expense
import com.example.expenses.extensions.roundWithAccuracy
import com.github.mikephil.charting.data.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpensesStatisticsServiceImpl @Inject constructor(
    private val expensesDao: ExpensesDao,
    private val categoriesDao: CategoriesDao
) : ExpensesStatisticsService {
    override suspend fun getTotalByDay(year: Int, month: Int, day: Int, filter: Set<String>): Double {
        return expensesDao.getExpensesByDay(year, month, day).filter { it.category !in filter }.sumOf { it.amount }
    }

    override suspend fun getTotalByMonth(year: Int, month: Int, filter: Set<String>): Double {
        return expensesDao.getExpensesByMonth(year, month).filter { it.category !in filter }.sumOf { it.amount }
    }

    override suspend fun getTotalByYear(year: Int): Double {
        return expensesDao.getExpensesByYear(year).sumOf { it.amount }
    }

    override suspend fun getTotalToday(): Double {
        val calendar = GregorianCalendar()
        return getTotalByDay(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    override suspend fun getTotalThisMonth(): Double {
        val calendar = GregorianCalendar()
        return getTotalByMonth(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH)
        )
    }

    override suspend fun getTotalThisYear(): Double {
        val calendar = GregorianCalendar()
        return getTotalByYear(
            calendar.get(Calendar.YEAR)
        )
    }

    override suspend fun getTotalForEachDayOfMonth(year: Int, month: Int): MutableList<Double> {
        val calendar = GregorianCalendar(year, month, 1)
        val result = mutableListOf<Double>()
        for (i in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            result.add(expensesDao.getExpensesByDay(year, month, i).sumOf { it.amount })
        }
        return result
    }

    override suspend fun getTotalForEachDayOfMonthByCategory(
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

    override suspend fun getLineChartStatisticsOfMonth(
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
                    setDrawCircles(false)
                }
            }.filter { it.yMax != 0f })
        return LineData(dataSets.toList())
    }

    override suspend fun getLineChartStatisticsOfDay(
        year: Int,
        month: Int,
        day: Int,
        filter: Set<String>
    ): LineData {
        val expenses1 = expensesDao.getExpensesByDay(year, month, day)
        val categories = expenses1.map { it.category }.toMutableSet().apply { add("All") }
            .filter { it !in filter }.toSet()
        val dataSets = categories.map { category ->
            val list = MutableList(24 * 60 / 5) { 0.0 }
            expenses1.filter { category == "All" || it.category == category }.forEach {
                list[it.hour * 60 / 5 + it.minute.toDouble().roundWithAccuracy(5) / 5] = it.amount
            }
            LineDataSet(
                list.mapIndexed { index, d -> Entry(index.toFloat(), d.toFloat()) },
                ""
            ).apply {
                if (category != "All") {
                    color = categoriesDao.getCategoryDBEntityByName(category).color
                } else label = "All"
                setDrawCircles(false)
            }
        }.filter { it.yMax != 0f }
        return LineData(dataSets)
    }

    override suspend fun getTotalByMonthAndCategory(
        year: Int,
        month: Int,
        category: CategoryDBEntity
    ): Double {
        return expensesDao.getExpensesByMonthAndCategory(year, month, category.name)
            .sumOf { it.amount }
    }

    override suspend fun getPieChartStatisticsOfMonth(year: Int, month: Int, filter: Set<String>): PieData {
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

    override suspend fun getNonZeroCategoryOfMonth(year: Int, month: Int): Category {
        return categoriesDao.getRootCategory().apply {
            removeZeroCategoriesOfMonth(year, month, this)
        }
    }

    override suspend fun getNonZeroCategoryOfDay(year: Int, month: Int, day: Int): Category {
        return categoriesDao.getRootCategory().apply {
            removeZeroCategoriesOfDay(year, month, day, this)
        }
    }

    override suspend fun getNonZeroCategories(): Category {
        return categoriesDao.getRootCategory().apply {
            removeZeroCategories(this)
        }
    }

    private suspend fun removeZeroCategoriesOfMonth(year: Int, month: Int, category: Category) {
        if (category.hasSubCategories()) {
            for (i in category.subCategories.indices.reversed())
                if (expensesDao.countExpensesOfCategoryAndSubCategoriesByMonth(
                        year,
                        month,
                        category.subCategories[i].fullName
                    ) == 0
                )
                    category.subCategories.removeAt(i)
                else removeZeroCategoriesOfMonth(year, month, category.subCategories[i])
        }
    }

    private suspend fun removeZeroCategoriesOfDay(year: Int, month: Int, day: Int, category: Category) {
        if (category.hasSubCategories()) {
            for (i in category.subCategories.indices.reversed())
                if (expensesDao.countExpensesOfCategoryAndSubCategoriesByDay(
                        year,
                        month,
                        day,
                        category.subCategories[i].fullName
                    ) == 0
                )
                    category.subCategories.removeAt(i)
                else removeZeroCategoriesOfDay(year, month, day, category.subCategories[i])
        }
    }

    private suspend fun removeZeroCategories(category: Category) {
        if (category.hasSubCategories()) {
            for (i in category.subCategories.indices.reversed())
                if (expensesDao.countExpensesOfCategoryAndSubCategories(category.subCategories[i].fullName) == 0)
                    category.subCategories.removeAt(i)
                else removeZeroCategories(category.subCategories[i])
        }
    }

    override suspend fun hasExpensesInMonth(year: Int, month: Int) =
        expensesDao.countExpensesInMonth(year, month) > 0

    override suspend fun hasExpensesInDay(year: Int, month: Int, day: Int) =
        expensesDao.countExpensesInDay(year, month, day) > 0

    override suspend fun getNumberOfExpenses(filter: Set<String>): Int {
        return expensesDao.countExpensesWithFilter(filter)
    }

    override suspend fun getTotal(filter: Set<String>): Double {
        return expensesDao.getAllExpenses().filter { it.category !in filter }.sumOf { it.amount }
    }

    override suspend fun getPieChartStatistics(filter: Set<String>): PieData {
        val categories = categoriesDao.getAllCategoryDBEntities()
        val entries = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()
        categories.forEach { category ->
            val value = getTotalByCategory(category).toFloat()
            if (value != 0f && category.name !in filter) {
                entries.add(PieEntry(value, category.name))
                colors.add(category.color)
            }
        }
        return PieData(PieDataSet(entries, "").apply { setColors(colors) })
    }

    override suspend fun getTotalByCategory(category: CategoryDBEntity): Double {
        return expensesDao.getExpensesByCategory(category.name).sumOf { it.amount }
    }
}