package com.misw.medisupply.core.base

/**
 * Custom exceptions for the application
 * Provides type-safe error handling
 */
sealed class AppException(message: String) : Exception(message) {
    
    /**
     * Network-related errors
     */
    data class NetworkException(
        override val message: String = "Error de conexión. Verifica tu internet."
    ) : AppException(message)

    /**
     * Database-related errors
     */
    data class DatabaseException(
        override val message: String = "Error al acceder a la base de datos local."
    ) : AppException(message)

    /**
     * Validation errors
     */
    data class ValidationException(
        override val message: String = "Error de validación."
    ) : AppException(message)

    /**
     * API errors with HTTP status code
     */
    data class ApiException(
        val code: Int,
        override val message: String = "Error en el servidor."
    ) : AppException(message)

    /**
     * Data not found
     */
    data class NotFoundException(
        override val message: String = "Recurso no encontrado."
    ) : AppException(message)

    /**
     * Unauthorized access
     */
    data class UnauthorizedException(
        override val message: String = "No autorizado. Inicia sesión nuevamente."
    ) : AppException(message)

    /**
     * Timeout errors
     */
    data class TimeoutException(
        override val message: String = "Tiempo de espera agotado."
    ) : AppException(message)

    /**
     * Unknown or unexpected errors
     */
    data class UnknownException(
        override val message: String = "Error inesperado."
    ) : AppException(message)
}
