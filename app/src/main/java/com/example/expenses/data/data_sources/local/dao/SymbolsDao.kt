package com.example.expenses.data.data_sources.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expenses.entities.symbols.Symbol

@Dao
interface SymbolsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllSymbols(list: MutableList<Symbol>)

    @Query("SELECT * FROM symbol")
    fun getSymbolsLiveData(): LiveData<MutableList<Symbol>>

    @Query("SELECT * FROM symbol")
    fun getAllSymbols(): MutableList<Symbol>
}