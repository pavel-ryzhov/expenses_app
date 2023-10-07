package com.expenses.mngr.data.data_sources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.expenses.mngr.data.data_sources.local.dao.CategoriesDao
import com.expenses.mngr.data.data_sources.local.dao.ExchangeRatesDao
import com.expenses.mngr.data.data_sources.local.dao.ExpensesDao
import com.expenses.mngr.data.data_sources.local.dao.SymbolsDao
import com.expenses.mngr.entities.category.CategoryDBEntity
import com.expenses.mngr.entities.exchange_rates.ExchangeRate
import com.expenses.mngr.entities.expense.Expense
import com.expenses.mngr.entities.symbols.Symbol
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
@TypeConverters(Converters::class)
@Singleton
abstract class AppDatabase : RoomDatabase() {
    abstract fun getSymbolsDao(): SymbolsDao
    abstract fun getExchangeRatesDao(): ExchangeRatesDao
    abstract fun getExpensesDao(): ExpensesDao
    abstract fun getCategoriesDao(): CategoriesDao
}