package com.dev.assignment.api.controller.v1.model.response

import com.dev.assignment.domain.model.Model

data class ModelResponse(
    val name: String,
    val description: String,
    val apiUrl: String,
) {
    companion object {
        fun of(model: Model): ModelResponse {
            return ModelResponse(
                name = model.name,
                description = model.description,
                apiUrl = model.apiUrl,
            )
        }

        fun of(models: List<Model>): List<ModelResponse> {
            return models.map { of(it) }
        }
    }
}
