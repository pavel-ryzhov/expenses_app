package com.example.expenses.presentation.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenses.data.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {

    val mainCurrencyLiveData = MutableLiveData<String>()
    val roundingLiveData = MutableLiveData<Int>()

    fun fetchData(){
        viewModelScope.launch(Dispatchers.IO) {
            mainCurrencyLiveData.postValue(appPreferences.getMainCurrency().code)
        }
    }
}