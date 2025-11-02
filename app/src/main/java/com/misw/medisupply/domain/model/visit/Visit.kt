package com.misw.medisupply.domain.model.visit

import java.time.LocalDate
import java.time.LocalTime

data class Visit(
    val id: Int = 0,
    val customerId: Int,
    val salespersonId: Int,
    val visitDate: LocalDate,
    val visitTime: LocalTime,
    val contactedPersons: String? = null,
    val clinicalFindings: String? = null,
    val additionalNotes: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val status: VisitStatus = VisitStatus.PROGRAMADA,
    val createdAt: String? = null,
    val updatedAt: String? = null
)