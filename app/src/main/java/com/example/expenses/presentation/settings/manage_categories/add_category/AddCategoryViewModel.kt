package com.example.expenses.presentation.settings.manage_categories.add_category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenses.data.data_sources.local.dao.CategoriesDao
import com.example.expenses.entities.category.CategoryDBEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val categoriesDao: CategoriesDao
) : ViewModel() {

    val invalidNameLiveData = MutableLiveData<String>()
    val colorAlreadyExistsLiveData = MutableLiveData<Unit>()
    val categoryAddedSuccessfullyLiveData = MutableLiveData<Unit>()
    private lateinit var parentCategory: CategoryDBEntity

    fun addCategory(name: String, color: Int){
        viewModelScope.launch(Dispatchers.IO) {
            var success = true
            if (name.isBlank()){
                invalidNameLiveData.postValue("Enter category name!")
                success = false
            } else if (name.contains('#')) {
                invalidNameLiveData.postValue("The name cannot contain hash symbols!")
                success = false
            } else if (categoriesDao.hasCategoryDBEntity("${parentCategory.name}#$name")) {
                invalidNameLiveData.postValue("There is already a category with this name!")
                success = false
            }
            if (!success) return@launch
            if (categoriesDao.hasColor(color)){
                colorAlreadyExistsLiveData.postValue(Unit)
                success = false
            }
            if (success){
                addCheckedCategory(parentCategory, name, color)
                categoryAddedSuccessfullyLiveData.postValue(Unit)
            }
        }
    }

    fun setDefaultParent(name: String){
        viewModelScope.launch(Dispatchers.IO){
            parentCategory = categoriesDao.getCategoryDBEntityByName(name)
        }
    }

    private suspend fun addCheckedCategory(parent: CategoryDBEntity, name: String, color: Int){
        categoriesDao.insertCategoryDBEntity(CategoryDBEntity(
            "${parent.name}#$name",
            parent.name,
            color
        ))
    }
}