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
    
    /**
     * Validate if a document number is already registered
     * 
     * @param documentNumber The document number to validate
     * @param documentType The type of document (NIT, CC, etc.)
     * @return Flow emitting Resource with validation result (true if exists, false if available)
     */
    fun validateDocumentNumber(
        documentNumber: String,
        documentType: String = "NIT"
    ): Flow<Resource<Boolean>>
    
    /**
     * Register a new customer
     * 
     * @param businessName Company/Institution name
     * @param documentNumber NIT/RUC or identification number
     * @param documentType Type of document (NIT, CC, etc.)
     * @param contactEmail Contact email address
     * @param contactPhone Contact phone number
     * @param address Physical address
     * @param city City (optional)
     * @param department Department/State (optional)
     * @param customerType Type of customer (HOSPITAL, CLINICA, etc.)
     * @return Flow emitting Resource with created customer
     */
    fun registerCustomer(
        businessName: String,
        documentNumber: String,
        documentType: String = "NIT",
        contactEmail: String,
        contactPhone: String,
        address: String,
        city: String? = null,
        department: String? = null,
        customerType: String = "HOSPITAL"
    ): Flow<Resource<Customer>>
}
