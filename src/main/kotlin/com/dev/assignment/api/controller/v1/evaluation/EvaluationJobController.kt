package com.dev.assignment.api.controller.v1.evaluation

import com.dev.assignment.api.controller.config.ErrorDocumented
import com.dev.assignment.api.controller.v1.evaluation.request.CreateEvaluationJobRequest
import com.dev.assignment.api.controller.v1.evaluation.response.CreateEvaluationJobResponse
import com.dev.assignment.api.controller.v1.evaluation.response.EvaluationResultResponse
import com.dev.assignment.api.controller.v1.evaluation.usecase.EvaluationJobUseCase
import com.dev.assignment.support.error.ErrorType.ALREADY_CREATED_EVALUATION
import com.dev.assignment.support.error.ErrorType.INVALID_MODEL
import com.dev.assignment.support.error.ErrorType.NOT_FINISHED_DATASET
import com.dev.assignment.support.error.ErrorType.NOT_FOUND_DATA
import com.dev.assignment.support.response.Response
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Evaluation Job API")
class EvaluationJobController(
    private val evaluationJobUseCase: EvaluationJobUseCase
) {
    @PostMapping("/v1/evaluation-jobs")
    @Operation(summary = "데이터셋 평가 작업 등록")
    @ErrorDocumented(NOT_FOUND_DATA, NOT_FINISHED_DATASET, INVALID_MODEL, ALREADY_CREATED_EVALUATION)
    fun createEvaluationJob(
        @Valid @RequestBody request: CreateEvaluationJobRequest
    ): Response<CreateEvaluationJobResponse> {
        val response = evaluationJobUseCase.createEvaluationJob(request.datasetId, request.modelName)
        return Response.success(response)
    }

    @GetMapping("/v1/evaluation-jobs/{evaluationJobId}")
    @Operation(summary = "데이터셋 평가 작업 결과 목록 조회")
    fun findEvaluationJobs(
        @PathVariable evaluationJobId: Long,
        pageable: Pageable
    ): Response<EvaluationResultResponse> {
        val response = evaluationJobUseCase.findEvaluationResults(evaluationJobId, pageable)
        return Response.success(response)
    }
}
