package com.example.expenses.entities.sorting

import com.example.expenses.entities.expense.Expense
import java.util.*

enum class Sorting(val friendlyName: String) {
    DATE("Date"),
    DATE_REVERSED("Date (reversed)"),
    AMOUNT_INCREASING("Amount (increasing)"),
    AMOUNT_DECREASING("Amount (decreasing)");

    fun sortExpenses(expenses: MutableList<Expense>): MutableList<Expense>{
        return when(this){
            AMOUNT_INCREASING -> expenses.sortedBy { it.amount.first().value }
            AMOUNT_DECREASING -> expenses.sortedByDescending { it.amount.first().value }
            DATE_REVERSED -> expenses.sortedBy { GregorianCalendar(it.year, it.month, it.day, it.hour, it.minute).time.time }
            DATE -> expenses.sortedByDescending { GregorianCalendar(it.year, it.month, it.day, it.hour, it.minute).time.time }
        }.toMutableList()
    }

    companion object{
        fun fromFriendlyName(name: String): Sorting?{
            for (s in values()) if (s.friendlyName == name) return s
            return null
        }
    }
}