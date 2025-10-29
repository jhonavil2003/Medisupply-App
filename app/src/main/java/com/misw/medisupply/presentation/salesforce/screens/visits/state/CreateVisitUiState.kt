package com.misw.medisupply.presentation.salesforce.screens.visits.state

import java.time.LocalDate
import java.time.LocalTime

data class CreateVisitUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val customerName: String = "",
    val contactedPersons: String = "",
    val clinicalFindings: String = "",
    val additionalNotes: String = "",
    val visitDate: LocalDate = LocalDate.now(),
    val visitTime: LocalTime = LocalTime.now(),
    val location: String = "",
    val attachments: List<String> = emptyList(),
    val isFormValid: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)