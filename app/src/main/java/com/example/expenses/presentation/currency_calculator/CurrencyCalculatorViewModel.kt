package com.example.expenses.presentation.currency_calculator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.data.services.currency_converter.CurrenciesConverterService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyCalculatorViewModel @Inject constructor(
    private val currenciesConverterService: CurrenciesConverterService,
    private val appPreferences: AppPreferences
) : ViewModel() {

    val amountLiveData = MutableLiveData<List<String?>>()
    val currenciesLiveData = MutableLiveData<List<String?>>()

    companion object {
        const val EDITTEXT_1 = 1
        const val EDITTEXT_2 = 2
        const val EDITTEXT_3 = 3
        const val EDITTEXT_4 = 4
        const val UNDEFINED = -1
    }

    private var currency1: String? = null
    private var currency2: String? = null
    private var currency3: String? = null
    private var currency4: String? = null

    private var state = UNDEFINED

    fun notifyAmountChanged(amount: String, editText: Int) {
        if (state == UNDEFINED) {
            state = editText
            viewModelScope.launch(Dispatchers.IO) {
                val list = mutableListOf<String?>()
                for (i in 1..4) {
                    list.add(getAmount(amount, editText, i))
                }
                amountLiveData.postValue(list)
            }
        }
    }

    fun notifyCurrencyChanged(currency: String, editText: Int, amount: String = "") {
        if (state == UNDEFINED) {
            state = editText
            viewModelScope.launch(Dispatchers.IO) {
                val list = MutableList<String?>(4) { null }
                list[editText - 1] = getAmount(amount, getCurrencyByEditText(editText), currency)
                setCurrency(currency, editText)
                amountLiveData.postValue(list)
            }
        } else setCurrency(currency, editText)
    }

    private fun setCurrency(currency: String, editText: Int) {
        when (editText) {
            EDITTEXT_1 -> currency1 = currency
            EDITTEXT_2 -> currency2 = currency
            EDITTEXT_3 -> currency3 = currency
            EDITTEXT_4 -> currency4 = currency
        }
    }

    fun notifyDataSet() {
        state = UNDEFINED
    }

    fun fetchCurrencies() {
        currenciesLiveData.postValue(mutableListOf<String?>(appPreferences.getMainCurrency()).apply {
            addAll(appPreferences.getSecondaryCurrencies())
            while (size < 4) add(null)
        })
    }

    private suspend fun getAmount(
        amount: String,
        editTextFrom: Int,
        editTextTo: Int
    ): String? {
        if (editTextFrom == editTextTo) return null
        return getAmount(
            amount,
            getCurrencyByEditText(editTextFrom),
            getCurrencyByEditText(editTextTo)
        )
    }

    private suspend fun getAmount(
        amount: String,
        from: String?,
        to: String?
    ): String? {
        if (from == null || to == null) return null
        if (from == to) return amount
        if (amount.isBlank()) return ""
        return currenciesConverterService.convertCurrencyByLatest(amount.toDouble(), from, to).toString()
    }

    private fun getCurrencyByEditText(editText: Int) = when (editText) {
        EDITTEXT_1 -> currency1
        EDITTEXT_2 -> currency2
        EDITTEXT_3 -> currency3
        EDITTEXT_4 -> currency4
        else -> null
    }
}