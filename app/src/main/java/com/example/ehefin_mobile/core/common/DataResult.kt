package com.example.ehefin_mobile.core.common

/**
 * Sealed class for wrapping DataSource layer responses.
 * Used by DataSource implementations to return success or error states.
 */
sealed class DataResult<out T> {
    /**
     * Represents a successful operation with data
     */
    data class Success<T>(val data: T) : DataResult<T>()
    
    /**
     * Represents a failed operation with error details
     */
    data class Error(
        val message: String,
        val code: Int? = null,
        val throwable: Throwable? = null
    ) : DataResult<Nothing>()
    
    /**
     * Maps the data of a Success result to a new type.
     */
    inline fun <R> map(transform: (T) -> R): DataResult<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> this
        }
    }
    
    /**
     * Returns the data if Success, otherwise null.
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }
    
    /**
     * Returns true if this is a Success.
     */
    val isSuccess: Boolean get() = this is Success
    
    /**
     * Returns true if this is an Error.
     */
    val isError: Boolean get() = this is Error
}

/**
 * Extension function to convert DataResult to Resource for use in UI layer.
 */
fun <T> DataResult<T>.toResource(): Resource<T> {
    return when (this) {
        is DataResult.Success -> Resource.Success(data)
        is DataResult.Error -> Resource.Error(message)
    }
}