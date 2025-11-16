package com.misw.medisupply.domain.usecase.customer

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import com.misw.medisupply.domain.repository.customer.CustomerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting customers assigned to a specific salesperson
 * Encapsulates the business logic for retrieving a salesperson's customer list
 */
class GetCustomersBySalespersonUseCase @Inject constructor(
    private val repository: CustomerRepository
) {
    /**
     * Execute the use case
     * 
     * @param salespersonId The ID of the salesperson
     * @param isActive Filter by active status (optional, defaults to true for active customers only)
     * @return Flow emitting Resource with list of customers assigned to the salesperson
     */
    operator fun invoke(
        salespersonId: Int,
        isActive: Boolean? = true
    ): Flow<Resource<List<Customer>>> {
        return repository.getCustomersBySalesperson(
            salespersonId = salespersonId,
            isActive = isActive
        )
    }
}
