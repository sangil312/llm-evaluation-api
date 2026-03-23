package com.dev.assignment.client.evaluation

data class EvaluationModelResponse(
    val choices: List<EvaluationModelResponseChoice>
)

data class EvaluationModelResponseChoice(
    val message: EvaluationModelResponseMessage
)

data class EvaluationModelResponseMessage(
    val role: String,
    val content: String
)
