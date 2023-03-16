package com.fmontano.weather.android.repo

/**
 * Abstraction class that represents the status of a response. It encapuslates the data of a
 * successful response in the data property of a RequestResponse.Success, and the exception
 * of an error response inside the exception property of RequestResponse.Error
 */
sealed class RequestResponse<T> {

    data class Success<T>(val data: T) : RequestResponse<T>()

    data class Error<T>(val exception: Exception? = null) : RequestResponse<T>()
}
