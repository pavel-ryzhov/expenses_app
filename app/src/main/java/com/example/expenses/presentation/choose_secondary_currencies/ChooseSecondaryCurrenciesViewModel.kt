package com.example.expenses.presentation.choose_secondary_currencies

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenses.data.data_sources.local.dao.SymbolsDao
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.data.repository.symbols.SymbolsRepository
import com.example.expenses.entities.symbols.Symbol
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseSecondaryCurrenciesViewModel @Inject constructor(
    symbolsRepository: SymbolsRepository,
    private val appPreferences: AppPreferences,
    private val symbolsDao: SymbolsDao
) :
    ViewModel() {

    private val symbolsLiveData = symbolsRepository.getSymbolsLiveData()
    val symbolsLiveDataMediator = MediatorLiveData<MutableList<Symbol>>().apply {
        addSource(symbolsLiveData){
            postValue(it.apply {
                remove(Symbol(appPreferences.getMainCurrency(), ""))
            })
        }
    }
    val savedSecondaryCurrenciesLiveData = MutableLiveData<MutableList<Symbol>>()

    fun saveSecondaryCurrencies(currencies: MutableList<Symbol>) {
        appPreferences.saveSecondaryCurrencies(currencies.map { it.code })
    }

    fun fetchSavedSecondaryCurrencies() {
        viewModelScope.launch(Dispatchers.IO){
            savedSecondaryCurrenciesLiveData.postValue(symbolsDao.getSymbolsByCodes(appPreferences.getSecondaryCurrencies()))
        }
    }
}