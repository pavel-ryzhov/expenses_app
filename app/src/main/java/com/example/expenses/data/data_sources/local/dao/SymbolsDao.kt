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
    suspend fun insertAllSymbols(list: MutableList<Symbol>)

    @Query("SELECT * FROM symbol")
    fun getSymbolsLiveData(): LiveData<MutableList<Symbol>>

    @Query("SELECT * FROM symbol")
    suspend fun getAllSymbols(): MutableList<Symbol>

    @Query("SELECT * FROM symbol WHERE code IN (:codes)")
    suspend fun getSymbolsByCodes(codes: List<String>): MutableList<Symbol>

    @Query("SELECT * FROM symbol WHERE code = :code")
    suspend fun getSymbolByCode(code: String): Symbol
}