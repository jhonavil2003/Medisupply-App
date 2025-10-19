package com.misw.medisupply.core.base

import org.junit.Test
import org.junit.Assert.*

class AppExceptionTest {

    @Test
    fun `NetworkException has correct default message`() {
        val exception = AppException.NetworkException()
        assertEquals("Error de conexión. Verifica tu internet.", exception.message)
    }

    @Test
    fun `NetworkException accepts custom message`() {
        val customMessage = "No hay conexión"
        val exception = AppException.NetworkException(customMessage)
        assertEquals(customMessage, exception.message)
    }

    @Test
    fun `DatabaseException has correct default message`() {
        val exception = AppException.DatabaseException()
        assertEquals("Error al acceder a la base de datos local.", exception.message)
    }

    @Test
    fun `ValidationException has correct default message`() {
        val exception = AppException.ValidationException()
        assertEquals("Error de validación.", exception.message)
    }

    @Test
    fun `ValidationException accepts custom message`() {
        val customMessage = "Campo requerido"
        val exception = AppException.ValidationException(customMessage)
        assertEquals(customMessage, exception.message)
    }

    @Test
    fun `ApiException has correct code and message`() {
        val exception = AppException.ApiException(code = 404, message = "Not Found")
        assertEquals(404, exception.code)
        assertEquals("Not Found", exception.message)
    }

    @Test
    fun `ApiException has default message when not provided`() {
        val exception = AppException.ApiException(code = 500)
        assertEquals(500, exception.code)
        assertEquals("Error en el servidor.", exception.message)
    }

    @Test
    fun `NotFoundException has correct default message`() {
        val exception = AppException.NotFoundException()
        assertEquals("Recurso no encontrado.", exception.message)
    }

    @Test
    fun `UnauthorizedException has correct default message`() {
        val exception = AppException.UnauthorizedException()
        assertEquals("No autorizado. Inicia sesión nuevamente.", exception.message)
    }

    @Test
    fun `TimeoutException has correct default message`() {
        val exception = AppException.TimeoutException()
        assertEquals("Tiempo de espera agotado.", exception.message)
    }

    @Test
    fun `UnknownException has correct default message`() {
        val exception = AppException.UnknownException()
        assertEquals("Error inesperado.", exception.message)
    }

    @Test
    fun `all exceptions are instances of AppException`() {
        assertTrue(AppException.NetworkException() is AppException)
        assertTrue(AppException.DatabaseException() is AppException)
        assertTrue(AppException.ValidationException() is AppException)
        assertTrue(AppException.ApiException(500) is AppException)
        assertTrue(AppException.NotFoundException() is AppException)
        assertTrue(AppException.UnauthorizedException() is AppException)
        assertTrue(AppException.TimeoutException() is AppException)
        assertTrue(AppException.UnknownException() is AppException)
    }
}
