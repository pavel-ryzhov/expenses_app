package com.expenses.mngr.entities.expense

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.expenses.mngr.entities.expense.Amount.Companion.checkAmountIsNull
import com.expenses.mngr.entities.expense.Amount.Companion.sumOfAmounts
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Date

@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "amount") val amount: Amount,
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
        amount: Amount,
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
        amount: Amount?,
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

    fun useLatestRates() = checkUseLatestRates(Calendar.getInstance().apply { time = getDate() })

    @SuppressLint("SimpleDateFormat")
    fun getDate(): Date =
        SimpleDateFormat("M-d-yyyy H:m").parse("$month-$day-$year $hour:$minute")!!

    companion object{
        fun sumOfExpenses(expenses: List<Expense>) = sumOfAmounts(expenses.map { it.amount })
        fun sumOfExpenses(expenses: List<Expense>, currencies: List<String>) = sumOfAmounts(expenses.map { it.amount }, currencies)
        fun checkUseLatestRates(calendar: Calendar): Boolean {
            val currentCalendar = GregorianCalendar()
            return if (currentCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                && currentCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && currentCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
            ) true else calendar.after(currentCalendar)
        }
    }
}