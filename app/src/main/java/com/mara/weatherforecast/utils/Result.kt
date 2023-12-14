package com.mara.weatherforecast.utils

/**
 * A sealed class to represent different states of a data fetching operation.
 */
sealed class Result<out T> {

    /**
     * Represents successful completion of the operation.
     * @param data The data resulted from the operation.
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Represents a failure in the operation.
     * @param exception The exception thrown during the operation.
     */
    data class Failure(val exception: Exception) : Result<Nothing>()

    /**
     * Represents the ongoing operation.
     */
    data object Loading : Result<Nothing>()
}
