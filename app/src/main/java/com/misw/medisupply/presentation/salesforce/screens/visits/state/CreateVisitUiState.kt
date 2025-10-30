package com.misw.medisupply.presentation.salesforce.screens.visits.state

import com.misw.medisupply.domain.model.customer.Customer
import java.time.LocalDate
import java.time.LocalTime

data class CreateVisitUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // Customer selection
    val selectedCustomer: Customer? = null,
    val customerSearchQuery: String = "",
    val customerSearchResults: List<Customer> = emptyList(),
    val isSearchingCustomers: Boolean = false,
    val showCustomerDropdown: Boolean = false,
    
    // Visit details
    val contactedPersons: String = "",
    val clinicalFindings: String = "",
    val additionalNotes: String = "",
    val visitDate: LocalDate = LocalDate.now(),
    val visitTime: LocalTime = LocalTime.of(9, 0),
    
    // Location
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    
    // Files
    val attachments: List<String> = emptyList(),
    
    // Form state and flow control
    val isFormValid: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    
    // Step-by-step flow control
    val isCustomerSelected: Boolean = false,
    val areVisitFieldsComplete: Boolean = false,
    val isVisitSaved: Boolean = false
)