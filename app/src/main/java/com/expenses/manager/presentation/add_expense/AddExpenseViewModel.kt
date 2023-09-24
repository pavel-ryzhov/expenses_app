package com.expenses.manager.presentation.add_expense

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expenses.manager.data.data_sources.local.dao.ExpensesDao
import com.expenses.manager.data.preferences.AppPreferences
import com.expenses.manager.data.services.currency_converter.CurrenciesConverterService
import com.expenses.manager.entities.category.Category
import com.expenses.manager.entities.expense.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.util.*
import java.util.Calendar.*
import javax.inject.Inject


@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val expensesDao: ExpensesDao,
    private val currenciesConverterService: CurrenciesConverterService
) : ViewModel() {
    val currenciesLiveData = MutableLiveData<MutableList<String>>()

    val expenseAddedSuccessfullyLiveData = MutableLiveData<Unit>()
    val amountFieldIsEmptyLiveData = MutableLiveData<Unit>()
    val amountIsZeroLiveData = MutableLiveData<Unit>()
    val categoryFieldIsEmptyLiveData = MutableLiveData<Unit>()
    val networkErrorLiveData = MutableLiveData<Unit?>()
    val addingExpenseStartedLiveData = MutableLiveData<Unit?>()

    fun fetchCurrencies() {
        currenciesLiveData.postValue(
            appPreferences.getSecondaryCurrencies().apply { add(appPreferences.getMainCurrency()) })
    }

    fun addExpense(
        calendar: Calendar,
        amountString: String,
        currency: String,
        category: Category?,
        description: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            var success = true
            if (amountString.isBlank()) {
                amountFieldIsEmptyLiveData.postValue(Unit)
                success = false
            } else if (amountString.toDouble() == 0.0) {
                amountIsZeroLiveData.postValue(Unit)
                success = false
            }
            if (category == null) {
                categoryFieldIsEmptyLiveData.postValue(Unit)
                success = false
            }
            if (success) {
                expensesDao.insertExpense(Expense(
                    category = category!!.fullName,
                    amount = if (Expense.checkUseLatestRates(calendar)) {
                        currenciesConverterService.amountFromCurrencyByLatest(amountString.toDouble(), currency)
                    } else {
                        addingExpenseStartedLiveData.postValue(Unit)
                        try {
                            currenciesConverterService.amountFromCurrency(
                                amountString.toDouble(),
                                currency,
                                calendar.get(YEAR),
                                calendar.get(MONTH),
                                calendar.get(DAY_OF_MONTH)
                            )
                        } catch (e: UnknownHostException){
                            networkErrorLiveData.postValue(Unit)
                            return@launch
                        }
                    },
                    description = description,
                    date = calendar
                ))
                expenseAddedSuccessfullyLiveData.postValue(Unit)
            }
        }
    }

    fun notifyFragmentStopped(){
        networkErrorLiveData.postValue(null)
        addingExpenseStartedLiveData.postValue(null)
    }
}