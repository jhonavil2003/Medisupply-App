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
     * @param sellerId Optional filter by assigned salesperson (if provided, other filters are ignored)
     * @return Flow emitting Resource with list of customers
     */
    operator fun invoke(
        customerType: String? = null,
        city: String? = null,
        isActive: Boolean? = null,
        sellerId: Int? = null
    ): Flow<Resource<List<Customer>>> {
        // Si se proporciona sellerId, usar el método específico para vendedor
        return if (sellerId != null) {
            repository.getCustomersBySalesperson(sellerId, isActive)
        } else {
            repository.getCustomers(customerType, city, isActive)
        }
    }
}
