package com.dev.assignment.support.error

data class ErrorMessage private constructor(
    val code: String,
    val message: String
) {
    constructor(errorType: ErrorType): this(
        code = errorType.name,
        message = errorType.message
    )

    constructor(errorType: ErrorType, message: String): this(
        code = errorType.name,
        message = message
    )
}