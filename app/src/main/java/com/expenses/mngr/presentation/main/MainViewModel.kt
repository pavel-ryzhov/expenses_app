package com.expenses.mngr.presentation.main

import androidx.lifecycle.ViewModel
import com.expenses.mngr.data.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val appPreferences: AppPreferences) : ViewModel() {
    fun isMainCurrencySaved() = appPreferences.hasMainCurrency()
    fun areSecondaryCurrenciesSaved() = appPreferences.hasSecondaryCurrencies()
}