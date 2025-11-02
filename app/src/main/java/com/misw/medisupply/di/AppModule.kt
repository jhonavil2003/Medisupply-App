package com.misw.medisupply.core.di

import android.content.Context
import com.misw.medisupply.core.session.SessionManager
import com.misw.medisupply.core.session.UserSessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ): Context = context
    
    @Provides
    @Singleton
    fun provideSessionManager(): SessionManager = SessionManager()
    
    @Provides
    @Singleton
    fun provideUserSessionManager(): UserSessionManager = UserSessionManager()
}
