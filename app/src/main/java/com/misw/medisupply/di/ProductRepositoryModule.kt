package com.misw.medisupply.di

import com.misw.medisupply.data.remote.api.product.ProductApiService
import com.misw.medisupply.data.repository.product.ProductRepositoryImpl
import com.misw.medisupply.domain.repository.product.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for Product Repository
 */
@Module
@InstallIn(SingletonComponent::class)
object ProductRepositoryModule {
    
    /**
     * Provides ProductRepository implementation
     */
    @Provides
    @Singleton
    fun provideProductRepository(
        apiService: ProductApiService
    ): ProductRepository {
        return ProductRepositoryImpl(apiService)
    }
}
