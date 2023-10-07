package com.expenses.mngr.presentation.choose_secondary_currencies

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.*
import com.expenses.mngr.data.data_sources.local.dao.SymbolsDao
import com.expenses.mngr.data.preferences.AppPreferences
import com.expenses.mngr.domain.symbols.SymbolsRepository
import com.expenses.mngr.entities.symbols.Symbol
import com.expenses.mngr.presentation.settings.ChangeMainCurrencyForegroundService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class ChooseSecondaryCurrenciesViewModel @Inject constructor(
    application: Application,
    symbolsRepository: SymbolsRepository,
    private val appPreferences: AppPreferences,
    private val symbolsDao: SymbolsDao
) : AndroidViewModel(application) {

    private val symbolsLiveData = symbolsRepository.getSymbolsLiveData()
    val symbolsLiveDataMediator = MediatorLiveData<MutableList<Symbol>>().apply {
        addSource(symbolsLiveData){
            postValue(it.apply {
                remove(Symbol(appPreferences.getMainCurrency(), ""))
            })
        }
    }
    val savedSecondaryCurrenciesLiveData = MutableLiveData<MutableList<Symbol>>()
    val secondaryCurrenciesSavedLiveData = MutableLiveData<Boolean?>()
    val noSelectedSecondaryCurrenciesLiveData = MutableLiveData<Unit?>()
    val nothingChangedLiveData = MutableLiveData<Unit?>()

    fun saveSecondaryCurrencies(currencies: MutableList<Symbol>, openedFromSettings: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        if (currencies.size > 0) {
            if (currencies.map{ it.code }.toSet() == appPreferences.getSecondaryCurrencies().toSet()){
                nothingChangedLiveData.postValue(Unit)
                return@launch
            }
            if (openedFromSettings) {
                val intent = Intent(
                    getApplication(),
                    ChangeMainCurrencyForegroundService::class.java
                ).apply {
                    putStringArrayListExtra(
                        ChangeMainCurrencyForegroundService.CURRENCIES_TO_TAG,
                        currencies.map { it.code } as ArrayList<String>)
                    action =
                        ChangeMainCurrencyForegroundService.ACTION_CHANGE_SECONDARY_CURRENCIES
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getApplication<Application>().startForegroundService(intent)
                } else {
                    getApplication<Application>().startService(intent)
                }
            } else {
                appPreferences.saveSecondaryCurrencies(currencies.map { it.code })
            }
            secondaryCurrenciesSavedLiveData.postValue(openedFromSettings)
        } else {
            noSelectedSecondaryCurrenciesLiveData.postValue(Unit)
        }
    }

    fun fetchSavedSecondaryCurrencies() {
        viewModelScope.launch(Dispatchers.IO){
            savedSecondaryCurrenciesLiveData.postValue(symbolsDao.getSymbolsByCodes(appPreferences.getSecondaryCurrencies()))
        }
    }

    fun notifyFragmentStopped() {
        secondaryCurrenciesSavedLiveData.postValue(null)
        noSelectedSecondaryCurrenciesLiveData.postValue(null)
        nothingChangedLiveData.postValue(null)
    }
}