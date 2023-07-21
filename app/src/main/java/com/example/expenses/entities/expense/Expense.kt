package com.example.expenses.entities.expense

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "amount") val amount: MutableMap<String, Double>,
    @ColumnInfo(name = "description") val description: String,

    @ColumnInfo(name = "month") val month: Int,
    @ColumnInfo(name = "day") val day: Int,
    @ColumnInfo(name = "year") val year: Int,
    @ColumnInfo(name = "hour") val hour: Int,
    @ColumnInfo(name = "minute") val minute: Int
) {

    constructor(
        id: Int = 0,
        category: String,
        amount: MutableMap<String, Double>,
        description: String,
        date: Calendar
    ) : this(
        id,
        category,
        amount,
        description,
        date.get(Calendar.MONTH),
        date.get(Calendar.DAY_OF_MONTH),
        date.get(Calendar.YEAR),
        date.get(Calendar.HOUR_OF_DAY),
        date.get(Calendar.MINUTE)
    )

    constructor(
        id: Int = 0,
        category: String,
        amount: MutableMap<String, Double>?,
        currencies: List<String>,
        description: String,
        date: Calendar
    ) : this(
        id,
        category,
        checkAmountIsNull(amount, currencies),
        description,
        date.get(Calendar.MONTH),
        date.get(Calendar.DAY_OF_MONTH),
        date.get(Calendar.YEAR),
        date.get(Calendar.HOUR_OF_DAY),
        date.get(Calendar.MINUTE)
    )

    @SuppressLint("SimpleDateFormat")
    fun getDate(): Date =
        SimpleDateFormat("M-d-yyyy H:m").parse("$month-$day-$year $hour:$minute")!!

    companion object{
        fun sumOfExpenses(expenses: List<Expense>): MutableMap<String, Double>? {
            val result = mutableMapOf<String, Double>()
            val currencies = expenses.firstOrNull()?.amount?.keys ?: return null
            val amounts = DoubleArray(currencies.size)
            for (expense in expenses){
                currencies.forEachIndexed { index, currency ->
                    amounts[index] += expense.amount[currency]!!
                }
            }
            currencies.forEachIndexed { index, currency ->
                result[currency] = amounts[index]
            }
            return result
        }

        private fun checkAmountIsNull(amount: MutableMap<String, Double>? = null, currencies: List<String>): MutableMap<String, Double>{
            return amount ?: let {
                val result = mutableMapOf<String, Double>()
                currencies.forEach { currency ->
                    result[currency] = 0.0
                }
                result
            }
        }
    }
}