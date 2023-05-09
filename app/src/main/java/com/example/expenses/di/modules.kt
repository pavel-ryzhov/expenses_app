package com.example.expenses.di

import android.content.Context
import androidx.room.Room
import com.example.expenses.BuildConfig
import com.example.expenses.data.data_sources.local.AppDatabase
import com.example.expenses.data.data_sources.local.dao.CategoriesDao
import com.example.expenses.data.data_sources.local.dao.ExchangeRatesDao
import com.example.expenses.data.data_sources.local.dao.ExpensesDao
import com.example.expenses.data.data_sources.local.dao.SymbolsDao
import com.example.expenses.data.data_sources.remote.RemoteExchangeRatesDataSource
import com.example.expenses.data.data_sources.remote.RemoteSymbolsDataSource
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.data.preferences.AppPreferencesImpl
import com.example.expenses.data.repository.exchange_rates.ExchangeRatesRepository
import com.example.expenses.data.repository.exchange_rates.ExchangeRatesRepositoryImpl
import com.example.expenses.data.repository.symbols.SymbolsRepository
import com.example.expenses.data.repository.symbols.SymbolsRepositoryImpl
import com.example.expenses.data.services.currency_converter.CurrenciesConverterService
import com.example.expenses.data.services.currency_converter.CurrenciesConverterServiceImpl
import com.example.expenses.data.services.expenses_statistics.ExpensesStatisticsService
import com.example.expenses.data.services.expenses_statistics.ExpensesStatisticsServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class SingletonComponentProvidesModule {
    @Provides
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "AppDatabase").build()
    }

    @Provides
    fun providesSymbolsDao(appDatabase: AppDatabase): SymbolsDao {
        return appDatabase.getSymbolsDao()
    }

    @Provides
    fun providesExchangeRatesDao(appDatabase: AppDatabase): ExchangeRatesDao {
        return appDatabase.getExchangeRatesDao()
    }

    @Provides
    fun providesExpensesDao(appDatabase: AppDatabase): ExpensesDao {
        return appDatabase.getExpensesDao()
    }

    @Provides
    fun providesCategoriesDao(appDatabase: AppDatabase): CategoriesDao {
        return appDatabase.getCategoriesDao()
    }

    @Provides
    fun providesSymbolsDataSource(retrofit: Retrofit): RemoteSymbolsDataSource {
        return retrofit.create(RemoteSymbolsDataSource::class.java)
    }

    @Provides
    fun providesExchangeRatesDataSource(retrofit: Retrofit): RemoteExchangeRatesDataSource {
        return retrofit.create(RemoteExchangeRatesDataSource::class.java)
    }

    @Provides
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonComponentBindsModule {
    @Binds
    abstract fun bindsAppPreferences(
        appPreferencesImpl: AppPreferencesImpl
    ): AppPreferences

    @Binds
    abstract fun bindsExpensesStatisticsService(
        expensesStatisticsServiceImpl: ExpensesStatisticsServiceImpl
    ): ExpensesStatisticsService

    @Binds
    abstract fun bindsCurrenciesConverterService(
        currenciesConverterServiceImpl: CurrenciesConverterServiceImpl
    ): CurrenciesConverterService

    @Binds
    abstract fun bindsSymbolsRepository(
        symbolsRepositoryImpl: SymbolsRepositoryImpl
    ): SymbolsRepository

    @Binds
    abstract fun bindsExchangeRatesRepository(
        exchangeRatesRepositoryImpl: ExchangeRatesRepositoryImpl
    ): ExchangeRatesRepository
}

