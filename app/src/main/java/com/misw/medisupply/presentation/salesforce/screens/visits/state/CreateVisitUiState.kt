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
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val visitDate: LocalDate = LocalDate.now(),
    val visitTime: LocalTime = LocalTime.of(9, 0),
    
    // Location
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    
    // Files
    val attachments: List<String> = emptyList(),
    val visitFiles: List<com.misw.medisupply.domain.model.visit.VisitFile> = emptyList(),
    val isLoadingFiles: Boolean = false,
    val isUploadingFile: Boolean = false,
    val isDeletingFile: Boolean = false,
    val fileError: String? = null,
    val uploadProgress: Float = 0f,
    
    // Form state and flow control
    val isFormValid: Boolean = false,
    val isSaving: Boolean = false,
    val isAutoSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val successMessage: String? = null,
    
    // Step-by-step flow control
    val isCustomerSelected: Boolean = false,
    val areVisitFieldsComplete: Boolean = false,
    val isVisitSaved: Boolean = false,
    
    // Created visit information
    val createdVisitId: Int? = null,
    val visitId: Int? = null,
    
    // User interaction tracking (to require explicit interaction)
    val hasModifiedDate: Boolean = false,
    val hasModifiedTime: Boolean = false
)