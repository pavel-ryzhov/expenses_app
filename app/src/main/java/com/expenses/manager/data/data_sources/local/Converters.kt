package com.expenses.manager.data.data_sources.local

import androidx.room.TypeConverter
import com.expenses.manager.entities.expense.Amount

class Converters {
    @TypeConverter
    fun stringToAmount(string: String) = Amount.fromJsonString(string)

    @TypeConverter
    fun amountToString(amount: Amount) = amount.toJsonString()
}