package com.misw.medisupply.presentation.salesforce.screens.visits.state

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class VisitUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val visits: List<Visit> = emptyList()
)

data class Visit(
    val id: Int = 0,
    val customerName: String = "",
    val contactedPersons: String = "",
    val clinicalFindings: String = "",
    val additionalNotes: String = "",
    val visitDate: LocalDate = LocalDate.now(),
    val visitTime: LocalTime = LocalTime.now(),
    val location: String = "",
    val attachments: List<String> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)