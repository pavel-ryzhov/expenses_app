package com.expenses.mngr.presentation.settings.manage_categories.choose_category_dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expenses.mngr.data.data_sources.local.dao.CategoriesDao
import com.expenses.mngr.entities.category.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseCategoryViewModel @Inject constructor(
    private val categoriesDao: CategoriesDao
) : ViewModel() {

    val chooseCategoriesDialogLiveData = MutableLiveData<MutableList<Category>>()
    val provideCategoriesLiveData = MutableLiveData<MutableList<Category>>()

    private lateinit var rootCategory: Category
    private lateinit var currentCategory: Category

    fun provideCategories(category: Category? = null) {
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
            provideCategoriesLiveData.postValue(if (rootCategory == currentCategory) mutableListOf() else currentCategory.parent!!.subCategories)
            if (currentCategory != rootCategory){
                currentCategory = currentCategory.parent!!
            }
        }
    }
}