package com.expenses.mngr.data.data_sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.expenses.mngr.entities.category.Category
import com.expenses.mngr.entities.category.CategoryDBEntity

@Dao
abstract class CategoriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllCategoryDBEntities(entities: List<CategoryDBEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCategoryDBEntity(categoryDBEntity: CategoryDBEntity)

    @Query("SELECT * FROM categoryDBEntity")
    abstract suspend fun getAllCategoryDBEntities(): MutableList<CategoryDBEntity>

    @Query("SELECT EXISTS (SELECT 1 FROM categoryDBEntity WHERE name = :categoryDBEntityName)")
    abstract suspend fun hasCategoryDBEntity(categoryDBEntityName: String): Boolean

    @Query("SELECT * FROM categoryDBEntity WHERE name = :categoryDBEntityName")
    abstract suspend fun getCategoryDBEntityByName(categoryDBEntityName: String): CategoryDBEntity

    @Query("SELECT * FROM categoryDBEntity WHERE ((parentName IS :categoryDBEntityParentName) OR (parentName = :categoryDBEntityParentName))")
    abstract suspend fun getCategoryDBEntityChildren(categoryDBEntityParentName: String?): MutableList<CategoryDBEntity>

    @Query("SELECT * FROM categoryDBEntity WHERE parentName IS NULL")
    abstract suspend fun getRootCategoryDBEntity(): CategoryDBEntity

    @Query("DELETE FROM categoryDBEntity WHERE name LIKE :categoryDBEntityName || '%'")
    abstract suspend fun deleteCategoryAndSubCategories(categoryDBEntityName: String)

    @Query("SELECT color FROM categoryDBEntity")
    abstract suspend fun getColors(): MutableList<Int>

    suspend fun insertAllCategories(categories: List<Category>) {
        insertAllCategoryDBEntities(categoriesToCategoryDBEntities(categories))
    }

    suspend fun insertCategory(category: Category) {
        insertAllCategoryDBEntities(categoryToCategoryDBEntities(category))
    }

    suspend fun hasCategoryDBEntity(categoryDBEntity: CategoryDBEntity) =
        hasCategoryDBEntity(categoryDBEntity.name)

    suspend fun getCategoryDBEntityChildren(categoryDBEntityParent: CategoryDBEntity) =
        getCategoryDBEntityChildren(categoryDBEntityParent.name)

    suspend fun getAllCategories(): MutableList<Category> {
        val categories =
            getCategoryDBEntityChildren("Root").map { Category(it.name, it.name, color = it.color) }
        fillCategories(categories)
        return categories.toMutableList()
    }

    suspend fun hasColor(color: Int) = color in getColors()

    suspend fun getRootCategory(): Category {
        val categoryDBEntity = getRootCategoryDBEntity()
        val category =
            Category(categoryDBEntity.name, categoryDBEntity.name, color = categoryDBEntity.color)
        fillCategories(listOf(category))
        return category
    }

    private fun categoriesToCategoryDBEntities(categories: List<Category>): List<CategoryDBEntity> {
        val result = categories.map(Category::toCategoryDBEntity).toMutableList()
        categories.forEach {
            result.addAll(categoryToCategoryDBEntities(it))
        }
        return result
    }

    private fun categoryToCategoryDBEntities(category: Category): List<CategoryDBEntity> {
        val result = mutableListOf(category.toCategoryDBEntity())
        if (category.hasSubCategories()) {
            category.subCategories.forEach {
                result.addAll(categoryToCategoryDBEntities(it))
            }
        }
        return result
    }

    private suspend fun fillCategories(categories: List<Category>) {
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