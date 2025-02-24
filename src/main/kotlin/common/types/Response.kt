package org.example.common.types

import org.springframework.http.HttpStatus

object ResponseProvider {
    fun <T> success(result: T): Response<T> {
        return Response(HttpStatus.OK.value(), SUCCESS, result)
    }

    fun <T> error(code: HttpStatus, message: String, result : T? = null): Response<T> {
        return Response(code.value(), message, result)
    }

    fun <T> customError(code: Int, message: String, result : T? = null): Response<T> {
        return Response(code, message, result)
    }
}

data class Response<T>(
    val code: Int,
    val message: String?,
    val result: T?
)


const val SUCCESS = "SUCCESS"