package com.misw.medisupply.di

import android.content.Context
import com.misw.medisupply.data.aws.S3UploadService
import com.misw.medisupply.data.repository.VideoAnalysisRepositoryImpl
import com.misw.medisupply.data.repository.VisitRepositoryImpl
import com.misw.medisupply.domain.repository.VideoAnalysisRepository
import com.misw.medisupply.domain.repository.VisitRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    
    @Binds
    @Singleton
    abstract fun bindVideoAnalysisRepository(
        videoAnalysisRepositoryImpl: VideoAnalysisRepositoryImpl
    ): VideoAnalysisRepository
    
    companion object {
        @Provides
        @Singleton
        fun provideS3UploadService(
            @ApplicationContext context: Context
        ): S3UploadService {
            return S3UploadService(context)
        }
    }
}