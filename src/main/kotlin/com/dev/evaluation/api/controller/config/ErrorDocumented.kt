package com.dev.evaluation.api.controller.config

import com.dev.evaluation.support.error.ErrorType

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ErrorDocumented(vararg val value: ErrorType)