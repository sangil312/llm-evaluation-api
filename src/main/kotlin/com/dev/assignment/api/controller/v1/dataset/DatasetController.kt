package com.dev.assignment.api.controller.v1.dataset

import com.dev.assignment.api.controller.v1.dataset.response.CreateDatasetResponse
import com.dev.assignment.service.dataset.DatasetService
import com.dev.assignment.support.response.Response
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@Tag(name = "Dataset API")
class DatasetController(
    private val datasetService: DatasetService
) {
    @PostMapping(value = ["/v1/datasets/upload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "데이터셋 생성 csv 파일 업로드")
    fun createDataset(
        @RequestParam modelId: Long,
        @RequestParam file: MultipartFile
    ): Response<CreateDatasetResponse> {
        val datasetId = datasetService.createDataset(modelId, file)
        return Response.success(CreateDatasetResponse(datasetId))
    }
}
