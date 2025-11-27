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
     * @param tradeName Trade name (optional)
     * @param documentNumber NIT/RUC or identification number
     * @param documentType Type of document (NIT, CC, etc.)
     * @param contactName Contact person name (optional)
     * @param contactEmail Contact email address
     * @param contactPhone Contact phone number
     * @param address Physical address
     * @param neighborhood Neighborhood (optional)
     * @param city City (optional)
     * @param department Department/State (optional)
     * @param country Country (default: Colombia)
     * @param latitude Latitude coordinate (optional)
     * @param longitude Longitude coordinate (optional)
     * @param customerType Type of customer (HOSPITAL, CLINICA, etc.)
     * @return Flow emitting Resource with created customer
     */
    fun registerCustomer(
        businessName: String,
        tradeName: String? = null,
        documentNumber: String,
        documentType: String = "NIT",
        contactName: String? = null,
        contactEmail: String,
        contactPhone: String,
        address: String,
        neighborhood: String? = null,
        city: String? = null,
        department: String? = null,
        country: String = "Colombia",
        latitude: Double? = null,
        longitude: Double? = null,
        customerType: String = "HOSPITAL",
        salespersonId: Int? = null
    ): Flow<Resource<Customer>>
    
    /**
     * Get customers assigned to a specific salesperson
     * 
     * @param salespersonId The ID of the salesperson
     * @param isActive Filter by active status (optional)
     * @return Flow emitting Resource with list of customers
     */
    fun getCustomersBySalesperson(
        salespersonId: Int,
        isActive: Boolean? = null
    ): Flow<Resource<List<Customer>>>

    fun getCustomersBySalespersonEmployeeId(
        salespersonId: String
    ): Flow<Resource<List<Customer>>>
}
