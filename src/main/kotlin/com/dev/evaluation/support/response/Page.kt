package com.dev.evaluation.support.response

data class Page<T>(
    val content: List<T>,
    val hasNext: Boolean,
)
