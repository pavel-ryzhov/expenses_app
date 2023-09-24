package com.expenses.manager.entities.category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.expenses.manager.extensions.randomColor
import kotlin.random.Random

@Entity
data class CategoryDBEntity(
    @PrimaryKey
    @ColumnInfo val name: String,
    @ColumnInfo val parentName: String? = null,
    @ColumnInfo val color: Int = Random.randomColor()
){
    fun getFriendlyName() = getFriendlyName(name)
    fun getShortName() = Companion.getShortName(name)

    companion object{
        val ROOT = CategoryDBEntity("Root")
        fun getFriendlyName(name: String) = name.removePrefix("Root#").replace("#", " → ")
        fun getShortName(name: String) = name.substring(name.lastIndexOf('#') + 1)
    }

    override fun equals(other: Any?): Boolean {
        return other != null && other is CategoryDBEntity && this.name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}