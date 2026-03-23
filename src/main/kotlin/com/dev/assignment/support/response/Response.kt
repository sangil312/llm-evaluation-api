package com.dev.assignment.support.response

import com.dev.assignment.support.error.ErrorMessage
import com.dev.assignment.support.error.ErrorType

data class Response<T>(
    val result: ResultType,
    val data: T? = null,
    val error: ErrorMessage? = null
) {
    companion object {
        fun success(): Response<Any> {
            return Response(ResultType.SUCCESS, null, null)
        }

        fun <T> success(data: T): Response<T> {
            return Response(ResultType.SUCCESS, data, null)
        }

        fun <T> error(error: ErrorType): Response<T> {
            return Response(ResultType.ERROR, null, ErrorMessage(error))
        }

        fun <T> error(error: ErrorType, message: String?): Response<T> {
            if (message.isNullOrBlank()) {
                return error(error)
            }
            return Response(ResultType.ERROR, null, ErrorMessage(error, message))
        }
    }
}
