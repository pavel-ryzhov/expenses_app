package com.example.expenses.data.repository.symbols

import com.example.expenses.data.data_sources.local.dao.SymbolsDao
import com.example.expenses.data.wrappers.RemoteSymbolsDataSourceWrapper
import javax.inject.Inject

class SymbolsRepositoryImpl @Inject constructor(
    private val symbolsDao: SymbolsDao,
    private val remoteSymbolsDataSource: RemoteSymbolsDataSourceWrapper
) : SymbolsRepository {
    override fun getSymbolsLiveData() = symbolsDao.getSymbolsLiveData()

    override fun fetchSymbols() {
        symbolsDao.insertAllSymbols(remoteSymbolsDataSource.getSymbols())
    }
}