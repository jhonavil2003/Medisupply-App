package com.misw.medisupply.domain.repository.customer

import com.misw.medisupply.core.base.Resource
import com.misw.medisupply.domain.model.customer.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    
    /**
     * Get list of customers with optional filters
     * 
     * @param customerType Filter by customer type
     * @param city Filter by city
     * @param isActive Filter by active status
     * @return Flow emitting Resource with list of customers
     */
    fun getCustomers(
        customerType: String? = null,
        city: String? = null,
        isActive: Boolean? = null
    ): Flow<Resource<List<Customer>>>
    
    /**
     * Get a single customer by ID
     * 
     * @param customerId The unique ID of the customer
     * @return Flow emitting Resource with customer data
     */
    fun getCustomerById(customerId: Int): Flow<Resource<Customer>>
}
