package com.expenses.manager.presentation.statistics.total.all_expenses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expenses.manager.domain.statistics.StatisticsRepository
import com.expenses.manager.entities.expense.Expense
import com.expenses.manager.entities.sorting.Sorting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllExpensesViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {

    val expensesLiveData = MutableLiveData<MutableList<Expense>>()

    fun fetchExpenses(descriptionFilter: String, sorting: Sorting?){
        viewModelScope.launch(Dispatchers.IO) {
            expensesLiveData.postValue(statisticsRepository.fetchExpenses(descriptionFilter, sorting))
        }
    }
}