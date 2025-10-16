package com.misw.medisupply.domain.usecase.customer

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.repository.customer.CustomerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting a single customer by ID
 * Encapsulates the business logic for retrieving a specific customer
 */
class GetCustomerByIdUseCase @Inject constructor(
    private val repository: CustomerRepository
) {
    /**
     * Execute the use case
     * 
     * @param customerId The unique ID of the customer to retrieve
     * @return Flow emitting Resource with customer data
     */
    operator fun invoke(customerId: Int): Flow<Resource<Customer>> {
        return repository.getCustomerById(customerId)
    }
}
