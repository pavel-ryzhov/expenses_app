package com.example.expenses.presentation

interface BackPressBlockable {
    fun isBackPressAllowed(): Boolean
}