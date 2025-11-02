package com.misw.medisupply.di

import com.misw.medisupply.data.repository.VisitRepositoryImpl
import com.misw.medisupply.domain.repository.VisitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VisitModule {

    @Binds
    @Singleton
    abstract fun bindVisitRepository(
        visitRepositoryImpl: VisitRepositoryImpl
    ): VisitRepository
}