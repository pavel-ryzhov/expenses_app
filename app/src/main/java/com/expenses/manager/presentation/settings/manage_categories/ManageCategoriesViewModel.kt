package com.expenses.manager.presentation.settings.manage_categories

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expenses.manager.data.data_sources.local.dao.CategoriesDao
import com.expenses.manager.data.data_sources.local.dao.ExpensesDao
import com.expenses.manager.entities.category.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageCategoriesViewModel @Inject constructor(
    private val categoriesDao: CategoriesDao,
    private val expensesDao: ExpensesDao
) : ViewModel() {
    val provideCategoriesLiveData = MutableLiveData<MutableList<Category>>()
    val categoriesLiveData = MutableLiveData<MutableList<Category>>()
    val checkCategoryHasSubCategoriesAndExpensesLiveData = MutableLiveData<Triple<Category, Boolean, Boolean>>()
    val categoryDeletedLiveData = MutableLiveData<Unit>()

    private lateinit var rootCategory: Category
    private lateinit var currentCategory: Category

    fun provideCategories(category: Category? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            if (category == null) {
                rootCategory = categoriesDao.getRootCategory()
                currentCategory = rootCategory
                categoriesLiveData.postValue(rootCategory.subCategories)
            } else {
                currentCategory = category
                provideCategoriesLiveData.postValue(category.subCategories)
            }
        }
    }

    fun provideParentCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            provideCategoriesLiveData.postValue(if (rootCategory == currentCategory) mutableListOf() else currentCategory.parent!!.subCategories)
            if (currentCategory != rootCategory){
                currentCategory = currentCategory.parent!!
            }
        }
    }

    fun deleteCategory(category: Category){
        viewModelScope.launch(Dispatchers.IO) {
            currentCategory.subCategories.remove(category)
            expensesDao.deleteByCategory(category.fullName)
            categoriesDao.deleteCategoryAndSubCategories(category.fullName)
            categoryDeletedLiveData.postValue(Unit)
        }
    }

    fun checkCategoryHasSubcategoriesAndExpenses(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            checkCategoryHasSubCategoriesAndExpensesLiveData.postValue(Triple(category, category.hasSubCategories(), expensesDao.countExpensesOfCategoryAndSubCategories(category.fullName) > 0))
        }
    }
}