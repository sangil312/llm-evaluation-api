package com.dev.assignment.api.controller

import com.dev.assignment.support.response.Response
import com.dev.assignment.support.error.CoreException
import com.dev.assignment.support.error.ErrorType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class ApiControllerAdvice {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(CoreException::class)
    fun handleCoreException(e: CoreException): ResponseEntity<Response<Any>> {
        when (e.errorType.logLevel) {
            LogLevel.ERROR -> log.error("CoreException : ${e.message}", e)
            LogLevel.WARN -> log.warn("CoreException : ${e.message}", e)
            else -> log.info("CoreException : ${e.message}", e)
        }
        return ResponseEntity(Response.error(e.errorType), e.errorType.status)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<Response<Any>> {
        log.error("Exception : ${e.message}", e)
        return ResponseEntity(Response.error(ErrorType.DEFAULT_ERROR), ErrorType.DEFAULT_ERROR.status)
    }

    @ExceptionHandler(
        MethodArgumentTypeMismatchException::class,
        ServletRequestBindingException::class,
        HandlerMethodValidationException::class,
        HttpMessageNotReadableException::class
    )
    fun handleRequestParameter(e: Exception): ResponseEntity<Response<Any>> {
        log.info("${e.javaClass}: ${e.message}")
        return ResponseEntity(Response.error(ErrorType.INVALID_REQUEST), ErrorType.INVALID_REQUEST.status)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<Response<Any>> {
        return ResponseEntity(
            Response.error(ErrorType.INVALID_REQUEST, getValidationMessage(e)),
            ErrorType.INVALID_REQUEST.status
        )
    }

    private fun getValidationMessage(e: MethodArgumentNotValidException): String {
        return e.bindingResult.fieldErrors.firstOrNull()?.defaultMessage
            ?: e.bindingResult.globalErrors.firstOrNull()?.defaultMessage
            ?: ErrorType.INVALID_REQUEST.message
    }
}
