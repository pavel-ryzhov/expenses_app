package com.example.expenses.data.preferences

import com.example.expenses.entities.symbols.Symbol

interface AppPreferences {
    fun saveMainCurrency(symbol: Symbol)
    fun getMainCurrency(): Symbol
    fun hasMainCurrency(): Boolean

    fun saveShowDialogOnNetworkError(showDialog: Boolean)
    fun getShowDialogOnNetworkError(): Boolean

    fun saveSecondaryCurrencies(currencies: MutableList<Symbol>)
    fun getSecondaryCurrencies(): MutableList<Symbol>
    fun hasSecondaryCurrencies(): Boolean

    fun saveDefaultCategoriesSaved(value: Boolean)
    fun getDefaultCategoriesSaved(): Boolean

    fun saveDoubleRounding(int: Int)
    fun getDoubleRounding(): Int
}