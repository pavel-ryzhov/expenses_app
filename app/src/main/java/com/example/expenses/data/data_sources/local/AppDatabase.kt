package com.example.expenses.data.data_sources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.expenses.data.data_sources.local.dao.CategoriesDao
import com.example.expenses.data.data_sources.local.dao.ExchangeRatesDao
import com.example.expenses.data.data_sources.local.dao.ExpensesDao
import com.example.expenses.data.data_sources.local.dao.SymbolsDao
import com.example.expenses.entities.category.CategoryDBEntity
import com.example.expenses.entities.exchange_rates.ExchangeRate
import com.example.expenses.entities.expense.Expense
import com.example.expenses.entities.symbols.Symbol
import javax.inject.Singleton

@Database(
    entities = [
        Symbol::class,
        ExchangeRate::class,
        Expense::class,
        CategoryDBEntity::class
    ],
    version = 1,
    exportSchema = false
)
@Singleton
abstract class AppDatabase : RoomDatabase() {
    abstract fun getSymbolsDao(): SymbolsDao
    abstract fun getExchangeRatesDao(): ExchangeRatesDao
    abstract fun getExpensesDao(): ExpensesDao
    abstract fun getCategoriesDao(): CategoriesDao
}