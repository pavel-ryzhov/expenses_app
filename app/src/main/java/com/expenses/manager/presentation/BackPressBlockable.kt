package com.expenses.manager.presentation

interface BackPressBlockable {
    fun isBackPressAllowed(): Boolean
}