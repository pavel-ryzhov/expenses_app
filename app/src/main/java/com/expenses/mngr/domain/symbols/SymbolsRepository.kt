package com.expenses.mngr.domain.symbols

import androidx.lifecycle.LiveData
import com.expenses.mngr.entities.symbols.Symbol
import javax.inject.Singleton

@Singleton
interface SymbolsRepository {
    fun getSymbolsLiveData(): LiveData<MutableList<Symbol>>
    suspend fun fetchSymbols()
}