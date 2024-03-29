package com.expenses.mngr.data.preferences

import javax.inject.Singleton

@Singleton
interface AppPreferences {
    fun saveMainCurrency(code: String)
    fun getMainCurrency(): String
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

    fun saveExchangeRatesFetchedToday()
    fun getExchangeRatesFetchedToday(): Boolean
}