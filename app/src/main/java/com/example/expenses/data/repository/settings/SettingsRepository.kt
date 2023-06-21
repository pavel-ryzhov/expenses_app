package com.example.expenses.data.repository.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.expenses.entities.category.Category
import javax.inject.Singleton

@Singleton
abstract class SettingsRepository {

    abstract val mainCurrencyChangedLiveData: MutableLiveData<Unit?>
    abstract val networkErrorLiveData: MutableLiveData<Unit?>
    abstract val stateChangedLiveData: LiveData<String>
    abstract val changingMainCurrencyStartedLiveData: MutableLiveData<Unit?>

    abstract suspend fun changeMainCurrency(currencyTo: String)
}