package com.misw.medisupply.core.base

/**
 * A generic wrapper class that holds data with its loading status.
 * Used to represent the state of a data loading operation.
 *
 * @param T The type of data being wrapped
 * @param data The actual data (nullable)
 * @param message Optional message, typically used for error descriptions
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    /**
     * Represents a successful data loading state
     */
    class Success<T>(data: T) : Resource<T>(data)

    /**
     * Represents an error state
     * @param message Error message describing what went wrong
     * @param data Optional data that might have been partially loaded
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    /**
     * Represents a loading state
     * @param data Optional data from cache or previous load
     */
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
