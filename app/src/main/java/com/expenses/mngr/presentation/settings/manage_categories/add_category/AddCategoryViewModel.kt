package com.expenses.mngr.presentation.settings.manage_categories.add_category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.expenses.mngr.R
import com.expenses.mngr.data.data_sources.local.dao.CategoriesDao
import com.expenses.mngr.entities.category.CategoryDBEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    application: Application,
    private val categoriesDao: CategoriesDao
) : AndroidViewModel(application) {

    val invalidNameLiveData = MutableLiveData<String>()
    val colorAlreadyExistsLiveData = MutableLiveData<Unit>()
    val categoryAddedSuccessfullyLiveData = MutableLiveData<Unit>()
    private lateinit var parentCategory: CategoryDBEntity

    fun addCategory(name: String, color: Int){
        viewModelScope.launch(Dispatchers.IO) {
            var success = true
            if (name.isBlank()){
                invalidNameLiveData.postValue(getApplication<Application>().getString(R.string.enter_category_name))
                success = false
            } else if (name.contains('#')) {
                invalidNameLiveData.postValue(getApplication<Application>().getString(R.string.the_name_cannot_contain_hash_symbols))
                success = false
            } else if (categoriesDao.hasCategoryDBEntity("${parentCategory.name}#$name")) {
                invalidNameLiveData.postValue(getApplication<Application>().getString(R.string.there_is_already_a_category_with_this_name))
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