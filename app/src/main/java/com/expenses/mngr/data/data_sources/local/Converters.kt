package com.expenses.mngr.data.data_sources.local

import androidx.room.TypeConverter
import com.expenses.mngr.entities.expense.Amount

class Converters {
    @TypeConverter
    fun stringToAmount(string: String) = Amount.fromJsonString(string)

    @TypeConverter
    fun amountToString(amount: Amount) = amount.toJsonString()
}