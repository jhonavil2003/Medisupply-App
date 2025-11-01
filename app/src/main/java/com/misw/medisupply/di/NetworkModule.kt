package com.misw.medisupply.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.misw.medisupply.data.remote.api.customer.CustomerApiService
import com.misw.medisupply.data.remote.api.order.OrderApiService
import com.misw.medisupply.data.remote.api.product.ProductApiService
import com.misw.medisupply.data.remote.api.stock.StockApiService
import com.misw.medisupply.core.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualifier for Customer Service Retrofit
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CustomerRetrofit

/**
 * Qualifier for Catalog Service Retrofit
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CatalogRetrofit

/**
 * Qualifier for Logistics Service Retrofit
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LogisticsRetrofit

/**
 * Qualifier for Sales Service Retrofit
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SalesRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }
    
    /**
     * Provides HTTP logging interceptor for debugging
     */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    /**
     * Provides OkHttp client with logging and timeouts
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    
    /**
     * Provides Retrofit instance for Customer Service
     */
    @Provides
    @Singleton
    @CustomerRetrofit
    fun provideCustomerRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    /**
     * Provides Retrofit instance for Catalog Service
     */
    @Provides
    @Singleton
    @CatalogRetrofit
    fun provideCatalogRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.CATALOG_SERVICE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    /**
     * Provides Retrofit instance for Logistics Service
     */
    @Provides
    @Singleton
    @LogisticsRetrofit
    fun provideLogisticsRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.LOGISTICS_SERVICE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    /**
     * Provides Retrofit instance for Sales Service
     */
    @Provides
    @Singleton
    @SalesRetrofit
    fun provideSalesRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.SALES_SERVICE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    /**
     * Provides Retrofit instance (default - for backward compatibility)
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    /**
     * Provides CustomerApiService
     */
    @Provides
    @Singleton
    fun provideCustomerApiService(@CustomerRetrofit retrofit: Retrofit): CustomerApiService {
        return retrofit.create(CustomerApiService::class.java)
    }
    
    /**
     * Provides ProductApiService
     */
    @Provides
    @Singleton
    fun provideProductApiService(@CatalogRetrofit retrofit: Retrofit): ProductApiService {
        return retrofit.create(ProductApiService::class.java)
    }
    
    /**
     * Provides StockApiService
     */
    @Provides
    @Singleton
    fun provideStockApiService(@LogisticsRetrofit retrofit: Retrofit): StockApiService {
        return retrofit.create(StockApiService::class.java)
    }
    
    /**
     * Provides OrderApiService
     */
    @Provides
    @Singleton
    fun provideOrderApiService(@SalesRetrofit retrofit: Retrofit): OrderApiService {
        return retrofit.create(OrderApiService::class.java)
    }
    

}
