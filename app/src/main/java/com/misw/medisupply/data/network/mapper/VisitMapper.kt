package com.misw.medisupply.data.network.mapper

import com.misw.medisupply.data.network.dto.visit.VisitDto
import com.misw.medisupply.domain.model.visit.Visit
import com.misw.medisupply.domain.model.visit.VisitStatus
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun VisitDto.toVisit(): Visit {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    
    return Visit(
        id = this.id,
        customerId = this.customerId,
        salespersonId = this.salespersonId,
        visitDate = LocalDate.parse(this.visitDate, dateFormatter),
        visitTime = this.visitTime?.let { LocalTime.parse(it, timeFormatter) } ?: LocalTime.MIDNIGHT,
        contactedPersons = this.contactedPersons,
        clinicalFindings = this.clinicalFindings,
        additionalNotes = this.additionalNotes,
        address = this.address,
        latitude = this.latitude,
        longitude = this.longitude,
        status = when (this.status.uppercase()) {
            "PROGRAMADA" -> VisitStatus.PROGRAMADA
            "REALIZADA", "COMPLETADA" -> VisitStatus.COMPLETADA
            "CANCELADA", "ELIMINADA" -> VisitStatus.ELIMINADA
            else -> VisitStatus.PROGRAMADA
        },
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
