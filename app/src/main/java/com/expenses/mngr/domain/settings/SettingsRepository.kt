package com.expenses.mngr.domain.settings

import androidx.lifecycle.MutableLiveData
import javax.inject.Singleton

@Singleton
abstract class SettingsRepository {

    abstract val mainCurrencyChangedLiveData: MutableLiveData<Unit?>
    abstract val secondaryCurrenciesChangedLiveData: MutableLiveData<Unit?>
    abstract val networkErrorLiveData: MutableLiveData<Unit?>
    abstract val stateChangedLiveData: MutableLiveData<String>
    abstract val changingMainCurrencyStartedLiveData: MutableLiveData<Unit?>
    abstract val changingSecondaryCurrenciesStartedLiveData: MutableLiveData<Unit?>

    abstract suspend fun changeMainCurrency(currencyTo: String)
    abstract suspend fun changeSecondaryCurrencies(currenciesTo: List<String>)
}