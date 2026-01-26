package com.example.ehefin_mobile.core.common

/**
 * Generic sealed class for wrapping API/Database responses
 * Supports Loading, Success, and Error states with optional data caching
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
