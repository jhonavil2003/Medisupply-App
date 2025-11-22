package com.misw.medisupply.data.network.mapper

import com.misw.medisupply.data.network.dto.route.*
import com.misw.medisupply.domain.model.route.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Convierte RouteDto a Route (modelo de dominio)
 */
fun RouteDto.toRoute(): Route {
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    return Route(
        id = this.id,
        routeCode = this.routeCode,
        salespersonId = this.salesperson.id,
        salespersonName = this.salesperson.name,
        salespersonEmployeeId = this.salesperson.employeeId ?: "",
        plannedDate = LocalDate.parse(this.plannedDate, dateFormatter),
        status = RouteStatus.fromApiValue(this.status),
        metrics = this.metrics.toRouteMetrics(),
        stops = this.stops.map { it.toRouteStop() },
        startLocation = this.startLocation?.toLocation(),
        endLocation = this.endLocation?.toLocation(),
        workHours = this.workHours.toWorkHours(),
        mapUrl = this.mapUrl,
        createdAt = this.createdAt?.let { 
            try { LocalDateTime.parse(it, dateTimeFormatter) } catch (e: Exception) { null }
        },
        updatedAt = this.updatedAt?.let { 
            try { LocalDateTime.parse(it, dateTimeFormatter) } catch (e: Exception) { null }
        },
        confirmedAt = this.timestamps?.confirmedAt?.let { 
            try { LocalDateTime.parse(it, dateTimeFormatter) } catch (e: Exception) { null }
        },
        startedAt = this.timestamps?.startedAt?.let { 
            try { LocalDateTime.parse(it, dateTimeFormatter) } catch (e: Exception) { null }
        },
        completedAt = this.timestamps?.completedAt?.let { 
            try { LocalDateTime.parse(it, dateTimeFormatter) } catch (e: Exception) { null }
        }
    )
}

/**
 * Convierte RouteSummaryDto a Route (versión resumida)
 */
fun RouteSummaryDto.toRoute(): Route {
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    
    return Route(
        id = this.id,
        routeCode = this.routeCode,
        salespersonId = 0, // No disponible en summary
        salespersonName = "",
        salespersonEmployeeId = "",
        plannedDate = LocalDate.parse(this.plannedDate, dateFormatter),
        status = RouteStatus.fromApiValue(this.status),
        metrics = this.metrics.toRouteMetrics(),
        stops = emptyList(), // No disponible en summary
        workHours = WorkHours()
    )
}

/**
 * Convierte RouteMetricsDto a RouteMetrics
 */
fun RouteMetricsDto.toRouteMetrics(): RouteMetrics {
    return RouteMetrics(
        totalStops = this.totalStops,
        totalDistanceKm = this.totalDistanceKm,
        estimatedDurationMinutes = this.estimatedDurationMinutes,
        optimizationScore = this.optimizationScore,
        completedStops = this.completedStops,
        skippedStops = this.skippedStops
    )
}

/**
 * Convierte RouteStopDto a RouteStop
 */
fun RouteStopDto.toRouteStop(): RouteStop {
    val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    return RouteStop(
        id = this.id,
        sequenceOrder = this.sequenceOrder,
        customerId = this.customer.id,
        customerName = this.customer.name,
        customerDocument = this.customer.document,
        customerType = this.customer.type,
        address = this.location.address,
        neighborhood = this.location.neighborhood,
        city = this.location.city,
        latitude = this.location.latitude,
        longitude = this.location.longitude,
        contactName = this.customer.contact.name,
        contactPhone = this.customer.contact.phone,
        contactEmail = this.customer.contact.email,
        estimatedArrival = try {
            LocalDateTime.parse(this.estimatedTimes.arrival, dateTimeFormatter)
        } catch (e: Exception) {
            // Fallback para otros formatos de fecha
            try {
                LocalDateTime.parse(this.estimatedTimes.arrival.replace("Z", ""))
            } catch (e2: Exception) {
                LocalDateTime.now() // Fallback
            }
        },
        estimatedDeparture = try {
            LocalDateTime.parse(this.estimatedTimes.departure, dateTimeFormatter)
        } catch (e: Exception) {
            // Fallback para otros formatos de fecha
            try {
                LocalDateTime.parse(this.estimatedTimes.departure.replace("Z", ""))
            } catch (e2: Exception) {
                LocalDateTime.now().plusMinutes(this.estimatedTimes.serviceMinutes.toLong()) // Fallback
            }
        },
        serviceMinutes = this.estimatedTimes.serviceMinutes,
        distanceFromPreviousKm = this.distanceMetrics.fromPreviousKm,
        travelTimeMinutes = this.distanceMetrics.travelTimeMinutes,
        actualArrival = this.actualTimes?.arrival?.let { 
            try { LocalDateTime.parse(it, dateTimeFormatter) } catch (e: Exception) { null }
        },
        actualDeparture = this.actualTimes?.departure?.let { 
            try { LocalDateTime.parse(it, dateTimeFormatter) } catch (e: Exception) { null }
        },
        actualServiceMinutes = this.actualTimes?.serviceMinutes,
        isCompleted = this.status.isCompleted,
        isSkipped = this.status.isSkipped,
        completedAt = if (this.status.isCompleted) {
            this.timestamps?.completedAt?.let { 
                try { LocalDateTime.parse(it, dateTimeFormatter) } catch (e: Exception) { null }
            }
        } else null,
        skippedAt = if (this.status.isSkipped) {
            this.timestamps?.completedAt?.let { 
                try { LocalDateTime.parse(it, dateTimeFormatter) } catch (e: Exception) { null }
            }
        } else null,
        notes = this.notes,
        skipReason = this.status.skipReason
    )
}

/**
 * Convierte LocationDto a Location
 */
fun LocationDto.toLocation(): Location {
    return Location(
        name = this.name,
        latitude = this.latitude,
        longitude = this.longitude
    )
}

/**
 * Convierte Location a LocationDto
 */
fun Location.toDto(): LocationDto {
    return LocationDto(
        name = this.name,
        latitude = this.latitude,
        longitude = this.longitude
    )
}

/**
 * Convierte WorkHoursDto a WorkHours
 */
fun WorkHoursDto.toWorkHours(): WorkHours {
    return WorkHours.fromApiFormat(this.start, this.end)
}

/**
 * Convierte WorkHours a WorkHoursDto
 */
fun WorkHours.toDto(): WorkHoursDto {
    val (start, end) = this.toApiFormat()
    return WorkHoursDto(
        start = start,
        end = end
    )
}
