package com.expenses.mngr.presentation.settings.choose_main_currency_dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expenses.mngr.domain.symbols.SymbolsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class ChooseMainCurrencyViewModel @Inject constructor(
    private val symbolsRepository: SymbolsRepository,
) : ViewModel() {

    val symbolsLiveData = symbolsRepository.getSymbolsLiveData()
    val fetchSymbolsErrorLiveData = MutableLiveData<Unit>()

    fun fetchSymbols() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                symbolsRepository.fetchSymbols()
            } catch (e: UnknownHostException) {
                fetchSymbolsErrorLiveData.postValue(Unit)
                e.printStackTrace()
            }
        }
    }
}