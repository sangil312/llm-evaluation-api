package com.dev.evaluation.client.evaluation

data class EvaluationModelRequest(
    val model: String,
    val messages: List<EvaluationModelRequestMessage>
)

data class EvaluationModelRequestMessage(
    val role: String,
    val content: String
)
