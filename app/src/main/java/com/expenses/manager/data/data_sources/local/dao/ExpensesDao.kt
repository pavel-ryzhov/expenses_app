package com.expenses.manager.data.data_sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import com.expenses.manager.entities.expense.Amount
import com.expenses.manager.entities.expense.Expense

@Dao
interface ExpensesDao {
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Insert
    suspend fun insertExpenses(expenses: List<Expense>)

    @Query("SELECT * FROM expense")
    suspend fun getAllExpenses(): MutableList<Expense>

    @Query("SELECT * FROM expense WHERE year = :year AND month = :month")
    suspend fun getExpensesByMonth(year: Int, month: Int): MutableList<Expense>

    @Query("SELECT * FROM expense WHERE year = :year AND month = :month AND day = :day")
    suspend fun getExpensesByDay(year: Int, month: Int, day: Int): MutableList<Expense>

    @Query("SELECT * FROM expense WHERE year = :year AND month = :month AND day = :day AND NOT category IN (:filter)")
    suspend fun getExpensesByDayWithFilter(year: Int, month: Int, day: Int, filter: Set<String>): MutableList<Expense>

    @Query("SELECT * FROM expense WHERE year = :year")
    suspend fun getExpensesByYear(year: Int): MutableList<Expense>

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Delete
    suspend fun deleteExpenses(expenses: List<Expense>)

    @Query("DELETE FROM expense WHERE year = :year AND month = :month")
    suspend fun deleteByMonth(year: Int, month: Int)

    @Query("DELETE FROM expense WHERE year = :year AND month = :month AND day = :day")
    suspend fun deleteByDay(year: Int, month: Int, day: Int)

    @Query("DELETE FROM expense WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM expense WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)

    @Query("SELECT * FROM expense WHERE category = :categoryName")
    suspend fun getExpensesByCategory(categoryName: String): MutableList<Expense>

    @Query("SELECT * FROM expense WHERE year = :year AND month = :month AND day = :day AND category = :categoryName")
    suspend fun getExpensesByDayAndCategory(year: Int, month: Int, day: Int, categoryName: String): MutableList<Expense>

    @Query("SELECT * FROM expense WHERE year = :year AND month = :month AND category = :categoryName")
    suspend fun getExpensesByMonthAndCategory(year: Int, month: Int, categoryName: String): MutableList<Expense>

    @Query("SELECT COUNT() FROM expense WHERE year = :year AND month = :month AND category LIKE :categoryName || '%'")
    suspend fun countExpensesOfCategoryAndSubCategoriesByMonth(year: Int, month: Int, categoryName: String): Int

    @Query("SELECT COUNT() FROM expense WHERE year = :year AND month = :month AND day = :day AND category LIKE :categoryName || '%'")
    suspend fun countExpensesOfCategoryAndSubCategoriesByDay(year: Int, month: Int, day: Int, categoryName: String): Int

    @Query("SELECT COUNT() FROM expense WHERE category LIKE :categoryName || '%'")
    suspend fun countExpensesOfCategoryAndSubCategories(categoryName: String): Int

    @Query("SELECT COUNT() FROM expense WHERE year = :year AND month = :month")
    suspend fun countExpensesInMonth(year: Int, month: Int): Int

    @Query("SELECT COUNT() FROM expense WHERE year = :year AND month = :month AND day = :day")
    suspend fun countExpensesInDay(year: Int, month: Int, day: Int): Int

    @Query("SELECT COUNT() FROM expense")
    suspend fun countExpenses(): Int

    @Query("SELECT COUNT() FROM expense WHERE NOT category IN (:filter)")
    suspend fun countExpensesWithFilter(filter: Set<String>): Int

    @Query("UPDATE expense SET amount = :amount WHERE id = :id")
    suspend fun updateExpenseAmount(id: Int, amount: Amount)

    @Query("DELETE FROM expense WHERE category LIKE :categoryName || '%'")
    suspend fun deleteByCategory(categoryName: String)
}