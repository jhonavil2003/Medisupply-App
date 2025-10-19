package com.misw.medisupply.di

import com.misw.medisupply.data.repository.customer.CustomerRepositoryImpl
import com.misw.medisupply.data.repository.order.OrderRepositoryImpl
import com.misw.medisupply.domain.repository.customer.CustomerRepository
import com.misw.medisupply.domain.repository.order.OrderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindCustomerRepository(
        customerRepositoryImpl: CustomerRepositoryImpl
    ): CustomerRepository
    
    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository
}
