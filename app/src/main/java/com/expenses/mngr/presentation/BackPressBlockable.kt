package com.expenses.mngr.presentation

interface BackPressBlockable {
    fun isBackPressAllowed(): Boolean
}