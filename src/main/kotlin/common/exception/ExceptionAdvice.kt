package org.example.common.exception

import org.example.common.types.Response
import org.example.common.types.ResponseProvider
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException


@RestControllerAdvice
class ExceptionAdvice {

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentTypeMismatch(ex: MethodArgumentTypeMismatchException): Response<Any> {
        val msg = "Invalid value for parameter '" + ex.name

        return ResponseProvider.error(HttpStatus.BAD_REQUEST, msg)
    }

    @ExceptionHandler(CustomException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleException(ex: CustomException): Response<Any> {
        val codeInterface = ex.getCodeInterface()

        val msg = codeInterface.message
        val code = codeInterface.code

        return ResponseProvider.customError(code, msg)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    fun handleMethodNotSupported(ex: HttpRequestMethodNotSupportedException): Response<Any> {
        val msg = "Method '${ex.method}' not supported. Supported methods: ${ex.supportedHttpMethods}"

        return ResponseProvider.error(HttpStatus.BAD_REQUEST, msg)
    }

}