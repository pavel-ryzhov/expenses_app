package com.example.expenses.presentation.choose_secondary_currencies

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.data.repository.symbols.SymbolsRepository
import com.example.expenses.entities.symbols.Symbol
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChooseSecondaryCurrenciesViewModel @Inject constructor(
    private val symbolsRepository: SymbolsRepository,
    private val appPreferences: AppPreferences
) :
    ViewModel() {
    private val symbolsLiveData = symbolsRepository.getSymbolsLiveData()
    val symbolsLiveDataMediator = MediatorLiveData<MutableList<Symbol>>().apply {
        addSource(symbolsLiveData){
            postValue(it.apply {
                remove(appPreferences.getMainCurrency())
            })
        }
    }

    fun saveSecondaryCurrencies(currencies: MutableList<Symbol>) {
        appPreferences.saveSecondaryCurrencies(currencies)
    }
}