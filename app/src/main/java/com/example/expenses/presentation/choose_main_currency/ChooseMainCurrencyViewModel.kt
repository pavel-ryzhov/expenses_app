package com.example.expenses.presentation.choose_main_currency

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenses.data.data_sources.local.dao.CategoriesDao
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.data.repository.symbols.SymbolsRepository
import com.example.expenses.entities.category.Category
import com.example.expenses.entities.symbols.Symbol
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class ChooseMainCurrencyViewModel @Inject constructor(
    private val symbolsRepository: SymbolsRepository,
    private val appPreferences: AppPreferences,
    private val categoriesDao: CategoriesDao
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

    fun saveDefaultCategories(){
        viewModelScope.launch(Dispatchers.IO) {
            if (!appPreferences.getDefaultCategoriesSaved()){
                categoriesDao.insertCategory(Category.getDefaultRootCategory())
                appPreferences.saveDefaultCategoriesSaved(true)
            }
        }
    }

    fun saveMainCurrency(symbol: Symbol) {
        appPreferences.saveMainCurrency(symbol)
    }

    fun getAppPreferences() = appPreferences
}