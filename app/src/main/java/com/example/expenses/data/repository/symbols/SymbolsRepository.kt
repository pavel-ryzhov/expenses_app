package com.example.expenses.data.repository.symbols

import androidx.lifecycle.LiveData
import com.example.expenses.entities.symbols.Symbol
import javax.inject.Singleton

@Singleton
interface SymbolsRepository {
    fun getSymbolsLiveData(): LiveData<MutableList<Symbol>>
    fun fetchSymbols()
}