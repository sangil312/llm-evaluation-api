package com.dev.assignment.support.error

class CoreException(
    val errorType: ErrorType
) : RuntimeException(errorType.message)
