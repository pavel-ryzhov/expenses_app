package com.example.expenses.presentation.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.data.repository.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val mainCurrencyLiveData = MutableLiveData<String>()
    val roundingLiveData = MutableLiveData<Int>()
    val fetchExchangeRatesErrorLiveData = settingsRepository.networkErrorLiveData
    val mainCurrencySavedLiveData = settingsRepository.mainCurrencyChangedLiveData
    val stateChangedLiveData = settingsRepository.stateChangedLiveData
    val changingMainCurrencyStartedLiveData = settingsRepository.changingMainCurrencyStartedLiveData
    val changingSecondaryCurrenciesStartedLiveData = settingsRepository.changingSecondaryCurrenciesStartedLiveData
    val secondaryCurrenciesChangedLiveData = settingsRepository.secondaryCurrenciesChangedLiveData

    fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            mainCurrencyLiveData.postValue(appPreferences.getMainCurrency())
            roundingLiveData.postValue(appPreferences.getDoubleRounding())
        }
    }

    fun saveRounding(string: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (string.isNotBlank()) {
                appPreferences.saveDoubleRounding(string.toInt())
            }
        }
    }

    fun notifyFragmentStopped() {
        fetchExchangeRatesErrorLiveData.postValue(null)
        changingMainCurrencyStartedLiveData.postValue(null)
        mainCurrencySavedLiveData.postValue(null)
        changingSecondaryCurrenciesStartedLiveData.postValue(null)
        secondaryCurrenciesChangedLiveData.postValue(null)
        fetchExchangeRatesErrorLiveData.postValue(null)
    }
}