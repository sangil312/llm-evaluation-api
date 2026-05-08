package com.dev.evaluation.service.model.request

data class UpdateModel(
    val modelId: Long,
    val name: String,
    val description: String,
    val apiUrl: String,
)