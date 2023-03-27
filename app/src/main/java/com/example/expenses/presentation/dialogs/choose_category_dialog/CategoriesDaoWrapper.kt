package com.example.expenses.presentation.dialogs.choose_category_dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenses.data.data_sources.local.dao.CategoriesDao
import com.example.expenses.entities.category.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoriesDaoWrapper(private val categoriesDao: CategoriesDao, private val viewModel: ViewModel) {

    val chooseCategoriesDialogLiveData = MutableLiveData<MutableList<Category>>()
    val provideCategoriesLiveData = MutableLiveData<MutableList<Category>>()

    private lateinit var rootCategory: Category
    private lateinit var currentCategory: Category

    fun provideCategories(category: Category? = null) {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            if (category == null) {
                rootCategory = categoriesDao.getRootCategory()
                currentCategory = rootCategory
                chooseCategoriesDialogLiveData.postValue(rootCategory.subCategories)
            } else {
                currentCategory = category
                provideCategoriesLiveData.postValue(category.subCategories)
            }
        }
    }

    fun provideParentCategories() {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            provideCategoriesLiveData.postValue(if (rootCategory == currentCategory) mutableListOf() else currentCategory.parent!!.subCategories)
            if (currentCategory != rootCategory){
                currentCategory = currentCategory.parent!!
            }
        }
    }

}