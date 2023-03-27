package com.example.expenses.presentation.dialogs.amount_in_secondary_currencies

import androidx.lifecycle.MutableLiveData
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.data.services.currency_converter.CurrenciesConverterService
import com.example.expenses.extensions.roundAndFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataWrapper(
    private val currenciesConverterService: CurrenciesConverterService,
    private val appPreferences: AppPreferences,
    private val viewModelScope: CoroutineScope
) {

    val valuesLiveData = MutableLiveData<Map<String, String>>()

    fun provideValues(amount: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val rounding = appPreferences.getDoubleRounding()
            val map = mutableMapOf(appPreferences.getMainCurrency().code to amount.roundAndFormat(rounding))
            for (currency in appPreferences.getSecondaryCurrencies()){
                map[currency.code] = currenciesConverterService.fromMainCurrency(amount, currency.code).roundAndFormat(rounding)
            }
            valuesLiveData.postValue(map)
        }
    }
}