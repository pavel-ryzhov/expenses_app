package com.expenses.manager.data.preferences

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesImpl @Inject constructor(
    @ApplicationContext context: Context,
) : AppPreferences {

    private lateinit var MAIN_CURRENCY: String

    private enum class Tag {
        MAIN_CURRENCY_CODE_TAG,
        SHOW_DIALOG_ON_NETWORK_ERROR_TAG,
        SECONDARY_CURRENCIES_CODES_TAG,
        DEFAULT_CATEGORIES_SAVED_TAG,
        DOUBLE_ROUNDING_TAG
    }

    private val sharedPreferences =
        context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    override fun saveMainCurrency(code: String) {
        sharedPreferences.edit()
            .putString(Tag.MAIN_CURRENCY_CODE_TAG.name, code)
            .apply()
        MAIN_CURRENCY = code
    }

    override fun getMainCurrency(): String {
        if (!::MAIN_CURRENCY.isInitialized)
            MAIN_CURRENCY = sharedPreferences.getString(Tag.MAIN_CURRENCY_CODE_TAG.name, "null")!!
        return MAIN_CURRENCY
    }
    
    override fun hasMainCurrency() =
        sharedPreferences.getString(Tag.MAIN_CURRENCY_CODE_TAG.name, null) != null

    override fun getCurrencies(): MutableList<String> {
        return getSecondaryCurrencies().apply { add(getMainCurrency()) }
    }

    override fun saveShowDialogOnNetworkError(showDialog: Boolean) {
        sharedPreferences.edit().putBoolean(Tag.SHOW_DIALOG_ON_NETWORK_ERROR_TAG.name, showDialog)
            .apply()
    }

    override fun getShowDialogOnNetworkError() = sharedPreferences.getBoolean(
        Tag.SHOW_DIALOG_ON_NETWORK_ERROR_TAG.name, true
    )

    override fun hasSecondaryCurrencies() =
        sharedPreferences.getStringSet(Tag.SECONDARY_CURRENCIES_CODES_TAG.name, null) != null

    override fun saveSecondaryCurrencies(currencies: List<String>) {
        sharedPreferences.edit().putStringSet(Tag.SECONDARY_CURRENCIES_CODES_TAG.name, currencies.toSet()).apply()
    }

    override fun getSecondaryCurrencies() =
        sharedPreferences.getStringSet(Tag.SECONDARY_CURRENCIES_CODES_TAG.name, setOf<String>())!!.toMutableList()

    override fun saveDefaultCategoriesSaved(value: Boolean) {
        sharedPreferences.edit().putBoolean(Tag.DEFAULT_CATEGORIES_SAVED_TAG.name, value).apply()
    }

    override fun getDefaultCategoriesSaved() =
        sharedPreferences.getBoolean(Tag.DEFAULT_CATEGORIES_SAVED_TAG.name, false)

    override fun saveDoubleRounding(int: Int) {
        sharedPreferences.edit().putInt(Tag.DOUBLE_ROUNDING_TAG.name, int).apply()
    }

    override fun getDoubleRounding() = sharedPreferences.getInt(Tag.DOUBLE_ROUNDING_TAG.name, 2)
}