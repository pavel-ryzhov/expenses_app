package com.expenses.mngr.presentation.choose_main_currency

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expenses.mngr.data.data_sources.local.dao.CategoriesDao
import com.expenses.mngr.data.preferences.AppPreferences
import com.expenses.mngr.domain.symbols.SymbolsRepository
import com.expenses.mngr.entities.category.Category
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
            } catch (e: Exception){
                e.printStackTrace()
                fetchSymbols()
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

    fun saveMainCurrency(code: String) {
        appPreferences.saveMainCurrency(code)
    }

    fun getAppPreferences() = appPreferences
}