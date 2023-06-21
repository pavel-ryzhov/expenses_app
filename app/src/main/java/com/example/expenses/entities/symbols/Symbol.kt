package com.example.expenses.entities.symbols

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Symbol(
    @PrimaryKey
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "description") val description: String
) {
    override fun equals(other: Any?): Boolean {
        return code == (other as? Symbol)?.code
    }
}