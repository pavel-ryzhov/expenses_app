package com.example.expenses.presentation.add_expense

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenses.data.data_sources.local.dao.CategoriesDao
import com.example.expenses.data.data_sources.local.dao.ExpensesDao
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.data.services.currency_converter.CurrenciesConverterService
import com.example.expenses.entities.category.Category
import com.example.expenses.entities.expense.Expense
import com.example.expenses.entities.symbols.Symbol
import com.example.expenses.presentation.dialogs.choose_category_dialog.CategoriesDaoWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val categoriesDao: CategoriesDao,
    private val expensesDao: ExpensesDao,
    private val currenciesConverterService: CurrenciesConverterService
) : ViewModel() {
    val currenciesLiveData = MutableLiveData<MutableList<Symbol>>()

    val expenseAddedSuccessfullyLiveData = MutableLiveData<Unit>()
    val amountFieldIsEmptyLiveData = MutableLiveData<Unit>()
    val categoryFieldIsEmptyLiveData = MutableLiveData<Unit>()

    fun fetchCurrencies() {
        currenciesLiveData.postValue(
            appPreferences.getSecondaryCurrencies().apply { add(appPreferences.getMainCurrency()) })
    }

    fun addExpense(
        date: Calendar,
        amount: String,
        currency: String,
        category: Category?,
        description: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            var success = true
            if (amount.isBlank()) {
                amountFieldIsEmptyLiveData.postValue(Unit)
                success = false
            }
            if (category == null) {
                categoryFieldIsEmptyLiveData.postValue(Unit)
                success = false
            }
            if (success) {
                expensesDao.insertExpense(
                    Expense(
                        category = category!!.fullName,
                        amount = currenciesConverterService.toMainCurrency(amount.toDouble(), currency),
                        description = description,
                        date = date
                    )
                )
                expenseAddedSuccessfullyLiveData.postValue(Unit)
            }
        }
    }

    fun getCategoriesWrapper(): CategoriesDaoWrapper {
        return CategoriesDaoWrapper(categoriesDao, this)
    }
}