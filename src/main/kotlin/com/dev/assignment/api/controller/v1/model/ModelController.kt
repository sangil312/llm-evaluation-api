package com.dev.assignment.api.controller.v1.model

import com.dev.assignment.api.controller.config.ErrorDocumented
import com.dev.assignment.api.controller.v1.model.request.CreateModelRequest
import com.dev.assignment.api.controller.v1.model.request.UpdateModelRequest
import com.dev.assignment.api.controller.v1.model.response.ModelResponse
import com.dev.assignment.support.response.Response
import com.dev.assignment.service.model.ModelService
import com.dev.assignment.support.error.ErrorType.DUPLICATED_MODEL
import com.dev.assignment.support.error.ErrorType.NOT_FOUND_DATA
import com.dev.assignment.support.response.Page
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Model API")
class ModelController(
    private val modelService: ModelService
) {
    @PostMapping("/v1/models")
    @Operation(summary = "모델 등록")
    @ErrorDocumented(DUPLICATED_MODEL)
    fun createModel(
        @Valid @RequestBody request: CreateModelRequest
    ): Response<ModelResponse> {
        val model = modelService.createModel(request.toCreateModel())
        return Response.success(ModelResponse.of(model))
    }

    @GetMapping("/v1/models/{modelId}")
    @Operation(summary = "모델 조회")
    @ErrorDocumented(NOT_FOUND_DATA)
    fun findModel(
        @PathVariable modelId: Long
    ): Response<ModelResponse> {
        val model = modelService.findModel(modelId)
        return Response.success(ModelResponse.of(model))
    }

    @GetMapping("/v1/models")
    @Operation(summary = "모델 목록 조회")
    fun findModels(
        pageable: Pageable
    ): Response<Page<ModelResponse>> {
        val models = modelService.findModels(pageable)
        return Response.success(
            Page(ModelResponse.of(models.content), models.hasNext)
        )
    }

    @PutMapping("/v1/models/{modelId}")
    @Operation(summary = "모델 수정")
    @ErrorDocumented(NOT_FOUND_DATA)
    fun updateModel(
        @PathVariable modelId: Long,
        @Valid @RequestBody request: UpdateModelRequest
    ): Response<Any> {
        modelService.updateModel(request.toUpdateModel(modelId))
        return Response.success()
    }

    @DeleteMapping("/v1/models/{modelId}")
    @Operation(summary = "모델 삭제")
    fun deleteModel(
        @PathVariable modelId: Long
    ): Response<Any> {
        modelService.deleteModel(modelId)
        return Response.success()
    }
}