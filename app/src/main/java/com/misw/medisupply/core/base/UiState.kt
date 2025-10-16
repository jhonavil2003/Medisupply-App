package com.misw.medisupply.core.base

/**
 * Represents the state of a UI component
 * Used in ViewModels to manage screen states
 */
sealed class UiState<out T> {
    /**
     * Initial idle state, no action has been performed yet
     */
    object Idle : UiState<Nothing>()

    /**
     * Loading state, data is being fetched
     */
    object Loading : UiState<Nothing>()

    /**
     * Success state with data
     * @param data The successfully loaded data
     */
    data class Success<T>(val data: T) : UiState<T>()

    /**
     * Error state
     * @param message Error message to display
     * @param exception Optional exception for debugging
     */
    data class Error(
        val message: String,
        val exception: Throwable? = null
    ) : UiState<Nothing>()
}

/**
 * Extension function to check if state is loading
 */
fun <T> UiState<T>.isLoading(): Boolean = this is UiState.Loading

/**
 * Extension function to check if state is success
 */
fun <T> UiState<T>.isSuccess(): Boolean = this is UiState.Success

/**
 * Extension function to check if state is error
 */
fun <T> UiState<T>.isError(): Boolean = this is UiState.Error

/**
 * Extension function to get data if success, null otherwise
 */
fun <T> UiState<T>.getDataOrNull(): T? = when (this) {
    is UiState.Success -> this.data
    else -> null
}
