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
    @ColumnInfo(name = "amount") val amount: Double,
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
        amount: Double,
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

    @SuppressLint("SimpleDateFormat")
    fun getDate(): Date =
        SimpleDateFormat("M-d-yyyy H:m").parse("$month-$day-$year $hour:$minute")!!
}