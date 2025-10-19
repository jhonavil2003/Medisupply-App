package com.misw.medisupply.core.base

/**
 * A wrapper for network call results
 * Provides a type-safe way to handle API responses
 */
sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
    val code: Int? = null
) {
    /**
     * Successful network call with data
     */
    class Success<T>(data: T) : NetworkResult<T>(data)

    /**
     * Network call failed with error
     * @param message Error message
     * @param code HTTP status code
     * @param data Optional partial data
     */
    class Error<T>(
        message: String,
        code: Int? = null,
        data: T? = null
    ) : NetworkResult<T>(data, message, code)

    /**
     * Network call is in progress
     */
    class Loading<T> : NetworkResult<T>()
}

/**
 * Extension function to convert NetworkResult to Resource
 */
fun <T> NetworkResult<T>.toResource(): Resource<T> {
    return when (this) {
        is NetworkResult.Success -> Resource.Success(this.data!!)
        is NetworkResult.Error -> Resource.Error(
            message = this.message ?: "Unknown error",
            data = this.data
        )
        is NetworkResult.Loading -> Resource.Loading()
    }
}
