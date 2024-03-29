package com.expenses.mngr.domain.symbols

import com.expenses.mngr.data.data_sources.local.dao.SymbolsDao
import com.expenses.mngr.data.wrappers.RemoteSymbolsDataSourceWrapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SymbolsRepositoryImpl @Inject constructor(
    private val symbolsDao: SymbolsDao,
    private val remoteSymbolsDataSource: RemoteSymbolsDataSourceWrapper
) : SymbolsRepository {
    override fun getSymbolsLiveData() = symbolsDao.getSymbolsLiveData()

    override suspend fun fetchSymbols() {
        symbolsDao.insertAllSymbols(remoteSymbolsDataSource.getSymbols())
    }
}