package com.misw.medisupply.di

import com.misw.medisupply.data.remote.api.stock.StockApiService
import com.misw.medisupply.data.repository.stock.StockRepositoryImpl
import com.misw.medisupply.domain.repository.stock.StockRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Stock Repository
 * Provides dependency injection for stock data operations
 */
@Module
@InstallIn(SingletonComponent::class)
object StockRepositoryModule {
    
    /**
     * Provides StockRepository implementation
     */
    @Provides
    @Singleton
    fun provideStockRepository(
        apiService: StockApiService
    ): StockRepository {
        return StockRepositoryImpl(apiService)
    }
}
