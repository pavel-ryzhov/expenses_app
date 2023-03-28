package com.example.expenses.presentation.dialogs.amount_in_secondary_currencies

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.data.services.currency_converter.CurrenciesConverterService
import com.example.expenses.extensions.roundAndFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AmountInSecondaryCurrenciesViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val currenciesConverterService: CurrenciesConverterService
) : ViewModel() {

    val valuesLiveData = MutableLiveData<Map<String, String>>()

    fun fetchValues(amount: Double) {
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