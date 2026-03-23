package com.dev.assignment.api.controller.v1.model.request

import com.dev.assignment.service.model.request.UpdateModel
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateModelRequest(
    @field:NotBlank(message = "필수 값을 입력해주세요.")
    @field:Size(min = 3, max = 20, message = "이름은 3자 이상 20자 이하로 입력해주세요.")
    val name: String,

    val description: String? = null,

    @field:NotBlank(message = "필수 값을 입력해주세요.")
    val apiUrl: String,
) {
    fun toUpdateModel(modelId: Long): UpdateModel {
        return UpdateModel(
            modelId = modelId,
            name = name,
            description = description ?: "",
            apiUrl = apiUrl.trim()
        )
    }
}
