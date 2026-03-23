package com.dev.assignment.api.controller.v1.evaluation.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class CreateEvaluationJobRequest(
    @field:Positive(message = "필수 값을 입력해주세요.")
    val datasetId: Long,

    @field:NotBlank(message = "필수 값을 입력해주세요.")
    val modelName: String,
)
