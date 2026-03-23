package com.dev.assignment.api.controller.config

import com.dev.assignment.support.error.ErrorType

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ErrorDocumented(vararg val value: ErrorType)