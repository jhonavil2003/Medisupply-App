package com.misw.medisupply.di

import com.misw.medisupply.data.local.session.SessionManager
import com.misw.medisupply.data.remote.api.cart.CartApiService
import com.misw.medisupply.data.repository.cart.CartRepositoryImpl
import com.misw.medisupply.domain.repository.cart.CartRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Cart Repository
 * Provides cart-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object CartRepositoryModule {
    
    /**
     * Provides CartRepository implementation
     */
    @Provides
    @Singleton
    fun provideCartRepository(
        apiService: CartApiService,
        sessionManager: SessionManager
    ): CartRepository {
        return CartRepositoryImpl(apiService, sessionManager)
    }
}
