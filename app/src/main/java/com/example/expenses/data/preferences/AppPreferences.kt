package com.example.expenses.data.preferences

import com.example.expenses.data.data_sources.local.dao.SymbolsDao
import com.example.expenses.entities.symbols.Symbol
import javax.inject.Singleton

@Singleton
interface AppPreferences {
    fun saveMainCurrency(code: String)
    fun getMainCurrency(): String
    fun getMainCurrencySymbol(symbolsDao: SymbolsDao): Symbol//TODO: delete this method
    fun hasMainCurrency(): Boolean

    fun saveShowDialogOnNetworkError(showDialog: Boolean)
    fun getShowDialogOnNetworkError(): Boolean

    fun saveSecondaryCurrencies(currencies: List<String>)
    fun getSecondaryCurrencies(): MutableList<String>
    fun hasSecondaryCurrencies(): Boolean

    fun getCurrencies(): MutableList<String>

    fun saveDefaultCategoriesSaved(value: Boolean)
    fun getDefaultCategoriesSaved(): Boolean

    fun saveDoubleRounding(int: Int)
    fun getDoubleRounding(): Int
}