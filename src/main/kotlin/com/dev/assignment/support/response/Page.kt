package com.dev.assignment.support.response

data class Page<T>(
    val content: List<T>,
    val hasNext: Boolean,
)
