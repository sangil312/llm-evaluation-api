package com.dev.evaluation.support.error

class CoreException(
    val errorType: ErrorType
) : RuntimeException(errorType.message)
