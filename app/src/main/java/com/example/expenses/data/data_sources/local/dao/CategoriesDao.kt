package com.example.expenses.data.data_sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expenses.entities.category.Category
import com.example.expenses.entities.category.CategoryDBEntity

@Dao
abstract class CategoriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAllCategoryDBEntities(entities: List<CategoryDBEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCategoryDBEntity(categoryDBEntity: CategoryDBEntity)

    @Query("SELECT * FROM categoryDBEntity")
    abstract fun getAllCategoryDBEntities(): MutableList<CategoryDBEntity>

    @Query("SELECT EXISTS (SELECT 1 FROM categoryDBEntity WHERE name = :categoryDBEntityName)")
    abstract fun hasCategoryDBEntity(categoryDBEntityName: String): Boolean

    @Query("SELECT * FROM categoryDBEntity WHERE name = :categoryDBEntityName")
    abstract fun getCategoryDBEntityByName(categoryDBEntityName: String): CategoryDBEntity

    @Query("SELECT * FROM categoryDBEntity WHERE ((parentName IS :categoryDBEntityParentName) OR (parentName = :categoryDBEntityParentName))")
    abstract fun getCategoryDBEntityChildren(categoryDBEntityParentName: String?): MutableList<CategoryDBEntity>

    @Query("SELECT * FROM categoryDBEntity WHERE parentName IS NULL")
    abstract fun getRootCategoryDBEntity(): CategoryDBEntity


    fun insertAllCategories(categories: List<Category>){
        insertAllCategoryDBEntities(categoriesToCategoryDBEntities(categories))
    }

    fun insertCategory(category: Category){
        insertAllCategoryDBEntities(categoryToCategoryDBEntities(category))
    }

    fun hasCategoryDBEntity(categoryDBEntity: CategoryDBEntity) =
        hasCategoryDBEntity(categoryDBEntity.name)

    fun getCategoryDBEntityChildren(categoryDBEntityParent: CategoryDBEntity) =
        getCategoryDBEntityChildren(categoryDBEntityParent.name)

    fun getAllCategories(): MutableList<Category> {
        val categories = getCategoryDBEntityChildren("Root").map { Category(it.name, it.name, color = it.color) }
        fillCategories(categories)
        return categories.toMutableList()
    }

    fun getRootCategory(): Category{
        val categoryDBEntity = getRootCategoryDBEntity()
        val category = Category(categoryDBEntity.name, categoryDBEntity.name, color = categoryDBEntity.color)
        fillCategories(listOf(category))
        return category
    }

    private fun categoriesToCategoryDBEntities(categories: List<Category>): List<CategoryDBEntity>{
        val result = categories.map(Category::toCategoryDBEntity).toMutableList()
        categories.forEach {
            result.addAll(categoryToCategoryDBEntities(it))
        }
        return result
    }

    private fun categoryToCategoryDBEntities(category: Category): List<CategoryDBEntity>{
        val result = mutableListOf(category.toCategoryDBEntity())
        if (category.hasSubCategories()){
            category.subCategories.forEach {
                result.addAll(categoryToCategoryDBEntities(it))
            }
        }
        return result
    }

    private fun fillCategories(categories: List<Category>) {
        categories.forEach { category ->
            val subCategories = getCategoryDBEntityChildren(category.fullName).map {
                Category(
                    it.name.substring(it.name.lastIndexOf('#') + 1),
                    it.name,
                    category,
                    color = it.color
                )
            }
            category.subCategories.addAll(subCategories)
            fillCategories(subCategories)
        }
    }
}