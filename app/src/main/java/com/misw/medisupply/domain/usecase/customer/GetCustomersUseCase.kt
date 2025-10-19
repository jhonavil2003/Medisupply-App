package com.misw.medisupply.domain.usecase.customer

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.repository.customer.CustomerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting a list of customers
 * Encapsulates the business logic for retrieving customers with optional filters
 */
class GetCustomersUseCase @Inject constructor(
    private val repository: CustomerRepository
) {
    /**
     * Execute the use case
     * 
     * @param customerType Optional filter by customer type
     * @param city Optional filter by city
     * @param isActive Optional filter by active status
     * @return Flow emitting Resource with list of customers
     */
    operator fun invoke(
        customerType: String? = null,
        city: String? = null,
        isActive: Boolean? = null
    ): Flow<Resource<List<Customer>>> {
        return repository.getCustomers(customerType, city, isActive)
    }
}
