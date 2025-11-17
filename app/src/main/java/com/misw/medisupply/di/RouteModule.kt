package com.misw.medisupply.di

import com.misw.medisupply.data.repository.RouteRepositoryImpl
import com.misw.medisupply.domain.repository.RouteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt para inyección de dependencias de rutas
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RouteModule {
    
    @Binds
    @Singleton
    abstract fun bindRouteRepository(
        routeRepositoryImpl: RouteRepositoryImpl
    ): RouteRepository
}
