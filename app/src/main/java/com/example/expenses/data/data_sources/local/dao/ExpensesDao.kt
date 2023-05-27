package com.example.expenses.data.data_sources.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.expenses.entities.expense.Expense

@Dao
interface ExpensesDao {
    @Insert
    fun insertExpense(expense: Expense)

    @Insert
    fun insertExpenses(expenses: List<Expense>)

    @Query("SELECT * FROM expense")
    fun getAllExpenses(): MutableList<Expense>

    @Query("SELECT * FROM expense WHERE year = :year AND month = :month")
    fun getExpensesByMonth(year: Int, month: Int): MutableList<Expense>

    @Query("SELECT * FROM expense WHERE year = :year AND month = :month AND day = :day")
    fun getExpensesByDay(year: Int, month: Int, day: Int): MutableList<Expense>

    @Query("SELECT * FROM expense WHERE year = :year AND month = :month AND day = :day AND NOT category IN (:filter)")
    fun getExpensesByDayWithFilter(year: Int, month: Int, day: Int, filter: Set<String>): MutableList<Expense>

    @Query("SELECT * FROM expense WHERE year = :year")
    fun getExpensesByYear(year: Int): MutableList<Expense>

    @Delete
    fun deleteExpense(expense: Expense)

    @Delete
    fun deleteExpenses(expenses: List<Expense>)

    @Query("DELETE FROM expense WHERE year = :year AND month = :month")
    fun deleteByMonth(year: Int, month: Int)

    @Query("DELETE FROM expense WHERE year = :year AND month = :month AND day = :day")
    fun deleteByDay(year: Int, month: Int, day: Int)

    @Query("DELETE FROM expense WHERE id = :id")
    fun deleteById(id: Int)

    @Query("DELETE FROM expense WHERE id IN (:ids)")
    fun deleteByIds(ids: List<Int>)

    @Query("SELECT * FROM expense WHERE category = :categoryName")
    fun getExpensesByCategory(categoryName: String): MutableList<Expense>

    @Query("SELECT * FROM expense WHERE year = :year AND month = :month AND day = :day AND category = :categoryName")
    fun getExpensesByDayAndCategory(year: Int, month: Int, day: Int, categoryName: String): MutableList<Expense>

    @Query("SELECT * FROM expense WHERE year = :year AND month = :month AND category = :categoryName")
    fun getExpensesByMonthAndCategory(year: Int, month: Int, categoryName: String): MutableList<Expense>

    @Query("SELECT COUNT() FROM expense WHERE year = :year AND month = :month AND category LIKE :categoryName || '%'")
    fun countExpensesOfCategoryAndSubCategoriesByMonth(year: Int, month: Int, categoryName: String): Int

    @Query("SELECT COUNT() FROM expense WHERE year = :year AND month = :month AND day = :day AND category LIKE :categoryName || '%'")
    fun countExpensesOfCategoryAndSubCategoriesByDay(year: Int, month: Int, day: Int, categoryName: String): Int

    @Query("SELECT COUNT() FROM expense WHERE category LIKE :categoryName || '%'")
    fun countExpensesOfCategoryAndSubCategories(categoryName: String): Int

    @Query("SELECT COUNT() FROM expense WHERE year = :year AND month = :month")
    fun countExpensesInMonth(year: Int, month: Int): Int

    @Query("SELECT COUNT() FROM expense WHERE year = :year AND month = :month AND day = :day")
    fun countExpensesInDay(year: Int, month: Int, day: Int): Int

    @Query("SELECT COUNT() FROM expense")
    fun countExpenses(): Int

    @Query("SELECT COUNT() FROM expense WHERE NOT category IN (:filter)")
    fun countExpensesWithFilter(filter: Set<String>): Int
}