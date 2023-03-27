package com.example.expenses.data.repository.symbols

import androidx.lifecycle.LiveData
import com.example.expenses.entities.symbols.Symbol

interface SymbolsRepository {
    fun getSymbolsLiveData(): LiveData<MutableList<Symbol>>
    fun fetchSymbols()
}