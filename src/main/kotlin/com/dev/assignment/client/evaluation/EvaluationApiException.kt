package com.dev.assignment.client.evaluation

class EvaluationApiException(
    val responseBody: String
) : RuntimeException(responseBody)
