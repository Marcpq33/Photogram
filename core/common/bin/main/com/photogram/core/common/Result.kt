package com.photogram.core.common

sealed class PhotogramResult<out T> {
    data class Success<T>(val data: T) : PhotogramResult<T>()
    data class Error(
        val exception: Throwable,
        val message: String? = null,
    ) : PhotogramResult<Nothing>()
    data object Loading : PhotogramResult<Nothing>()
}

inline fun <T> PhotogramResult<T>.onSuccess(action: (T) -> Unit): PhotogramResult<T> {
    if (this is PhotogramResult.Success) action(data)
    return this
}

inline fun <T> PhotogramResult<T>.onError(
    action: (Throwable, String?) -> Unit,
): PhotogramResult<T> {
    if (this is PhotogramResult.Error) action(exception, message)
    return this
}

inline fun <T, R> PhotogramResult<T>.map(
    transform: (T) -> R,
): PhotogramResult<R> = when (this) {
    is PhotogramResult.Success -> PhotogramResult.Success(transform(data))
    is PhotogramResult.Error -> this
    is PhotogramResult.Loading -> PhotogramResult.Loading
}
