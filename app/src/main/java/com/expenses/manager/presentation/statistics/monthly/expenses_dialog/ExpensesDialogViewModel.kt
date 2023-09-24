package com.expenses.manager.presentation.statistics.monthly.expenses_dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expenses.manager.data.data_sources.local.dao.ExpensesDao
import com.expenses.manager.entities.expense.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesDialogViewModel @Inject constructor(
    private val expensesDao: ExpensesDao
) : ViewModel() {
    val expensesLiveData = MutableLiveData<List<Expense>>()

    fun fetchExpenses(year: Int, month: Int, categoryName: String){
        viewModelScope.launch(Dispatchers.IO){
            expensesLiveData.postValue(expensesDao.getExpensesByMonthAndCategory(year, month, categoryName))
        }
    }
}