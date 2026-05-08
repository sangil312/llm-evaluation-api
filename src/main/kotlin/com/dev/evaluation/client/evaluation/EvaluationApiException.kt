package com.dev.evaluation.client.evaluation

class EvaluationApiException(
    val responseBody: String
) : RuntimeException(responseBody)
