package com.example.expenses.data.preferences

import android.content.Context
import com.example.expenses.entities.symbols.Symbol
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreferencesImpl @Inject constructor(@ApplicationContext context: Context) :
    AppPreferences {

    //    companion object {
//        private const val MAIN_CURRENCY_CODE_TAG = "MAIN_CURRENCY_CODE_TAG"
//        private const val MAIN_CURRENCY_DESCRIPTION_TAG = "MAIN_CURRENCY_DESCRIPTION_TAG"
//        private const val SHOW_DIALOG_ON_NETWORK_ERROR_TAG = "SHOW_DIALOG_ON_NETWORK_ERROR_TAG"
//        private const val SECONDARY_CURRENCIES_CODES_TAG = "SECONDARY_CURRENCIES_CODES_TAG"
//        private const val SECONDARY_CURRENCIES_DESCRIPTIONS_TAG = "SECONDARY_CURRENCIES_DESCRIPTIONS_TAG"
//        private const val DEFAULT_CATEGORIES_SAVED_TAG = "DEFAULT_CATEGORIES_SAVED_TAG"
//    }
    private enum class Tag {
        MAIN_CURRENCY_CODE_TAG,
        MAIN_CURRENCY_DESCRIPTION_TAG,
        SHOW_DIALOG_ON_NETWORK_ERROR_TAG,
        SECONDARY_CURRENCIES_CODES_TAG,
        SECONDARY_CURRENCIES_DESCRIPTIONS_TAG,
        DEFAULT_CATEGORIES_SAVED_TAG,
        DOUBLE_ROUNDING_TAG
    }

    private val sharedPreferences =
        context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    override fun saveMainCurrency(symbol: Symbol) {
        sharedPreferences.edit()
            .putString(Tag.MAIN_CURRENCY_CODE_TAG.name, symbol.code)
            .putString(Tag.MAIN_CURRENCY_DESCRIPTION_TAG.name, symbol.description)
            .apply()
    }

    override fun getMainCurrency(): Symbol {
        sharedPreferences.apply {
            return Symbol(
                getString(Tag.MAIN_CURRENCY_CODE_TAG.name, "null")!!,
                getString(Tag.MAIN_CURRENCY_DESCRIPTION_TAG.name, "null")!!
            )
        }
    }

    override fun hasMainCurrency() =
        sharedPreferences.getString(Tag.MAIN_CURRENCY_DESCRIPTION_TAG.name, null) != null

    override fun saveShowDialogOnNetworkError(showDialog: Boolean) {
        sharedPreferences.edit().putBoolean(Tag.SHOW_DIALOG_ON_NETWORK_ERROR_TAG.name, showDialog)
            .apply()
    }

    override fun getShowDialogOnNetworkError() = sharedPreferences.getBoolean(
        Tag.SHOW_DIALOG_ON_NETWORK_ERROR_TAG.name, true
    )


    override fun saveSecondaryCurrencies(currencies: MutableList<Symbol>) {
        sharedPreferences.edit()
            .putStringSet(
                Tag.SECONDARY_CURRENCIES_CODES_TAG.name,
                currencies.map { it.code }.toSet()
            )
            .putStringSet(
                Tag.SECONDARY_CURRENCIES_DESCRIPTIONS_TAG.name,
                currencies.map { it.description }.toSet()
            )
            .apply()
    }

    override fun getSecondaryCurrencies(): MutableList<Symbol> {
        val codes =
            sharedPreferences.getStringSet(Tag.SECONDARY_CURRENCIES_CODES_TAG.name, setOf())!!
                .toList()
        val descriptions =
            sharedPreferences.getStringSet(
                Tag.SECONDARY_CURRENCIES_DESCRIPTIONS_TAG.name,
                setOf()
            )!!
                .toList()
        val result = mutableListOf<Symbol>()
        for (i in codes.indices) {
            result.add(Symbol(codes[i], descriptions[i]))
        }
        return result
    }

    override fun hasSecondaryCurrencies() =
        sharedPreferences.getStringSet(Tag.SECONDARY_CURRENCIES_CODES_TAG.name, null) != null

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