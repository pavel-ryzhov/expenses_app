package com.example.expenses.entities.category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expenses.extensions.randomColor
import kotlin.random.Random

@Entity
data class CategoryDBEntity(
    @PrimaryKey
    @ColumnInfo val name: String,
    @ColumnInfo val parentName: String?,
    @ColumnInfo val color: Int = Random.randomColor()
){
    fun getFriendlyName() = name.removePrefix("Root#").replace("#", " → ")
}