package com.misw.medisupply.domain.model.route

import java.time.LocalTime

/**
 * Horario laboral para la ruta
 */
data class WorkHours(
    val start: LocalTime = LocalTime.of(8, 0),
    val end: LocalTime = LocalTime.of(18, 0)
) {
    /**
     * Convierte a formato de API (HH:mm)
     */
    fun toApiFormat(): Pair<String, String> {
        return Pair(
            start.toString().substring(0, 5), // "08:00"
            end.toString().substring(0, 5)    // "18:00"
        )
    }
    
    companion object {
        /**
         * Crea WorkHours desde strings de la API
         */
        fun fromApiFormat(startStr: String, endStr: String): WorkHours {
            return WorkHours(
                start = LocalTime.parse(startStr),
                end = LocalTime.parse(endStr)
            )
        }
    }
}
