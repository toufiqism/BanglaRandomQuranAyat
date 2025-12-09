package com.toufiq.banglaayat.data.common

import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class NetworkError(override val message: String, override val cause: Throwable? = null) : Exception(message, cause) {
    class NoInternet(cause: Throwable? = null) : NetworkError("No internet connection", cause)
    class Timeout(cause: Throwable? = null) : NetworkError("Connection timed out", cause)
    class Server(val code: Int, cause: Throwable? = null) : NetworkError("Server error: $code", cause)
    class Unknown(cause: Throwable? = null) : NetworkError(cause?.message ?: "Unknown error occurred", cause)
}

fun Throwable.toNetworkError(): NetworkError = when (this) {
    is UnknownHostException, is ConnectException -> NetworkError.NoInternet(this)
    is SocketTimeoutException -> NetworkError.Timeout(this)
    is HttpException -> NetworkError.Server(code(), this)
    else -> NetworkError.Unknown(this)
}

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable, val message: String = exception.message ?: "Unknown error") : Result<Nothing>()
    data object Loading : Result<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun exceptionOrNull(): Throwable? = when (this) {
        is Error -> exception
        else -> null
    }

    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> Loading
    }

    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (Throwable, String) -> Unit): Result<T> {
        if (this is Error) action(exception, message)
        return this
    }

    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun error(exception: Throwable, message: String = exception.message ?: "Unknown error"): Result<Nothing> = Error(exception, message)
        fun loading(): Result<Nothing> = Loading
    }
}
